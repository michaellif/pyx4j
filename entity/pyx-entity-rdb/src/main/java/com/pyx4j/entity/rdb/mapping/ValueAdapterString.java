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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;

class ValueAdapterString extends ValueAdapterPrimitive {

    private static final Logger log = LoggerFactory.getLogger(ValueAdapterString.class);

    private final MemberMeta memberMeta;

    private final boolean ignoreCaseIndex;

    protected ValueAdapterString(Dialect dialect, MemberMeta memberMeta) {
        super(dialect, String.class);
        this.memberMeta = memberMeta;
        if (memberMeta.isIndexed() && dialect.isFunctionIndexesSupported() && memberMeta.getAnnotation(Indexed.class).ignoreCase()) {
            ignoreCaseIndex = true;
        } else {
            ignoreCaseIndex = false;
        }
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        int maxLength = member.getMemberMeta().getLength();
        if (maxLength == 0) {
            maxLength = TableModel.ORDINARY_STRING_LENGHT_MAX;
        }
        return super.sqlColumnTypeDefinition(dialect, member, columnName) + '(' + maxLength + ')';
    }

    @Override
    public boolean isColumnTypeChanges(Dialect dialect, String typeName, int columnSize, MemberOperationsMeta member, String sqlColumnName) {
        int maxLength = member.getMemberMeta().getLength();
        if (maxLength == 0) {
            maxLength = TableModel.ORDINARY_STRING_LENGHT_MAX;
        }
        if (maxLength != columnSize) {
            return true;
        }
        return false;
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        String str = (String) value;
        if (str == null) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            int maxLength = memberMeta.getLength();
            if (maxLength == 0) {
                maxLength = TableModel.ORDINARY_STRING_LENGHT_MAX;
            }
            int size = str.length();
            if (size > maxLength) {
                log.error("member '{}' size violation [{}]", memberMeta.getFieldName(), str);
                throw new RuntimeException(
                        "Member size violation member '" + memberMeta.getFieldName() + "' size " + size + " is greater than max allowed " + maxLength);
            }
            if (size == 0) {
                stmt.setNull(parameterIndex, sqlType);
            } else {
                stmt.setString(parameterIndex, str);
            }
        }
        return 1;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        String value = rs.getString(memberSqlName);
        if (rs.wasNull() || (value.length() == 0)) {
            return null;
        } else {
            return value;
        }
    }

    /**
     * Adapter without size validation for Query
     *
     */

    private class QueryStringValueBindAdapter extends ValueBindAdapterAbstract {

        @Override
        public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
            String str = (String) value;
            if (str == null) {
                stmt.setNull(parameterIndex, sqlType);
            } else {
                int size = str.length();
                if (size == 0) {
                    stmt.setNull(parameterIndex, sqlType);
                } else {
                    stmt.setString(parameterIndex, str);
                }
            }
            return 1;
        }

        @Override
        public List<String> querySQLFunctionOnColumn(Dialect dialect, Restriction restriction, List<String> columnNames) {
            if (ignoreCaseIndex) {
                List<String> columnSql = new ArrayList<>();
                for (String columnName : columnNames) {
                    columnSql.add("LOWER(" + columnName + ')');
                }
                return columnSql;
            } else {
                return columnNames;
            }
        }

        @Override
        public String querySqlFunctionOnValue(Dialect dialect, Restriction restriction, String argumentPlaceHolder) {
            if (ignoreCaseIndex) {
                return "LOWER(" + argumentPlaceHolder + ')';
            } else {
                return argumentPlaceHolder;
            }
        }

    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        if (restriction == Restriction.RDB_LIKE) {
            return new QueryStringValueBindAdapter();
        } else {
            // TODO Add validation  in UI.
            return new QueryStringValueBindAdapter();
        }
    }

}
