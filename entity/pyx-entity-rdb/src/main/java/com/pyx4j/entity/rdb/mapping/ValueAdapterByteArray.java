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
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;

class ValueAdapterByteArray extends ValueAdapterPrimitive {

    private final MemberMeta memberMeta;

    protected ValueAdapterByteArray(Dialect dialect, MemberMeta memberMeta) {
        super(dialect, byte[].class);
        this.memberMeta = memberMeta;
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            int maxLength = memberMeta.getLength();
            if (maxLength > 0) {
                int size = Array.getLength(value);
                if (size > maxLength) {
                    throw new RuntimeException(
                            "Member size violation member '" + memberMeta.getFieldName() + "' size " + size + " is greater than max allowed " + maxLength);
                }
            }
            stmt.setBytes(parameterIndex, (byte[]) value);
        }
        return 1;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        byte[] value = rs.getBytes(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }

}
