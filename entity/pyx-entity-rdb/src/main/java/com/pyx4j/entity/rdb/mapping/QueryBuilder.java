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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.adapters.IndexAdapter;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.mapping.QueryJoinBuilder.JoinDef;
import com.pyx4j.server.contexts.NamespaceManager;

public class QueryBuilder<T extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final Dialect dialect;

    private final StringBuilder sql = new StringBuilder();

    private final StringBuilder sortsSql = new StringBuilder();

    private final Set<String> selectColumnsSqlNames = new HashSet<String>();

    private final List<BindHolder> bindParams = new Vector<BindHolder>();

    private final String mainTableSqlAlias;

    private static class BindHolder {

        Object bindValue;

        ValueBindAdapter adapter;

    }

    QueryJoinBuilder queryJoin;

    public QueryBuilder(PersistenceContext persistenceContext, Mappings mappings, String alias, EntityOperationsMeta operationsMeta,
            EntityQueryCriteria<T> criteria) {
        this.dialect = persistenceContext.getDialect();
        this.mainTableSqlAlias = alias;

        this.queryJoin = new QueryJoinBuilder(persistenceContext, mappings, operationsMeta, alias, criteria.getVersionedCriteria());

        boolean firstCriteria = true;
        if (dialect.isMultitenantSharedSchema()) {
            sql.append(alias).append('.').append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            firstCriteria = false;
        }

        if (IVersionedEntity.class.isAssignableFrom(operationsMeta.entityMeta().getEntityClass())) {
            IVersionedEntity<?> versionedProto = (IVersionedEntity<?>) criteria.proto();
            switch (criteria.getVersionedCriteria()) {
            case onlyFinalized:
                appendPropertyCriterion(sql, queryJoin, PropertyCriterion.isNotNull(versionedProto.version().fromDate()), firstCriteria, true);
                firstCriteria = false;
                appendPropertyCriterion(sql, queryJoin, PropertyCriterion.isNull(versionedProto.version().toDate()), firstCriteria, true);
                break;
            case onlyDraft:
                appendPropertyCriterion(sql, queryJoin, PropertyCriterion.isNull(versionedProto.version().fromDate()), firstCriteria, true);
                firstCriteria = false;
                appendPropertyCriterion(sql, queryJoin, PropertyCriterion.isNull(versionedProto.version().toDate()), firstCriteria, true);
                break;
            case finalizedAsOfNow:
                appendPropertyCriterion(sql, queryJoin, PropertyCriterion.isNotNull(versionedProto.version()), firstCriteria, true);
                firstCriteria = false;
                break;
            default:
                throw new Error("Unsupported VersionedCriteria " + criteria.getVersionedCriteria());
            }
        }

        if (operationsMeta.impClasses != null) {
            appendPropertyCriterion(sql, queryJoin,
                    new PropertyCriterion(criteria.proto().instanceValueClass(), Restriction.IN, operationsMeta.impClasses.values()), firstCriteria, true);
            firstCriteria = false;
        }

        // Build JOIN for ORDER BY. This will not allow us to Use DISTINCT and add special criteria for collections
        if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
            for (EntityQueryCriteria.Sort sort : expandToStringMembers(criteria.getSorts())) {
                queryJoin.buildQueryMember(sort.getPropertyPath(), true, true);
            }
        }

        if ((criteria.getFilters() != null) && (!criteria.getFilters().isEmpty())) {
            appendFilters(sql, queryJoin, criteria.getFilters(), firstCriteria, true);
        }
        if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
            log.trace("sort by {}", criteria.getSorts());
            sortsSql.append(" ORDER BY ");
            boolean firstOrderBy = true;
            for (EntityQueryCriteria.Sort sort : expandToStringMembers(criteria.getSorts())) {
                if (firstOrderBy) {
                    firstOrderBy = false;
                } else {
                    sortsSql.append(", ");
                }
                QueryMember queryMember = queryJoin.buildQueryMember(sort.getPropertyPath(), true, true);
                if (queryMember == null) {
                    throw new RuntimeException("Unknown member " + sort.getPropertyPath() + " in " + operationsMeta.entityMeta().getEntityClass().getName());
                }
                String sqlName = queryMember.memberSqlName;
                if (queryMember.memberOper.getSortMemberOperationsMeta() != null) {
                    sqlName = queryMember.joinAlias + "." + queryMember.memberOper.getSortMemberOperationsMeta().sqlName();
                }

                sortsSql.append(sqlName);

                if (!queryMember.joinAlias.startsWith(mainTableSqlAlias)) {
                    selectColumnsSqlNames.add(sqlName);
                }

                sortsSql.append(' ').append(sort.isDescending() ? "DESC" : "ASC");
                // TODO Make it configurable in API
                sortsSql.append(dialect.sqlSortNulls(sort.isDescending()));
            }
        }
    }

    private static boolean hasLikeValue(String value) {
        return value.contains("*");
    }

    private void appendFilters(StringBuilder criterionSql, QueryJoinBuilder joinBuilder, List<Criterion> filters, boolean firstInSentence, boolean required) {
        for (Criterion criterion : filters) {
            if (firstInSentence) {
                firstInSentence = false;
            } else {
                criterionSql.append(" AND ");
            }
            appendCriterion(criterionSql, joinBuilder, criterion, required);
        }
    }

    private void appendCriterion(StringBuilder criterionSql, QueryJoinBuilder joinBuilder, Criterion criterion, boolean required) {
        if (criterion instanceof PropertyCriterion) {
            appendPropertyCriterion(criterionSql, joinBuilder, (PropertyCriterion) criterion, required);
        } else if (criterion instanceof AndCriterion) {
            criterionSql.append(" ( ");
            appendFilters(criterionSql, joinBuilder, ((AndCriterion) criterion).getFilters(), true, required);
            criterionSql.append(" ) ");
        } else if (criterion instanceof OrCriterion) {
            criterionSql.append(" (( ");
            appendFilters(criterionSql, joinBuilder, ((OrCriterion) criterion).getFiltersLeft(), true, false);
            criterionSql.append(" ) OR ( ");
            appendFilters(criterionSql, joinBuilder, ((OrCriterion) criterion).getFiltersRight(), true, false);
            criterionSql.append(" )) ");
        } else {
            throw new RuntimeException("Unsupported Operator " + criterion.getClass());
        }
    }

    private void appendPropertyCriterion(StringBuilder criterionSql, QueryJoinBuilder joinBuilder, PropertyCriterion propertyCriterion,
            boolean firstInSentence, boolean required) {
        if (firstInSentence) {
            firstInSentence = false;
        } else {
            criterionSql.append(" AND ");
        }
        appendPropertyCriterion(criterionSql, joinBuilder, propertyCriterion, required);
    }

    private void appendPropertyCriterion(StringBuilder criterionSql, QueryJoinBuilder joinBuilder, PropertyCriterion propertyCriterion, boolean required) {
        BindHolder bindHolder = new BindHolder();
        bindHolder.bindValue = propertyCriterion.getValue();

        String secondPersistenceName = null;
        if (propertyCriterion.getPropertyPath().endsWith(IndexAdapter.SECONDARY_PRROPERTY_SUFIX)) {
            // TODO create index binders and value adapters
            criterionSql.append(mainTableSqlAlias).append('.').append(dialect.getNamingConvention().sqlFieldName(propertyCriterion.getPropertyPath()));
        } else {
            boolean leftJoin = false;
            if (!required) {
                leftJoin = true;
            }
            // "LEFT JOIN / IS NULL" works as "NOT EXISTS", make the LEFT join
            if (propertyCriterion.getRestriction() == Restriction.NOT_EXISTS) {
                if (bindHolder.bindValue == null) {
                    leftJoin = true;
                } else if (bindHolder.bindValue instanceof Criterion) {
                    buildSubQuery(criterionSql, joinBuilder, propertyCriterion.getPropertyPath(), (Criterion) bindHolder.bindValue);
                    return;
                }
            }
            QueryMember queryMember = joinBuilder.buildQueryMember(propertyCriterion.getPropertyPath(), leftJoin, false);
            if (queryMember == null) {
                throw new RuntimeException("Unknown member " + propertyCriterion.getPropertyPath() + " in "
                        + joinBuilder.operationsMeta.entityMeta().getEntityClass().getName());
            }
            bindHolder.adapter = queryMember.memberOper.getValueAdapter().getQueryValueBindAdapter(propertyCriterion.getRestriction(), bindHolder.bindValue);

            // TODO P3. support more then two columns
            boolean firstValue = true;
            for (String name : bindHolder.adapter.getColumnNames(queryMember.memberSqlName)) {
                if (firstValue) {
                    criterionSql.append(name);
                    firstValue = false;
                } else {
                    secondPersistenceName = name;
                }
            }

        }

        if (bindHolder.bindValue instanceof Path) {
            switch (propertyCriterion.getRestriction()) {
            case LESS_THAN:
                criterionSql.append(" < ");
                break;
            case LESS_THAN_OR_EQUAL:
                criterionSql.append(" <= ");
                break;
            case GREATER_THAN:
                criterionSql.append(" > ");
                break;
            case GREATER_THAN_OR_EQUAL:
                criterionSql.append(" >= ");
                break;
            case EQUAL:
                criterionSql.append(" = ");
                break;
            case NOT_EQUAL:
                criterionSql.append(" != ");
                break;
            default:
                throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction() + " for PathReference");
            }

            String property2Path = bindHolder.bindValue.toString();

            boolean leftJoin = false;
            if (!required) {
                leftJoin = true;
            }
            QueryMember queryMember2 = joinBuilder.buildQueryMember(property2Path, leftJoin, false);
            if (queryMember2 == null) {
                throw new RuntimeException("Unknown member " + property2Path + " in " + joinBuilder.operationsMeta.entityMeta().getEntityClass().getName());
            }
            ValueBindAdapter adapter = queryMember2.memberOper.getValueAdapter().getQueryValueBindAdapter(propertyCriterion.getRestriction(),
                    bindHolder.bindValue);

            // TODO P3. support more then two columns
            boolean firstValue = true;
            for (String name : adapter.getColumnNames(queryMember2.memberSqlName)) {
                if (firstValue) {
                    criterionSql.append(name);
                    firstValue = false;
                } else {
                    secondPersistenceName = name;
                    throw new Error("TODO support more then two columns in value join");
                }
            }

        } else if (bindHolder.bindValue == null) {
            switch (propertyCriterion.getRestriction()) {
            case NOT_EXISTS:
            case EQUAL:
                criterionSql.append(" IS NULL ");
                break;
            case NOT_EQUAL:
                criterionSql.append(" IS NOT NULL ");
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
                criterionSql.append(" IN (");
                Collection<?> items;
                if (bindHolder.bindValue.getClass().isArray()) {
                    items = Arrays.asList((Object[]) bindHolder.bindValue);
                } else if (bindHolder.bindValue instanceof Collection) {
                    items = (Collection<?>) bindHolder.bindValue;
                } else {
                    throw new RuntimeException("Unsupported Type for IN " + bindHolder.bindValue.getClass().getName());
                }
                boolean first = true;
                for (Object item : items) {
                    if (first) {
                        first = false;
                    } else {
                        criterionSql.append(",");
                    }
                    criterionSql.append(" ? ");

                    BindHolder itemBindHolder = new BindHolder();
                    itemBindHolder.adapter = bindHolder.adapter;
                    itemBindHolder.bindValue = item;
                    bindParams.add(itemBindHolder);
                }
                criterionSql.append(")");
                return;
            case RDB_LIKE:
                if (bindHolder.bindValue != null) {
                    if (hasLikeValue(bindHolder.bindValue.toString())) {
                        bindHolder.bindValue = bindHolder.bindValue.toString().trim().replace('*', dialect.likeWildCards());
                    } else {
                        bindHolder.bindValue = dialect.likeWildCards() + bindHolder.bindValue.toString().trim() + dialect.likeWildCards();
                    }
                }
                sqlOperator = " " + dialect.likeOperator() + " ? ";
                break;
            default:
                throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction());
            }

            criterionSql.append(sqlOperator);

            if (secondPersistenceName != null) {
                criterionSql.append(" AND ").append(secondPersistenceName).append(sqlOperator);
            }

            bindParams.add(bindHolder);
        }
    }

    private void buildSubQuery(StringBuilder sql, QueryJoinBuilder joinBuilder, String propertyPath, Criterion criterion) {

        QueryJoinBuilderSubQuery subQueryJoinBuilder = new QueryJoinBuilderSubQuery(joinBuilder, propertyPath, true);
        JoinDef memberJoin = subQueryJoinBuilder.getMainTable();

        StringBuilder criterionSql = new StringBuilder();
        appendCriterion(criterionSql, subQueryJoinBuilder, criterion, true);

        StringBuilder subQuerySql = new StringBuilder();
        subQuerySql.append(" \n NOT EXISTS ( SELECT 1 FROM ");

        if (dialect.isMultitenantSeparateSchemas()) {
            assert NamespaceManager.getNamespace() != null : "Namespace is required";
            subQuerySql.append(NamespaceManager.getNamespace()).append('.');
        }
        subQuerySql.append(memberJoin.sqlTableName);
        subQuerySql.append(' ');
        // AS
        subQuerySql.append(memberJoin.alias);

        subQueryJoinBuilder.appendJoins(subQuerySql);

        subQuerySql.append(" \n WHERE ").append(memberJoin.condition);

        subQuerySql.append(" AND ");

        subQuerySql.append(criterionSql);

        subQuerySql.append(")\n");

        sql.append(subQuerySql);
    }

    private List<Sort> expandToStringMembers(List<Sort> sorts) {
        List<Sort> result = new ArrayList<Sort>();
        for (Sort sort : sorts) {
            Path path = new Path(sort.getPropertyPath());

            // Sort by collections is unsupported on postgresql
            if (path.isUndefinedCollectionPath() || (sort.getPropertyPath().endsWith(Path.COLLECTION_SEPARATOR + Path.PATH_SEPARATOR))) {
                throw new Error("Sort by collections is unsupported");
            }

            MemberMeta memberMeta = queryJoin.operationsMeta.entityMeta().getMemberMeta(path);
            ObjectClassType type = memberMeta.getObjectClassType();

            if ((type == ObjectClassType.EntityList) || (type == ObjectClassType.EntitySet)) {
                throw new Error("Sort by collections is unsupported");
            }

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

    public boolean addDistinct() {
        return queryJoin.addDistinct;
    }

    String getColumnsSQL() {
        StringBuilder sql = new StringBuilder();
        for (String sqlName : selectColumnsSqlNames) {
            sql.append(", ");
            sql.append(sqlName);
            sql.append(" c_");
            sql.append(sqlName.replace(".", "_"));
        }
        return sql.toString();
    }

    String getSQL(String mainTableSqlName) {
        return getJoins(mainTableSqlName) + getWhere() + getSorts();
    }

    String getUpdateSQL(String mainTableSqlName, String setExpression) {
        return getJoins(mainTableSqlName) + setExpression + getWhere() + getSorts();
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
        return bindParameters(1, persistenceContext, stmt);
    }

    int bindParameters(int parameterIndex, PersistenceContext persistenceContext, PreparedStatement stmt) throws SQLException {
        parameterIndex = this.queryJoin.bindParameters(parameterIndex, persistenceContext, stmt);
        if (dialect.isMultitenantSharedSchema()) {
            stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            parameterIndex++;
        }
        for (BindHolder param : bindParams) {
            if (param.adapter != null) {
                parameterIndex += param.adapter.bindValue(persistenceContext, stmt, parameterIndex, param.bindValue);
            } else {
                stmt.setObject(parameterIndex, encodeValue(param.bindValue));
                parameterIndex++;
            }
        }
        return parameterIndex;
    }
}
