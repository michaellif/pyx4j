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
 * Created on 2011-05-17
 * @author vlads
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;

class ValueAdapterBigDecimal extends ValueAdapterPrimitive {

    protected ValueAdapterBigDecimal(Dialect dialect) {
        super(dialect, BigDecimal.class);
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        return dialect.getSqlType(valueClass, member.getTypeConfiguration());
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            stmt.setBigDecimal(parameterIndex, (BigDecimal) value);
        }
        return 1;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        if (value == null || value instanceof BigDecimal) {
            return this;
        } else {
            return new QueryByDoubleValueBindAdapter();
        }
    }

    class QueryByDoubleValueBindAdapter extends ValueBindAdapterAbstract {

        @Override
        public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
            if (value == null) {
                stmt.setNull(parameterIndex, sqlType);
            } else {
                stmt.setDouble(parameterIndex, (Double) value);
            }
            return 1;
        }

    }
}
