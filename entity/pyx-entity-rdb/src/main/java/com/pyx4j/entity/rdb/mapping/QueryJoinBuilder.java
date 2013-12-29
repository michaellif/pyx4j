/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 23, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.server.contexts.NamespaceManager;

class QueryJoinBuilder {

    protected final PersistenceContext persistenceContext;

    protected final Dialect dialect;

    protected final Mappings mappings;

    protected EntityOperationsMeta operationsMeta;

    protected final String mainTableSqlAlias;

    protected final VersionedCriteria versionedCriteria;

    static class JoinDef {

        MemberOperationsMeta memberOper;

        String alias;

        boolean leftJoin;

        String condition;

        String sqlTableName;

        JoinDef(MemberOperationsMeta memberOper, String alias, boolean leftJoin) {
            this.memberOper = memberOper;
            this.alias = alias;
            this.leftJoin = leftJoin;
        }

        @Override
        public String toString() {
            StringBuilder sql = new StringBuilder();
            if (leftJoin) {
                sql.append(" LEFT JOIN ");
            } else {
                sql.append(" INNER JOIN ");
            }

            sql.append(sqlTableName);

            sql.append(' ');
            // AS
            sql.append(alias);
            sql.append(" ON ").append(condition);
            return sql.toString();
        }

    }

    //keep the keys in the order they were inserted.
    private final Map<String, JoinDef> memberJoinAliases = new LinkedHashMap<String, JoinDef>();

    private int nextJoinAliaseId = 0;

    private int nowParameters = 0;

    boolean addDistinct = false;

    QueryJoinBuilder(PersistenceContext persistenceContext, Mappings mappings, EntityOperationsMeta operationsMeta, String mainTableSqlAlias,
            VersionedCriteria versionedCriteria) {
        this.persistenceContext = persistenceContext;
        this.dialect = persistenceContext.getDialect();
        this.mappings = mappings;
        this.operationsMeta = operationsMeta;
        this.mainTableSqlAlias = mainTableSqlAlias;
        this.versionedCriteria = versionedCriteria;
    }

    QueryMember buildQueryMember(String propertyPath, boolean leftJoin, boolean usedInSort) {
        JoinDef join = buildJoin(null, operationsMeta, mainTableSqlAlias, propertyPath, leftJoin, usedInSort, false);
        if (join != null) {
            return new QueryMember(join.alias, join.memberOper);
        } else {
            return null;
        }
    }

    protected JoinDef buildJoin(String accessPath, EntityOperationsMeta fromEntityOperMeta, String fromAlias, String propertyPath, boolean leftJoin,
            boolean usedInSort, boolean targetSubQueryMainTable) {

        if (accessPath == null) {
            // root
            accessPath = propertyPath.substring(0, propertyPath.indexOf(Path.PATH_SEPARATOR) + 1);
        }

        MemberOperationsMeta memberOper = fromEntityOperMeta.getMember(propertyPath);
        if (memberOper != null) {
            if ((memberOper instanceof MemberCollectionOperationsMeta) && (!((MemberCollectionOperationsMeta) memberOper).isJoinTableSameAsTarget())) {
                // Relationship is managed via joinTable
                return createJoinBackReference(accessPath, (MemberCollectionOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort);
            } else if (memberOper instanceof MemberExternalOperationsMeta) {
                // Relationship is managed in CHILD table using PARENT column
                return createJoinBackReference(accessPath, (MemberExternalOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort);
            } else {
                // Relationship is managed in PARENT table using CHILD column.
                if (!targetSubQueryMainTable) {
                    // Not actually a join
                    return new JoinDef(memberOper, fromAlias, leftJoin);
                } else {
                    return createDirectJoin(accessPath, memberOper, fromAlias, leftJoin);
                }
            }
        } else {
            // Find path to this member
            return buildRecurciveJoin(accessPath, fromEntityOperMeta, fromAlias, propertyPath, leftJoin, usedInSort, targetSubQueryMainTable);
        }
    }

    private JoinDef buildRecurciveJoin(String accessPath, EntityOperationsMeta fromEntityOperMeta, String fromAlias, String propertyPath, boolean leftJoin,
            boolean usedInSort, boolean targetSubQueryMainTable) {
        MemberOperationsMeta memberOper = fromEntityOperMeta.getFirstDirectMember(propertyPath);
        if (memberOper == null) {
            return null;
        }
        JoinDef join;
        if (memberOper instanceof MemberExternalOperationsMeta) {
            join = createExternalJoin(accessPath, (MemberExternalOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort);
        } else {
            join = createDirectJoin(accessPath, memberOper, fromAlias, leftJoin);
        }
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> targetEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
        EntityOperationsMeta targetEntityOperationsMeta = mappings.getEntityOperationsMeta(persistenceContext, targetEntityClass);

        accessPath += memberOper.getMemberPath().substring(memberOper.getMemberPath().indexOf(Path.PATH_SEPARATOR) + 1);

        String pathFragmet = propertyPath.substring(memberOper.getMemberPath().length());
        if (pathFragmet.startsWith(Path.COLLECTION_SEPARATOR)) {
            pathFragmet = pathFragmet.substring(Path.COLLECTION_SEPARATOR.length() + 1);
            accessPath += Path.COLLECTION_SEPARATOR + Path.PATH_SEPARATOR;
        }
        String shorterPath = GWTJava5Helper.getSimpleName(targetEntityClass) + Path.PATH_SEPARATOR + pathFragmet;

        return buildJoin(accessPath, targetEntityOperationsMeta, join.alias, shorterPath, join.leftJoin, usedInSort, targetSubQueryMainTable);
    }

    protected JoinDef getMemberJoin(String accessPath, String path) {
        return memberJoinAliases.get(path);
    }

    protected Collection<JoinDef> getMemberJoinAliases() {
        return memberJoinAliases.values();
    }

    protected void putMemberJoin(String accessPath, String path, JoinDef memberJoin) {
        memberJoinAliases.put(path, memberJoin);
    }

    protected String nextJoinAliaseId() {
        return String.valueOf(++nextJoinAliaseId);
    }

    private JoinDef createDirectJoin(String accessPath, MemberOperationsMeta memberOper, String fromAlias, boolean leftJoin) {
        JoinDef memberJoin = getMemberJoin(accessPath, memberOper.getMemberPath());
        if (memberJoin == null) {
            memberJoin = new JoinDef(memberOper, "jd" + nextJoinAliaseId(), leftJoin);

            @SuppressWarnings("unchecked")
            Class<? extends IEntity> entityClass = (Class<IEntity>) memberOper.getMemberMeta().getValueClass();
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(entityClass));

            putMemberJoin(accessPath, memberOper.getMemberPath(), memberJoin);

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());
            condition.append(" = ");
            condition.append(fromAlias).append('.').append(memberOper.sqlName());
            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef createExternalJoin(String accessPath, MemberExternalOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        if ((memberOper instanceof MemberCollectionOperationsMeta) && (!memberOper.isJoinTableSameAsTarget())) {
            return createJoinViaJoinTable(accessPath, (MemberCollectionOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort);
        } else if (!memberOper.isJoinTableSameAsTarget()) {
            return createJoinViaBackReference(accessPath, memberOper, fromAlias, leftJoin, usedInSort);
        } else {
            return createJoinBackReference(accessPath, memberOper, fromAlias, leftJoin, usedInSort);
        }
    }

    private JoinDef createJoinBackReference(String accessPath, MemberExternalOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        JoinDef memberJoin = getMemberJoin(accessPath, memberOper.getMemberPath());
        if (memberJoin == null) {
            memberJoin = new JoinDef(memberOper, "jbr" + nextJoinAliaseId(), leftJoin);

            putMemberJoin(accessPath, memberOper.getMemberPath(), memberJoin);

            memberJoin.sqlTableName = memberOper.sqlName();

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(memberOper.sqlOwnerName());
            condition.append(" = ");
            condition.append(fromAlias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());

            if (memberOper.hasChildJoinContition()) {
                condition.append(" AND (");
                condition.append(memberJoin.alias).append('.').append(memberOper.getSqlChildJoinContition());
                condition.append(")");
            }

            if (memberOper instanceof MemberVersionDataOperationsMeta) {
                MemberVersionDataOperationsMeta memberVersionDataOper = (MemberVersionDataOperationsMeta) memberOper;
                condition.append(" AND (");
                switch (versionedCriteria) {
                case onlyFinalized:
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlFromDateColumnName()).append(" IS NOT NULL");
                    condition.append(" AND ");
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlToDateColumnName()).append(" IS NULL");
                    break;
                case onlyDraft:
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlFromDateColumnName()).append(" IS NULL");
                    condition.append(" AND ");
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlToDateColumnName()).append(" IS NULL");
                    break;
                case finalizedAsOfNow:
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlFromDateColumnName()).append(" <= ?");
                    condition.append(" AND (");
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlToDateColumnName()).append(" > ?");
                    condition.append(" OR ");
                    condition.append(memberJoin.alias).append('.').append(memberVersionDataOper.getSqlToDateColumnName()).append(" IS NULL");
                    condition.append(")");
                    nowParameters += 2;
                    break;
                default:
                    throw new Error("Unsupported VersionedCriteria " + versionedCriteria);
                }
                condition.append(")");

            }

            if (memberOper instanceof MemberCollectionOperationsMeta) {
                addDistinct = true;
            }

            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef createJoinViaJoinTable(String accessPath, MemberCollectionOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        JoinDef collectionJoin = createJoinBackReference(accessPath, memberOper, fromAlias, leftJoin, usedInSort);
        String path = memberOper.getMemberPath() + Path.COLLECTION_SEPARATOR;
        JoinDef memberJoin = getMemberJoin(accessPath, path);
        if (memberJoin == null) {
            memberJoin = new JoinDef(memberOper, "jc" + nextJoinAliaseId(), collectionJoin.leftJoin);
            putMemberJoin(accessPath, path, memberJoin);

            @SuppressWarnings("unchecked")
            Class<? extends IEntity> memberEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(memberEntityClass));

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());
            condition.append(" = ");
            condition.append(collectionJoin.alias).append('.').append(memberOper.sqlValueName());

            addDistinct = true;

            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef createJoinViaBackReference(String accessPath, MemberExternalOperationsMeta memberOper, String fromAlias, boolean leftJoin,
            boolean usedInSort) {
        JoinDef collectionJoin = createJoinBackReference(accessPath, memberOper, fromAlias, leftJoin, usedInSort);

        String path = memberOper.getMemberPath() + '&';
        JoinDef memberJoin = getMemberJoin(accessPath, path);
        if (memberJoin == null) {
            memberJoin = new JoinDef(memberOper, "jr" + nextJoinAliaseId(), collectionJoin.leftJoin);
            putMemberJoin(accessPath, path, memberJoin);

            @SuppressWarnings("unchecked")
            Class<? extends IEntity> memberEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(memberEntityClass));

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());
            condition.append(" = ");
            condition.append(collectionJoin.alias).append('.').append(memberOper.sqlValueName());
            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    protected boolean appenFromJoin(JoinDef memberJoin) {
        return true;
    }

    void appendJoins(StringBuilder sql) {
        for (JoinDef memberJoin : getMemberJoinAliases()) {
            if (appenFromJoin(memberJoin)) {
                sql.append('\n');
                if (memberJoin.leftJoin) {
                    sql.append(" LEFT JOIN ");
                } else {
                    sql.append(" INNER JOIN ");
                }
                if (dialect.isMultitenantSeparateSchemas()) {
                    assert NamespaceManager.getNamespace() != null : "Namespace is required";
                    sql.append(NamespaceManager.getNamespace()).append('.');
                }
                sql.append(memberJoin.sqlTableName);

                sql.append(' ');
                // AS
                sql.append(memberJoin.alias);
                sql.append(" ON ").append(memberJoin.condition);
            }
        }
    }

    int bindParameters(int parameterIndex, PersistenceContext persistenceContext, PreparedStatement stmt) throws SQLException {
        if (nowParameters > 0) {
            Date forDate = persistenceContext.getTimeNow();
            Calendar c = new GregorianCalendar();
            c.setTime(forDate);
            // DB does not store Milliseconds
            c.set(Calendar.MILLISECOND, 0);
            for (int i = 0; i < nowParameters; i++) {
                stmt.setTimestamp(parameterIndex, new java.sql.Timestamp(c.getTimeInMillis()));
                parameterIndex++;
            }
        }
        return parameterIndex;
    }
}
