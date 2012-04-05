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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.server.contexts.NamespaceManager;

public class QueryBuilder<T extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final Dialect dialect;

    private final StringBuilder sql = new StringBuilder();

    private final StringBuilder sortsSql = new StringBuilder();

    private final List<Object> bindParams = new Vector<Object>();

    private final String mainTableSqlAlias;

    private static class BindHolder {

        Object bindValue;

        ValueBindAdapter adapter;

    }

    QueryJoinBuilder queryJoin;

    private boolean sortAddDistinct = false;

    public QueryBuilder(PersistenceContext persistenceContext, Mappings mappings, String alias, EntityOperationsMeta operationsMeta,
            EntityQueryCriteria<T> criteria) {
        this.dialect = persistenceContext.getDialect();
        this.mainTableSqlAlias = alias;

        this.queryJoin = new QueryJoinBuilder(persistenceContext, mappings, operationsMeta, alias, criteria.getVersionedCriteria());

        boolean firstCriteria = true;
        if (dialect.isMultitenant()) {
            sql.append(alias).append('.').append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            firstCriteria = false;
        }

        if (IVersionedEntity.class.isAssignableFrom(operationsMeta.entityMeta().getEntityClass())) {
            IVersionedEntity<?> versionedProto = (IVersionedEntity<?>) criteria.proto();
            switch (criteria.getVersionedCriteria()) {
            case onlyFinalized:
                appendPropertyCriterion(PropertyCriterion.isNotNull(versionedProto.version().fromDate()), firstCriteria);
                firstCriteria = false;
                appendPropertyCriterion(PropertyCriterion.isNull(versionedProto.version().toDate()), firstCriteria);
                break;
            case onlyDraft:
                appendPropertyCriterion(PropertyCriterion.isNull(versionedProto.version().fromDate()), firstCriteria);
                firstCriteria = false;
                appendPropertyCriterion(PropertyCriterion.isNull(versionedProto.version().toDate()), firstCriteria);
                break;
            case finalizedAsOfNow:
                appendPropertyCriterion(PropertyCriterion.isNotNull(versionedProto.version()), firstCriteria);
                firstCriteria = false;
                break;
            default:
                throw new Error("Unsupported VersionedCriteria " + criteria.getVersionedCriteria());
            }
        }

        if ((criteria.getFilters() != null) && (!criteria.getFilters().isEmpty())) {
            appendFilters(criteria.getFilters(), firstCriteria);
        }
        if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
            log.debug("sort by {}", criteria.getSorts());
            sortsSql.append(" ORDER BY ");
            boolean firstOrderBy = true;
            for (EntityQueryCriteria.Sort sort : expandToStringMembers(criteria.getSorts())) {
                if (firstOrderBy) {
                    firstOrderBy = false;
                } else {
                    sortsSql.append(", ");
                }
                QueryMember queryMember = queryJoin.buildQueryMember(sort.getPropertyPath(), true);
                if (queryMember == null) {
                    throw new RuntimeException("Unknown member " + sort.getPropertyPath() + " in " + operationsMeta.entityMeta().getEntityClass().getName());
                }
                sortsSql.append(queryMember.memberSqlName);

                sortsSql.append(' ').append(sort.isDescending() ? "DESC" : "ASC");
                // TODO Make it configurable in API
                sortsSql.append(dialect.sqlSortNulls(sort.isDescending()));
            }
        }
    }

    private static boolean hasLikeValue(String value) {
        return value.contains("*");
    }

    private void appendFilters(List<Criterion> filters, boolean firstInSentence) {
        for (Criterion cr : filters) {
            if (firstInSentence) {
                firstInSentence = false;
            } else {
                sql.append(" AND ");
            }
            if (cr instanceof PropertyCriterion) {
                appendPropertyCriterion((PropertyCriterion) cr);
            } else if (cr instanceof OrCriterion) {
                sql.append(" (( ");
                appendFilters(((OrCriterion) cr).getFiltersLeft(), true);
                sql.append(" ) OR ( ");
                appendFilters(((OrCriterion) cr).getFiltersRight(), true);
                sql.append(" )) ");
            } else {
                throw new RuntimeException("Unsupported Operator " + cr.getClass());
            }
        }
    }

    private void appendPropertyCriterion(PropertyCriterion propertyCriterion, boolean firstInSentence) {
        if (firstInSentence) {
            firstInSentence = false;
        } else {
            sql.append(" AND ");
        }
        appendPropertyCriterion(propertyCriterion);
    }

    private void appendPropertyCriterion(PropertyCriterion propertyCriterion) {
        BindHolder bindHolder = new BindHolder();
        bindHolder.bindValue = propertyCriterion.getValue();

        String secondPersistenceName = null;
        if (propertyCriterion.getPropertyPath().endsWith(IndexAdapter.SECONDARY_PRROPERTY_SUFIX)) {
            // TODO create index binders and value adapters
            sql.append(mainTableSqlAlias).append('.').append(dialect.getNamingConvention().sqlFieldName(propertyCriterion.getPropertyPath()));
        } else {
            boolean leftJoin = false;
            // "LEFT JOIN / IS NULL" works as "NOT EXISTS", make the LEFT join
            if ((bindHolder.bindValue == null) && (propertyCriterion.getRestriction() == Restriction.EQUAL)) {
                leftJoin = true;
            }
            QueryMember queryMember = queryJoin.buildQueryMember(propertyCriterion.getPropertyPath(), leftJoin);
            if (queryMember == null) {
                throw new RuntimeException("Unknown member " + propertyCriterion.getPropertyPath() + " in "
                        + queryJoin.operationsMeta.entityMeta().getEntityClass().getName());
            }
            bindHolder.adapter = queryMember.memberOper.getValueAdapter().getQueryValueBindAdapter(propertyCriterion.getRestriction(), bindHolder.bindValue);

            // TODO P3. support more then two columns
            boolean firstValue = true;
            for (String name : bindHolder.adapter.getColumnNames(queryMember.memberSqlName)) {
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

    private List<Sort> expandToStringMembers(List<Sort> sorts) {
        List<Sort> result = new ArrayList<Sort>();
        for (Sort sort : sorts) {
            Path path = new Path(sort.getPropertyPath());
            MemberMeta memberMeta = queryJoin.operationsMeta.entityMeta().getMemberMeta(path);
            ObjectClassType type = memberMeta.getObjectClassType();
            if ((type == ObjectClassType.Entity) || (type == ObjectClassType.EntityList) || (type == ObjectClassType.EntitySet)) {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> targetEntityClass = (Class<? extends IEntity>) memberMeta.getValueClass();
                List<Sort> expanded = expandEntityToStringMembers(sort.getPropertyPath(), targetEntityClass, sort.isDescending());
                if (expanded.size() > 0) {
                    result.addAll(expanded);
                } else {
                    result.add(sort);
                }
            } else {
                result.add(sort);
            }

            if (path.isUndefinedCollectionPath() || (sort.getPropertyPath().endsWith(Path.COLLECTION_SEPARATOR + Path.PATH_SEPARATOR))) {
                sortAddDistinct = true;
            }

        }
        return result;
    }

    private List<Sort> expandEntityToStringMembers(String propertyPath, Class<? extends IEntity> targetEntityClass, boolean descending) {
        List<Sort> result = new ArrayList<Sort>();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(targetEntityClass);
        for (String sortMemberName : entityMeta.getToStringMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(sortMemberName);
            ToString memberColumn = memberMeta.getAnnotation(ToString.class);
            if ((memberColumn != null) && (memberColumn.sortable() == false)) {
                continue;
            }
            ObjectClassType type = memberMeta.getObjectClassType();
            if ((type == ObjectClassType.Entity) || (type == ObjectClassType.EntityList) || (type == ObjectClassType.EntitySet)) {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> childEntityClass = (Class<? extends IEntity>) memberMeta.getValueClass();
                result.addAll(expandEntityToStringMembers(propertyPath + sortMemberName + Path.PATH_SEPARATOR, childEntityClass, descending));
            } else {
                result.add(new Sort(propertyPath + sortMemberName + Path.PATH_SEPARATOR, descending));
            }
        }
        return result;
    }

    boolean addDistinct() {
        return sortAddDistinct;
    }

    String getSQL(String mainTableSqlName) {
        return getJoins(mainTableSqlName) + getWhere() + getSorts();
    }

    private String getJoins(String mainTableSqlName) {
        StringBuilder sqlFrom = new StringBuilder();
        sqlFrom.append(mainTableSqlName).append(' ').append(mainTableSqlAlias);
        queryJoin.appendJoins(sqlFrom);
        return sqlFrom.toString();
    }

    public String getMainTableSqlAlias() {
        return mainTableSqlAlias;
    }

    private String getWhere() {
        if (sql.length() == 0) {
            return "";
        } else {
            StringBuilder sqlWhere = new StringBuilder("\n WHERE ");
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

    int bindParameters(PersistenceContext persistenceContext, PreparedStatement stmt) throws SQLException {
        int parameterIndex = 1;
        parameterIndex = this.queryJoin.bindParameters(parameterIndex, persistenceContext, stmt);
        if (dialect.isMultitenant()) {
            stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            parameterIndex++;
        }
        for (Object param : bindParams) {
            if (param instanceof BindHolder) {
                if (((BindHolder) param).adapter != null) {
                    parameterIndex += ((BindHolder) param).adapter.bindValue(persistenceContext, stmt, parameterIndex, ((BindHolder) param).bindValue);
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
