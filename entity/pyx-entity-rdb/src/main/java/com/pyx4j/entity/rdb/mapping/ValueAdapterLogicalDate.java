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
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rdb.dialect.Dialect;

class ValueAdapterLogicalDate extends ValueAdapterPrimitive {

    protected ValueAdapterLogicalDate(Dialect dialect) {
        super(dialect, LogicalDate.class);
    }

    @Override
    public int bindValue(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, sqlType);
        } else if (value instanceof java.sql.Date) {
            stmt.setDate(parameterIndex, (java.sql.Date) value);
        } else {
            Calendar c = new GregorianCalendar();
            c.setTime((java.util.Date) value);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            stmt.setDate(parameterIndex, new LogicalDate(c.getTimeInMillis()));
        }
        return 1;
    }

    @Override
    public Object retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        java.sql.Date value = rs.getDate(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            return new LogicalDate(value);
        }
    }

}
