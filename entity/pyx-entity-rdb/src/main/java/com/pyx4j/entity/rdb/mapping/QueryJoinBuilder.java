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

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

class QueryJoinBuilder {

    private final Connection connection;

    private final Dialect dialect;

    private final Mappings mappings;

    final EntityOperationsMeta operationsMeta;

    private final String mainTableSqlAlias;

    private static class JoinDef {

        String alias;

        boolean leftJoin;

        String condition;

        String sqlTableName;

        JoinDef(String alias, boolean leftJoin) {
            this.alias = alias;
            this.leftJoin = leftJoin;
        }

    }

    //keep the keys in the order they were inserted.
    private final Map<String, JoinDef> memberJoinAliases = new LinkedHashMap<String, JoinDef>();

    QueryJoinBuilder(Connection connection, Dialect dialect, Mappings mappings, EntityOperationsMeta operationsMeta, String mainTableSqlAlias) {
        this.connection = connection;
        this.dialect = dialect;
        this.mappings = mappings;
        this.operationsMeta = operationsMeta;
        this.mainTableSqlAlias = mainTableSqlAlias;
    }

    QueryMember buildQueryMember(String propertyPath, boolean leftJoin) {
        return getMemberOperationsMetaByPath(operationsMeta, mainTableSqlAlias, propertyPath, leftJoin);
    }

    private QueryMember getMemberOperationsMetaByPath(EntityOperationsMeta fromEntityOperMeta, String fromAlias, String propertyPath, boolean leftJoin) {
        MemberOperationsMeta memberOper = fromEntityOperMeta.getMember(propertyPath);
        if (memberOper != null) {
            if (memberOper instanceof MemberExternalOperationsMeta) {
                return new QueryMember(getJoin(memberOper, fromAlias, leftJoin).alias, memberOper);
            } else {
                return new QueryMember(fromAlias, memberOper);
            }
        } else {
            memberOper = fromEntityOperMeta.getFirstDirectMember(propertyPath);
            if (memberOper == null) {
                return null;
            }
            JoinDef thisJoin = getJoin(memberOper, fromAlias, leftJoin);
            // This will fix OR criteria
            if (thisJoin.leftJoin) {
                leftJoin = true;
            }
            @SuppressWarnings("unchecked")
            Class<? extends IEntity> memeberEntityClass = (Class<? extends IEntity>) memberOper.getMemberMeta().getValueClass();
            EntityOperationsMeta otherEntityOperMeta = mappings.getEntityOperationsMeta(connection, memeberEntityClass);
            if ((memberOper instanceof MemberCollectionOperationsMeta) && (!((MemberExternalOperationsMeta) memberOper).isJoinTableSameAsTarget())) {
                thisJoin = getJoinCollectionMemeber((MemberCollectionOperationsMeta) memberOper, otherEntityOperMeta, thisJoin.alias, leftJoin);
                // This will fix OR criteria
                if (thisJoin.leftJoin) {
                    leftJoin = true;
                }
            }
            String pathFragmet = propertyPath.substring(memberOper.getMemberPath().length());
            if (pathFragmet.startsWith(Path.COLLECTION_SEPARATOR)) {
                pathFragmet = pathFragmet.substring(Path.COLLECTION_SEPARATOR.length() + 1);
            }
            String shorterPath = GWTJava5Helper.getSimpleName(memberOper.getMemberMeta().getValueClass()) + Path.PATH_SEPARATOR + pathFragmet;
            return getMemberOperationsMetaByPath(otherEntityOperMeta, thisJoin.alias, shorterPath, leftJoin);
        }
    }

    private JoinDef getMemberJoin(String path) {
        return memberJoinAliases.get(path);
    }

    private void putMemberJoin(String path, JoinDef memberJoin) {
        memberJoinAliases.put(path, memberJoin);
    }

    private JoinDef getJoinCollectionMemeber(MemberCollectionOperationsMeta memberOper, EntityOperationsMeta memeberEntityMeta, String fromAlias,
            boolean leftJoin) {
        String path = memberOper.getMemberPath() + Path.COLLECTION_SEPARATOR;
        JoinDef memberJoin = getMemberJoin(path);
        if (memberJoin == null) {
            memberJoin = new JoinDef("j" + String.valueOf(memberJoinAliases.size() + 1), leftJoin);
            memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(memeberEntityMeta.entityMeta().getEntityClass()));

            putMemberJoin(path, memberJoin);

            StringBuilder condition = new StringBuilder();

            condition.append(memberJoin.alias).append(".").append("id");
            condition.append(" = ");
            condition.append(fromAlias).append(".").append(((MemberExternalOperationsMeta) (memberOper)).sqlValueName());

            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    private JoinDef getJoin(MemberOperationsMeta memberOper, String fromAlias, boolean leftJoin) {
        JoinDef memberJoin = getMemberJoin(memberOper.getMemberPath());
        if (memberJoin == null) {
            memberJoin = new JoinDef("j" + String.valueOf(memberJoinAliases.size() + 1), leftJoin);

            if (memberOper instanceof MemberExternalOperationsMeta) {
                memberJoin.sqlTableName = memberOper.sqlName();
            } else {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> entityClass = (Class<IEntity>) memberOper.getMemberMeta().getValueClass();
                memberJoin.sqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(entityClass));
            }

            putMemberJoin(memberOper.getMemberPath(), memberJoin);

            StringBuilder condition = new StringBuilder();

            if (memberOper instanceof MemberExternalOperationsMeta) {
                // Collection or join
                condition.append(memberJoin.alias).append(".").append(((MemberExternalOperationsMeta) (memberOper)).sqlOwnerName());
                condition.append(" = ");
                condition.append(fromAlias).append(".id");
            } else {
                condition.append(fromAlias).append(".").append(memberOper.sqlName());
                condition.append(" = ");
                condition.append(memberJoin.alias).append(".id");
            }

            memberJoin.condition = condition.toString();
        }
        return memberJoin;
    }

    void appendJoins(StringBuilder sql) {
        for (Map.Entry<String, JoinDef> me : memberJoinAliases.entrySet()) {
            JoinDef memberJoin = me.getValue();

            if (memberJoin.leftJoin) {
                sql.append(" LEFT JOIN ");
            } else {
                sql.append(" INNER JOIN ");
            }

            sql.append(memberJoin.sqlTableName);

            sql.append(" ");
            // AS
            sql.append(memberJoin.alias);
            sql.append(" ON ").append(memberJoin.condition);
        }
    }
}
