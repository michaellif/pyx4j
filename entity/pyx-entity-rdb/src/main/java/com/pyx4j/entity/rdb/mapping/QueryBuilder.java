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
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;

class QueryBuilder<T extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final StringBuilder sql = new StringBuilder();

    private final List<Object> bindParams = new Vector<Object>();

    QueryBuilder(Dialect dialect, EntityMeta entityMeta, EntityQueryCriteria<T> criteria) {
        if ((criteria.getFilters() != null) && (!criteria.getFilters().isEmpty())) {
            sql.append(" WHERE ");
            boolean firstCriteria = true;
            for (Criterion cr : criteria.getFilters()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sql.append(" AND ");
                }
                if (cr instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
                    sql.append(dialect.sqlName(propertyCriterion.getPropertyName()));
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

    String getWhere() {
        return sql.toString();
    }

    void bindParameters(PreparedStatement stmt) throws SQLException {
        int parameterIndex = 1;
        for (Object param : bindParams) {
            if (param instanceof Enum) {
                param = ((Enum<?>) param).name();
            } else if (param instanceof IEntity) {
                param = ((IEntity) param).getPrimaryKey();
            }
            stmt.setObject(parameterIndex, param);
            parameterIndex++;
        }
    }
}
