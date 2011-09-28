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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.server.contexts.NamespaceManager;

public class QueryBuilder<T extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final StringBuilder sql = new StringBuilder();

    private final StringBuilder joinWhereSql = new StringBuilder();

    private final boolean multitenant;

    private final List<Object> bindParams = new Vector<Object>();

    private final String mainTableSqlAlias;

    private final Map<String, String> memberJoinAliases = new HashMap<String, String>();

    private final EntityOperationsMeta operationsMeta;

    private final Dialect dialect;

    public QueryBuilder(Dialect dialect, String alias, EntityMeta entityMeta, EntityOperationsMeta operationsMeta, EntityQueryCriteria<T> criteria) {
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

            for (Criterion cr : criteria.getFilters()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sql.append(" AND ");
                }
                if (cr instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) cr;

                    ObjectClassType objectClassType = ObjectClassType.Primitive;
                    MemeberWithAlias memeberWithAlias = null;
                    String memberPersistenceName = propertyCriterion.getPropertyName();
                    String pkPath = GWTJava5Helper.getSimpleName(entityMeta.getEntityClass()) + Path.PATH_SEPARATOR + IEntity.PRIMARY_KEY + Path.PATH_SEPARATOR;
                    if (pkPath.equals(propertyCriterion.getPropertyName()) || IEntity.PRIMARY_KEY.equals(propertyCriterion.getPropertyName())) {
                        memberPersistenceName = IEntity.PRIMARY_KEY;
                    } else if (!propertyCriterion.getPropertyName().endsWith(IndexAdapter.SECONDARY_PRROPERTY_SUFIX)) {
                        memeberWithAlias = getMemberOperationsMetaByPath(alias, propertyCriterion.getPropertyName());
                        if (memeberWithAlias == null) {
                            throw new RuntimeException("Unknown member " + propertyCriterion.getPropertyName() + " in " + entityMeta.getEntityClass().getName());
                        }
                        objectClassType = memeberWithAlias.memberOper.getMemberMeta().getObjectClassType();
                        memberPersistenceName = memeberWithAlias.memberOper.sqlName();
                    }
                    switch (objectClassType) {
                    case EntityList:
                    case EntitySet:
                        String memberJoinAlias = getJoin(memeberWithAlias.memberOper, memeberWithAlias.alias);
                        sql.append(memberJoinAlias).append(".value ");
                        break;
                    default:
                        if (memeberWithAlias == null) {
                            sql.append(alias).append('.');
                        } else {
                            sql.append(memeberWithAlias.alias).append('.');
                        }
                        sql.append(dialect.getNamingConvention().sqlFieldName(memberPersistenceName));
                    }

                    if (valueIsNull(propertyCriterion.getValue())) {
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
                        Serializable bindValue = propertyCriterion.getValue();
                        switch (propertyCriterion.getRestriction()) {
                        case LESS_THAN:
                            sql.append(" < ? ");
                            break;
                        case LESS_THAN_OR_EQUAL:
                            sql.append(" <= ? ");
                            break;
                        case GREATER_THAN:
                            sql.append(" > ? ");
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            sql.append(" >= ? ");
                            break;
                        case EQUAL:
                            sql.append(" = ? ");
                            break;
                        case NOT_EQUAL:
                            sql.append(" != ? ");
                            break;
                        case IN:
                            sql.append(" IN (");
                            Collection<?> items;
                            if (bindValue.getClass().isArray()) {
                                items = Arrays.asList((Object[]) bindValue);
                            } else if (bindValue instanceof Collection) {
                                items = (Collection<?>) bindValue;
                            } else {
                                throw new RuntimeException("Unsupported Type for IN " + bindValue.getClass().getName());
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
                            continue;
                        case RDB_LIKE:
                            if (bindValue != null) {
                                if (hasLikeValue(bindValue.toString())) {
                                    bindValue = bindValue.toString().replace('*', dialect.likeWildCards());
                                } else {
                                    bindValue = dialect.likeWildCards() + bindValue.toString() + dialect.likeWildCards();
                                }
                            }
                            sql.append(" LIKE ? ");
                            break;
                        default:
                            throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction());
                        }
                        bindParams.add(bindValue);
                    }
                }
            }
        }
        if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
            log.debug("sort by {}", criteria.getSorts());
            StringBuilder sortsSql = new StringBuilder();
            sortsSql.append(" ORDER BY ");
            boolean firstOrderBy = true;
            for (EntityQueryCriteria.Sort sort : criteria.getSorts()) {
                if (firstOrderBy) {
                    firstOrderBy = false;
                } else {
                    sortsSql.append(", ");
                }
                MemeberWithAlias descr = getMemberOperationsMetaByPath(alias, sort.getPropertyName());
                if (descr != null) {
                    sortsSql.append(descr.alias).append('.');
                    sortsSql.append(descr.memberOper.sqlName());
                } else {
                    // Assume proper SQL string supplied ???
                    //TODO verify sql INJECTION
                    sortsSql.append(alias).append('.');
                    sortsSql.append(sort.getPropertyName());
                }
                sortsSql.append(' ').append(sort.isDescending() ? "DESC" : "ASC");
            }

            sql.append(sortsSql);
        }
    }

    private static boolean hasLikeValue(String value) {
        return value.contains("*");
    }

    private static class MemeberWithAlias {

        MemberOperationsMeta memberOper;

        String alias;

        public MemeberWithAlias(MemberOperationsMeta memberOper, String alias) {
            this.memberOper = memberOper;
            this.alias = alias;
        }
    }

    private MemeberWithAlias getMemberOperationsMetaByPath(String mainAlias, String propertyPath) {
        MemberOperationsMeta memberOper = operationsMeta.getMember(propertyPath);
        if (memberOper != null) {
            return new MemeberWithAlias(memberOper, mainAlias);
        } else {
            memberOper = operationsMeta.getFirstDirectMember(propertyPath);
            if (memberOper != null) {
                String thisAlias = getJoin(memberOper, mainAlias);

                //TODO recursion
                @SuppressWarnings("unchecked")
                EntityOperationsMeta otherEntityOperMeta = operationsMeta.getMappedOperationsMeta((Class<? extends IEntity>) memberOper.getMemberMeta()
                        .getObjectClass());

                String pathFragmet = propertyPath.substring(memberOper.getMemberPath().length());
                MemberOperationsMeta memberOper2 = otherEntityOperMeta.getMember(GWTJava5Helper.getSimpleName(memberOper.getMemberMeta().getObjectClass())
                        + Path.PATH_SEPARATOR + pathFragmet);

                return new MemeberWithAlias(memberOper2, thisAlias);
            } else {
                return null;
            }
        }
    }

    private String getJoin(MemberOperationsMeta memberOper, String mainAlias) {
        String memberJoinAlias = memberJoinAliases.get(memberOper.getMemberPath());
        if (memberJoinAlias == null) {
            memberJoinAlias = "j" + String.valueOf(memberJoinAliases.size() + 1);
            memberJoinAliases.put(memberOper.getMemberPath(), memberJoinAlias);

            if (joinWhereSql.length() > 0) {
                joinWhereSql.append(" AND ");
            }

            if (memberOper.getMemberMeta().getObjectClassType() == ObjectClassType.Entity) {
                joinWhereSql.append(mainAlias).append(".").append(memberOper.sqlName());
                joinWhereSql.append(" = ");
                joinWhereSql.append(memberJoinAlias).append(".id ");
            } else {
                // Collection
                joinWhereSql.append(mainAlias).append(".id = ");
                joinWhereSql.append(memberJoinAlias).append(".owner ");
            }
        }
        return memberJoinAlias;
    }

    private boolean valueIsNull(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof IEntity) {
            return (((IEntity) value).getPrimaryKey() == null);
        } else {
            return false;
        }
    }

    String getSQL(String mainTableSqlName) {
        return getJoins(mainTableSqlName) + getWhere();
    }

    @SuppressWarnings("unchecked")
    private String getJoins(String mainTableSqlName) {
        StringBuilder sqlFrom = new StringBuilder();
        sqlFrom.append(mainTableSqlName).append(' ').append(mainTableSqlAlias);
        for (Map.Entry<String, String> me : memberJoinAliases.entrySet()) {
            sqlFrom.append(", ");

            MemberOperationsMeta memberOper = operationsMeta.getMember(me.getKey());
            if (memberOper.getMemberMeta().getObjectClassType() == ObjectClassType.Entity) {
                sqlFrom.append(TableModel.getTableName(dialect,
                        EntityFactory.getEntityMeta((Class<? extends IEntity>) memberOper.getMemberMeta().getObjectClass())));
            } else {
                // Collections
                sqlFrom.append(memberOper.sqlName());
            }

            sqlFrom.append(" ");
            // AS
            sqlFrom.append(me.getValue());
        }
        return sqlFrom.toString();
    }

    public String getMainTableSqlAlias() {
        return mainTableSqlAlias;
    }

    private String getWhere() {
        if ((sql.length() == 0) && (joinWhereSql.length() == 0)) {
            return "";
        } else {
            StringBuilder sqlWhere = new StringBuilder(" WHERE ");
            sqlWhere.append(joinWhereSql);
            if ((sql.length() != 0) && (joinWhereSql.length() != 0)) {
                sqlWhere.append(" AND ");
            }
            sqlWhere.append(sql);
            return sqlWhere.toString();
        }
    }

//    String getWhere(String conditions) {
//        if (sql.length() == 0) {
//            return " WHERE " + conditions;
//        } else {
//            return " WHERE " + conditions + " AND " + sql.toString();
//        }
//    }

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
            stmt.setObject(parameterIndex, encodeValue(param));
            parameterIndex++;
        }
        return parameterIndex;
    }
}
