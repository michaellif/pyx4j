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
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.geo.GeoPoint;

class ValueAdapterGeoPoint extends ValueBindAdapterAbstract implements ValueAdapter {

    protected int sqlType;

    protected ValueAdapterGeoPoint(Dialect dialect) {
        sqlType = dialect.getTargetSqlType(Double.class);
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        return Arrays.asList(memberSqlName + "_lat", memberSqlName + "_lng");
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String columnName) {
        return dialect.isCompatibleType(Double.class, 0, typeName);
    }

    @Override
    public boolean isColumnTypeChanges(Dialect dialect, String typeName, int columnSize, MemberOperationsMeta member, String sqlColumnName) {
        return false;
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        return dialect.getSqlType(Double.class);
    }

    @Override
    public String toSqlValue(Dialect dialect, String columnName, String argumentPlaceHolder) {
        return argumentPlaceHolder;
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, sqlType);
            stmt.setNull(parameterIndex + 1, sqlType);
        } else {
            GeoPoint geo = (GeoPoint) value;
            stmt.setDouble(parameterIndex, geo.getLat());
            stmt.setDouble(parameterIndex + 1, geo.getLng());
        }
        return 2;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        double lat = rs.getDouble(memberSqlName + "_lat");
        if (rs.wasNull()) {
            return null;
        } else {
            return new GeoPoint(lat, rs.getDouble(memberSqlName + "_lng"));
        }
    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        return this;
    }

    @Override
    public Serializable ensureType(Serializable value) {
        return value;
    }

}
