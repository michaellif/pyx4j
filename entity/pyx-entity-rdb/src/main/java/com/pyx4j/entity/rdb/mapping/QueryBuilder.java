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
import java.util.Calendar;
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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.server.contexts.NamespaceManager;

public class QueryBuilder<T extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final StringBuilder sql = new StringBuilder();

    private final boolean multitenant;

    private final List<Object> bindParams = new Vector<Object>();

    private final String mainTableSqlAlias;

    private final Map<String, String> memberJoinAliases = new HashMap<String, String>();

    private final EntityOperationsMeta operationsMeta;

    public QueryBuilder(Dialect dialect, String alias, EntityMeta entityMeta, EntityOperationsMeta operationsMeta, EntityQueryCriteria<T> criteria) {
        this.mainTableSqlAlias = alias;
        this.operationsMeta = operationsMeta;
        this.multitenant = dialect.isMultitenant();
        if ((criteria.getFilters() != null) && (!criteria.getFilters().isEmpty())) {
            boolean firstCriteria = true;

            if (multitenant) {
                sql.append("ns = ?");
                firstCriteria = false;
            }

            for (Criterion cr : criteria.getFilters()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sql.append(" AND ");
                }
                if (cr instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) cr;

                    ObjectClassType objectClassType = ObjectClassType.Primitive;
                    MemberMeta memberMeta = null;
                    String memberPersistenceName = propertyCriterion.getPropertyName();
                    String pkPath = GWTJava5Helper.getSimpleName(entityMeta.getEntityClass()) + Path.PATH_SEPARATOR + IEntity.PRIMARY_KEY + Path.PATH_SEPARATOR;
                    if (pkPath.equals(propertyCriterion.getPropertyName()) || IEntity.PRIMARY_KEY.equals(propertyCriterion.getPropertyName())) {
                        memberPersistenceName = IEntity.PRIMARY_KEY;
                    } else if (!propertyCriterion.getPropertyName().endsWith(IndexAdapter.SECONDARY_PRROPERTY_SUFIX)) {
                        MemberOperationsMeta memberOper = operationsMeta.getMember(propertyCriterion.getPropertyName());
                        if (memberOper == null) {
                            throw new RuntimeException("Unknown member " + propertyCriterion.getPropertyName() + " in " + entityMeta.getEntityClass().getName());
                        }
                        memberMeta = memberOper.getMemberMeta();
                        objectClassType = memberMeta.getObjectClassType();
                        memberPersistenceName = memberOper.sqlName();
                    }
                    switch (objectClassType) {
                    case EntityList:
                    case EntitySet:
                        String memberJoinAlias = memberJoinAliases.get(memberMeta.getFieldName());
                        if (memberJoinAlias == null) {
                            memberJoinAlias = "c" + String.valueOf(memberJoinAliases.size() + 1);
                            memberJoinAliases.put(memberMeta.getFieldName(), memberJoinAlias);
                            sql.append(alias).append(".id = ");
                            sql.append(memberJoinAlias).append(".owner AND ");
                        }
                        sql.append(memberJoinAlias).append(".value ");
                        break;
                    default:
                        sql.append(alias).append('.');
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
                            sql.append(" IN ? ");
                            break;
                        case RDB_LIKE:
                            sql.append(" LIKE ? ");
                            break;
                        default:
                            throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction());
                        }
                        bindParams.add(propertyCriterion.getValue());
                    }
                }
            }
        }
        if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
            log.debug("sort by {}", criteria.getSorts());
            sql.append(" ORDER BY ");
            boolean firstOrderBy = true;
            for (EntityQueryCriteria.Sort sort : criteria.getSorts()) {
                if (firstOrderBy) {
                    firstOrderBy = false;
                } else {
                    sql.append(", ");
                }
                sql.append(alias).append('.');
                sql.append(sort.getPropertyName()).append(' ');
                sql.append(sort.isDescending() ? "DESC" : "ASC");
            }
        }
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

    String getJoins(String mainTableSqlName) {
        StringBuilder sqlFrom = new StringBuilder();
        sqlFrom.append(mainTableSqlName).append(' ').append(mainTableSqlAlias);
        for (Map.Entry<String, String> me : memberJoinAliases.entrySet()) {
            sqlFrom.append(", ");

            sqlFrom.append(operationsMeta.getCollectionMember(me.getKey()).sqlName());
            sqlFrom.append(" ");
            // AS
            sqlFrom.append(me.getValue());
        }
        return sqlFrom.toString();
    }

    public String getMainTableSqlAlias() {
        return mainTableSqlAlias;
    }

    String getWhere() {
        if (sql.length() == 0) {
            return "";
        } else {
            return " WHERE " + sql.toString();
        }
    }

    String getWhere(String conditions) {
        if (sql.length() == 0) {
            return " WHERE " + conditions;
        } else {
            return " WHERE " + conditions + " AND " + sql.toString();
        }
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

    void bindParameters(PreparedStatement stmt) throws SQLException {
        int parameterIndex = 1;
        if (multitenant) {
            stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            parameterIndex++;
        }
        for (Object param : bindParams) {
            stmt.setObject(parameterIndex, encodeValue(param));
            parameterIndex++;
        }
    }
}
