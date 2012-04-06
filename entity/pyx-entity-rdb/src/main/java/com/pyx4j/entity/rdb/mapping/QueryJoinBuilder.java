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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;

class QueryJoinBuilder {

    private final PersistenceContext persistenceContext;

    private final Dialect dialect;

    private final Mappings mappings;

    final EntityOperationsMeta operationsMeta;

    private final String mainTableSqlAlias;

    private final VersionedCriteria versionedCriteria;

    private static class JoinDef {

        String alias;

        boolean leftJoin;

        String condition;

        String sqlTableName;

        JoinDef(String alias, boolean leftJoin) {
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

    private int nowParameters = 0;

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
        return buildJoin(operationsMeta, mainTableSqlAlias, propertyPath, leftJoin, usedInSort);
    }

    private QueryMember buildJoin(EntityOperationsMeta fromEntityOperMeta, String fromAlias, String propertyPath, boolean leftJoin, boolean usedInSort) {
        MemberOperationsMeta memberOper = fromEntityOperMeta.getMember(propertyPath);
        if (memberOper != null) {
            if ((memberOper instanceof MemberCollectionOperationsMeta) && (!((MemberCollectionOperationsMeta) memberOper).isJoinTableSameAsTarget())) {
                // Relationship is managed via joinTable
                return new QueryMember(createJoinBackReference((MemberCollectionOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort).alias, memberOper);
            } else if (memberOper instanceof MemberExternalOperationsMeta) {
                // Relationship is managed in CHILD table using PARENT column
                return new QueryMember(createJoinBackReference((MemberExternalOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort).alias, memberOper);
            } else {
                // Relationship is managed in PARENT table using CHILD column.
                return new QueryMember(fromAlias, memberOper);
            }
        } else {
            // Find path to this member
            return buildRecurciveJoin(fromEntityOperMeta, fromAlias, propertyPath, leftJoin, usedInSort);
        }
    }

    private QueryMember buildRecurciveJoin(EntityOperationsMeta fromEntityOperMeta, String fromAlias, String propertyPath, boolean leftJoin, boolean usedInSort) {
        MemberOperationsMeta memberOper = fromEntityOperMeta.getFirstDirectMember(propertyPath);
        if (memberOper == null) {
            return null;
        }
        JoinDef join;
        if (memberOper instanceof MemberExternalOperationsMeta) {
            join = createExternalJoin((MemberExternalOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort);
        } else {
            join = createDirectJoin(memberOper, fromAlias, leftJoin);
        }
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> targetEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
        EntityOperationsMeta targetEntityOperationsMeta = mappings.getEntityOperationsMeta(persistenceContext.getConnection(), targetEntityClass);

        String pathFragmet = propertyPath.substring(memberOper.getMemberPath().length());
        if (pathFragmet.startsWith(Path.COLLECTION_SEPARATOR)) {
            pathFragmet = pathFragmet.substring(Path.COLLECTION_SEPARATOR.length() + 1);
        }
        String shorterPath = GWTJava5Helper.getSimpleName(targetEntityClass) + Path.PATH_SEPARATOR + pathFragmet;

        return buildJoin(targetEntityOperationsMeta, join.alias, shorterPath, join.leftJoin, usedInSort);
    }

    private JoinDef getMemberJoin(String path) {
        return memberJoinAliases.get(path);
    }

    private void putMemberJoin(String path, JoinDef memberJoin) {
        memberJoinAliases.put(path, memberJoin);
    }

    private JoinDef createDirectJoin(MemberOperationsMeta memberOper, String fromAlias, boolean leftJoin) {
        JoinDef memberJoin = getMemberJoin(memberOper.getMemberPath());
        if (memberJoin == null) {
            memberJoin = new JoinDef("jd" + String.valueOf(memberJoinAliases.size() + 1), leftJoin);

            @SuppressWarnings("unchecked")
            Class<? extends IEntity> entityClass = (Class<IEntity>) memberOper.getMemberMeta().getValueClass();
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(entityClass));

            putMemberJoin(memberOper.getMemberPath(), memberJoin);

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());
            condition.append(" = ");
            condition.append(fromAlias).append('.').append(memberOper.sqlName());
            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef createExternalJoin(MemberExternalOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        if ((memberOper instanceof MemberCollectionOperationsMeta) && (!memberOper.isJoinTableSameAsTarget())) {
            return createJoinViaJoinTable((MemberCollectionOperationsMeta) memberOper, fromAlias, leftJoin, usedInSort);
        } else if (!memberOper.isJoinTableSameAsTarget()) {
            return createJoinViaBackReference(memberOper, fromAlias, leftJoin, usedInSort);
        } else {
            return createJoinBackReference(memberOper, fromAlias, leftJoin, usedInSort);
        }
    }

    private JoinDef createJoinBackReference(MemberExternalOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        JoinDef memberJoin = getMemberJoin(memberOper.getMemberPath());
        if (memberJoin == null) {
            memberJoin = new JoinDef("jbr" + String.valueOf(memberJoinAliases.size() + 1), leftJoin);
            putMemberJoin(memberOper.getMemberPath(), memberJoin);

            memberJoin.sqlTableName = memberOper.sqlName();

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(memberOper.sqlOwnerName());
            condition.append(" = ");
            condition.append(fromAlias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());

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
            if (usedInSort && (memberOper instanceof MemberCollectionOperationsMeta)) {
                MemberCollectionOperationsMeta memberCollectionDataOper = (MemberCollectionOperationsMeta) memberOper;
                condition.append(" AND ");
                condition.append(memberJoin.alias).append('.').append(memberCollectionDataOper.sqlOrderColumnName());
                condition.append(" = ");
                condition.append(" 0 ");
            }

            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef createJoinViaJoinTable(MemberCollectionOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        JoinDef collectionJoin = createJoinBackReference(memberOper, fromAlias, leftJoin, usedInSort);

        String path = memberOper.getMemberPath() + Path.COLLECTION_SEPARATOR;
        JoinDef memberJoin = getMemberJoin(path);
        if (memberJoin == null) {
            memberJoin = new JoinDef("jc" + String.valueOf(memberJoinAliases.size() + 1), collectionJoin.leftJoin);
            putMemberJoin(path, memberJoin);

            @SuppressWarnings("unchecked")
            Class<? extends IEntity> memeberEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(memeberEntityClass));

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());
            condition.append(" = ");
            condition.append(collectionJoin.alias).append('.').append(memberOper.sqlValueName());

            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef createJoinViaBackReference(MemberExternalOperationsMeta memberOper, String fromAlias, boolean leftJoin, boolean usedInSort) {
        JoinDef collectionJoin = createJoinBackReference(memberOper, fromAlias, leftJoin, usedInSort);

        String path = memberOper.getMemberPath() + '&';
        JoinDef memberJoin = getMemberJoin(path);
        if (memberJoin == null) {
            memberJoin = new JoinDef("jr" + String.valueOf(memberJoinAliases.size() + 1), collectionJoin.leftJoin);
            putMemberJoin(path, memberJoin);

            @SuppressWarnings("unchecked")
            Class<? extends IEntity> memeberEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(memeberEntityClass));

            StringBuilder condition = new StringBuilder();
            condition.append(memberJoin.alias).append('.').append(dialect.getNamingConvention().sqlIdColumnName());
            condition.append(" = ");
            condition.append(collectionJoin.alias).append('.').append(memberOper.sqlValueName());
            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    void appendJoins(StringBuilder sql) {
        for (Map.Entry<String, JoinDef> me : memberJoinAliases.entrySet()) {
            JoinDef memberJoin = me.getValue();
            sql.append('\n');
            if (memberJoin.leftJoin) {
                sql.append(" LEFT JOIN ");
            } else {
                sql.append(" INNER JOIN ");
            }

            sql.append(memberJoin.sqlTableName);

            sql.append(' ');
            // AS
            sql.append(memberJoin.alias);
            sql.append(" ON ").append(memberJoin.condition);
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
