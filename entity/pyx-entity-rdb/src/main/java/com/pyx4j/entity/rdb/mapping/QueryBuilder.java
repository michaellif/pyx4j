/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.server.contexts.NamespaceManager;

public class QueryBuilder<T extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final StringBuilder sql = new StringBuilder();

    private final StringBuilder sortsSql = new StringBuilder();

    private final boolean multitenant;

    private final List<Object> bindParams = new Vector<Object>();

    private final String mainTableSqlAlias;

    private static class JoinDef {

        String alias;

        boolean leftJoin;

        String condition;

        MemberOperationsMeta memberOper;

    }

    //keep the keys in the order they were inserted.
    private final Map<String, JoinDef> memberJoinAliases = new LinkedHashMap<String, JoinDef>();

    private static class MemeberWithAlias {

        MemberOperationsMeta memberOper;

        String alias;

        public MemeberWithAlias(MemberOperationsMeta memberOper, String alias) {
            this.memberOper = memberOper;
            this.alias = alias;
        }

        @Override
        public String toString() {
            return memberOper.toString() + " " + alias;
        }
    }

    private static class BindHolder {

        Object bindValue;

        ValueBindAdapter adapter;

    }

    private final EntityOperationsMeta operationsMeta;

    private final Connection connection;

    private final Dialect dialect;

    public QueryBuilder(Connection connection, Dialect dialect, String alias, EntityMeta entityMeta, EntityOperationsMeta operationsMeta,
            EntityQueryCriteria<T> criteria) {
        this.connection = connection;
        this.dialect = dialect;
        this.mainTableSqlAlias = alias;
        this.operationsMeta = operationsMeta;
        this.multitenant = dialect.isMultitenant();
        boolean firstCriteria = true;
        if (multitenant) {
            sql.append(alias).append(".ns = ?");
            firstCriteria = false;
        }
        if ((criteria.getFilters() != null) && (!criteria.getFilters().isEmpty())) {
            appendFilters(entityMeta, criteria.getFilters(), firstCriteria);
        }
        if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
            log.debug("sort by {}", criteria.getSorts());
            sortsSql.append(" ORDER BY ");
            boolean firstOrderBy = true;
            for (EntityQueryCriteria.Sort sort : criteria.getSorts()) {
                if (firstOrderBy) {
                    firstOrderBy = false;
                } else {
                    sortsSql.append(", ");
                }
                MemeberWithAlias descr = getMemberOperationsMetaByPath(alias, sort.getPropertyName(), true);
                if (descr == null) {
                    throw new RuntimeException("Unknown member " + sort.getPropertyName() + " in " + entityMeta.getEntityClass().getName());
                }
                sortsSql.append(descr.alias).append('.');
                sortsSql.append(descr.memberOper.sqlName());
                sortsSql.append(' ').append(sort.isDescending() ? "DESC" : "ASC");
                // TODO Make it configurable in API
                sortsSql.append(dialect.sqlSortNulls(sort.isDescending()));
            }
        }
    }

    private static boolean hasLikeValue(String value) {
        return value.contains("*");
    }

    private void appendFilters(EntityMeta entityMeta, List<Criterion> filters, boolean firstInSentence) {
        for (Criterion cr : filters) {
            if (firstInSentence) {
                firstInSentence = false;
            } else {
                sql.append(" AND ");
            }
            if (cr instanceof PropertyCriterion) {
                appendPropertyCriterion(entityMeta, (PropertyCriterion) cr);
            } else if (cr instanceof OrCriterion) {
                sql.append(" (( ");
                appendFilters(entityMeta, ((OrCriterion) cr).getFiltersLeft(), true);
                sql.append(" ) OR ( ");
                appendFilters(entityMeta, ((OrCriterion) cr).getFiltersRight(), true);
                sql.append(" )) ");
            } else {
                throw new RuntimeException("Unsupported Operator " + cr.getClass());
            }
        }
    }

    private void appendPropertyCriterion(EntityMeta entityMeta, PropertyCriterion propertyCriterion) {
        ObjectClassType objectClassType = ObjectClassType.Primitive;
        MemeberWithAlias memeberWithAlias = null;
        BindHolder bindHolder = new BindHolder();
        bindHolder.bindValue = propertyCriterion.getValue();

        String secondPersistenceName = null;
        if (propertyCriterion.getPropertyName().endsWith(IndexAdapter.SECONDARY_PRROPERTY_SUFIX)) {
            // TODO create index binders and value adapters
            sql.append(mainTableSqlAlias).append('.').append(dialect.getNamingConvention().sqlFieldName(propertyCriterion.getPropertyName()));
        } else {
            memeberWithAlias = getMemberOperationsMetaByPath(mainTableSqlAlias, propertyCriterion.getPropertyName(), false);
            if (memeberWithAlias == null) {
                throw new RuntimeException("Unknown member " + propertyCriterion.getPropertyName() + " in " + entityMeta.getEntityClass().getName());
            }
            objectClassType = memeberWithAlias.memberOper.getMemberMeta().getObjectClassType();
            bindHolder.adapter = memeberWithAlias.memberOper.getValueAdapter().getQueryValueBindAdapter(propertyCriterion.getRestriction(),
                    bindHolder.bindValue);

            String memberSqlNameBase;

            switch (objectClassType) {
            case EntityList:
            case EntitySet:
                String memberJoinAlias = getJoin(memeberWithAlias.memberOper, memeberWithAlias.alias, false);
                memberSqlNameBase = memberJoinAlias + ".value";
                break;
            default:
                memberSqlNameBase = memeberWithAlias.alias + "." + memeberWithAlias.memberOper.sqlName();
            }

            // TODO support more then two columns
            boolean firstValue = true;
            for (String name : bindHolder.adapter.getColumnNames(memberSqlNameBase)) {
                if (firstValue) {
                    sql.append(name);
                    firstValue = false;
                } else {
                    secondPersistenceName = name;
                }
            }

        }

        if (bindHolder.bindValue == null) {
            switch (propertyCriterion.getRestriction()) {
            case EQUAL:
                sql.append(" IS NULL ");
                break;
            case NOT_EQUAL:
                sql.append(" IS NOT NULL ");
                break;
            default:
                throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction() + " for NULL value");
            }
        } else {
            String sqlOperator;
            switch (propertyCriterion.getRestriction()) {
            case LESS_THAN:
                sqlOperator = " < ? ";
                break;
            case LESS_THAN_OR_EQUAL:
                sqlOperator = " <= ? ";
                break;
            case GREATER_THAN:
                sqlOperator = " > ? ";
                break;
            case GREATER_THAN_OR_EQUAL:
                sqlOperator = " >= ? ";
                break;
            case EQUAL:
                sqlOperator = " = ? ";
                break;
            case NOT_EQUAL:
                sqlOperator = " != ? ";
                break;
            case IN:
                sql.append(" IN (");
                Collection<?> items;
                if (bindHolder.bindValue.getClass().isArray()) {
                    items = Arrays.asList((Object[]) bindHolder.bindValue);
                } else if (bindHolder.bindValue instanceof Collection) {
                    items = (Collection<?>) bindHolder.bindValue;
                } else {
                    throw new RuntimeException("Unsupported Type for IN " + bindHolder.bindValue.getClass().getName());
                }
                boolean first = true;
                for (Object i : items) {
                    if (first) {
                        first = false;
                    } else {
                        sql.append(",");
                    }
                    sql.append(" ? ");
                    bindParams.add(i);
                }
                sql.append(")");
                return;
            case RDB_LIKE:
                if (bindHolder.bindValue != null) {
                    if (hasLikeValue(bindHolder.bindValue.toString())) {
                        bindHolder.bindValue = bindHolder.bindValue.toString().replace('*', dialect.likeWildCards());
                    } else {
                        bindHolder.bindValue = dialect.likeWildCards() + bindHolder.bindValue.toString() + dialect.likeWildCards();
                    }
                }
                sqlOperator = " LIKE ? ";
                break;
            default:
                throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction());
            }

            sql.append(sqlOperator);

            if (secondPersistenceName != null) {
                sql.append(" AND ").append(secondPersistenceName).append(sqlOperator);
            }

            bindParams.add(bindHolder);
        }
    }

    private MemeberWithAlias getMemberOperationsMetaByPath(String mainAlias, String propertyPath, boolean leftJoin) {
        return getMemberOperationsMetaByPath(operationsMeta, mainAlias, propertyPath, leftJoin);
    }

    private MemeberWithAlias getMemberOperationsMetaByPath(EntityOperationsMeta fromEntityOperMeta, String fromAlias, String propertyPath, boolean leftJoin) {
        MemberOperationsMeta memberOper = fromEntityOperMeta.getMember(propertyPath);
        if (memberOper != null) {
            return new MemeberWithAlias(memberOper, fromAlias);
        } else {
            memberOper = fromEntityOperMeta.getFirstDirectMember(propertyPath);
            if (memberOper == null) {
                return null;
            }
            String thisAlias = getJoin(memberOper, fromAlias, leftJoin);
            @SuppressWarnings("unchecked")
            EntityOperationsMeta otherEntityOperMeta = operationsMeta.getMappedOperationsMeta(connection, (Class<? extends IEntity>) memberOper.getMemberMeta()
                    .getObjectClass());
            String pathFragmet = propertyPath.substring(memberOper.getMemberPath().length());
            String shorterPath = GWTJava5Helper.getSimpleName(memberOper.getMemberMeta().getObjectClass()) + Path.PATH_SEPARATOR + pathFragmet;
            return getMemberOperationsMetaByPath(otherEntityOperMeta, thisAlias, shorterPath, leftJoin);
        }
    }

    private String getJoin(MemberOperationsMeta memberOper, String fromAlias, boolean leftJoin) {
        JoinDef memberJoin = memberJoinAliases.get(memberOper.getMemberPath());
        if (memberJoin == null) {
            memberJoin = new JoinDef();
            memberJoin.alias = "j" + String.valueOf(memberJoinAliases.size() + 1);
            memberJoin.leftJoin = leftJoin;
            memberJoin.memberOper = memberOper;
            memberJoinAliases.put(memberOper.getMemberPath(), memberJoin);

            StringBuilder condition = new StringBuilder();

            if (memberOper.getMemberMeta().getObjectClassType() == ObjectClassType.Entity) {
                condition.append(fromAlias).append(".").append(memberOper.sqlName());
                condition.append(" = ");
                condition.append(memberJoin.alias).append(".id ");
            } else {
                // Collection
                condition.append(fromAlias).append(".id = ");
                condition.append(memberJoin.alias).append(".owner ");
            }

            memberJoin.condition = condition.toString();
        }
        return memberJoin.alias;
    }

    String getSQL(String mainTableSqlName) {
        return getJoins(mainTableSqlName) + getWhere() + getSorts();
    }

    @SuppressWarnings("unchecked")
    private String getJoins(String mainTableSqlName) {
        StringBuilder sqlFrom = new StringBuilder();
        sqlFrom.append(mainTableSqlName).append(' ').append(mainTableSqlAlias);
        for (Map.Entry<String, JoinDef> me : memberJoinAliases.entrySet()) {
            JoinDef memberJoin = me.getValue();

            if (memberJoin.leftJoin) {
                sqlFrom.append(" LEFT JOIN ");
            } else {
                sqlFrom.append(" INNER JOIN ");
            }

            MemberOperationsMeta memberOper = memberJoin.memberOper;
            if (memberOper.getMemberMeta().getObjectClassType() == ObjectClassType.Entity) {
                sqlFrom.append(TableModel.getTableName(dialect,
                        EntityFactory.getEntityMeta((Class<? extends IEntity>) memberOper.getMemberMeta().getObjectClass())));
            } else {
                // Collections
                sqlFrom.append(memberOper.sqlName());
            }

            sqlFrom.append(" ");
            // AS
            sqlFrom.append(memberJoin.alias);
            sqlFrom.append(" ON ").append(memberJoin.condition);
        }
        return sqlFrom.toString();
    }

    public String getMainTableSqlAlias() {
        return mainTableSqlAlias;
    }

    private String getWhere() {
        if (sql.length() == 0) {
            return "";
        } else {
            StringBuilder sqlWhere = new StringBuilder(" WHERE ");
            sqlWhere.append(sql);
            return sqlWhere.toString();
        }
    }

    private String getSorts() {
        return sortsSql.toString();
    }

    static Object encodeValue(Object value) {
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        } else if (value instanceof IEntity) {
            return ((IEntity) value).getPrimaryKey().asLong();
        } else if (value instanceof Key) {
            return ((Key) value).asLong();
        } else if (value instanceof java.util.Date) {
            Calendar c = new GregorianCalendar();
            c.setTime((java.util.Date) value);
            // DB does not store Milliseconds
            c.set(Calendar.MILLISECOND, 0);
            return new java.sql.Timestamp(c.getTimeInMillis());
        } else {
            return value;
        }
    }

    int bindParameters(PreparedStatement stmt) throws SQLException {
        int parameterIndex = 1;
        if (multitenant) {
            stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            parameterIndex++;
        }
        for (Object param : bindParams) {
            if (param instanceof BindHolder) {
                if (((BindHolder) param).adapter != null) {
                    parameterIndex += ((BindHolder) param).adapter.bindValue(stmt, parameterIndex, ((BindHolder) param).bindValue);
                } else {
                    stmt.setObject(parameterIndex, encodeValue(((BindHolder) param).bindValue));
                    parameterIndex++;
                }
            } else {
                stmt.setObject(parameterIndex, encodeValue(param));
                parameterIndex++;
            }
        }
        return parameterIndex;
    }
}
