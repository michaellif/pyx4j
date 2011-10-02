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
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.geo.GeoPoint;

class ValueAdapterGeoPoint implements ValueAdapter {

    protected int sqlType;

    protected ValueAdapterGeoPoint(Dialect dialect) {
        sqlType = dialect.getTargetSqlType(Double.class);
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        List<String> columnNames = new Vector<String>();
        columnNames.add(memberSqlName + "_lat");
        columnNames.add(memberSqlName + "_lng");
        return columnNames;
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String coumnName) {
        return dialect.isCompatibleType(Double.class, 0, typeName);
    }

    @Override
    public void appendColumnDefinition(StringBuilder sql, Dialect dialect, MemberOperationsMeta member, String coumnName) {
        sql.append(dialect.getSqlType(Double.class));
    }

    @Override
    public int bindValue(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
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
    public Object retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        double lat = rs.getDouble(memberSqlName + "_lat");
        if (rs.wasNull()) {
            return null;
        } else {
            return new GeoPoint(lat, rs.getDouble(memberSqlName + "_lng"));
        }
    }

}
