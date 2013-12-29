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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;

class ValueAdapterEntityVersioned implements ValueAdapter {

    private static String FOR_DATE_COLUNM_NAME_SUFIX = "_for";

    protected int sqlTypeKey;

    protected int sqlTypeForDate;

    private final Class<? extends IEntity> entityClass;

    protected ValueAdapterEntityVersioned(Dialect dialect, Class<? extends IEntity> entityClass) {
        sqlTypeKey = dialect.getTargetSqlType(Long.class);
        sqlTypeForDate = dialect.getTargetSqlType(java.util.Date.class);
        this.entityClass = entityClass;
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        List<String> columnNames = new Vector<String>();
        columnNames.add(memberSqlName);
        columnNames.add(memberSqlName + FOR_DATE_COLUNM_NAME_SUFIX);
        return columnNames;
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String columnName) {
        if (columnName.endsWith(FOR_DATE_COLUNM_NAME_SUFIX)) {
            return dialect.isCompatibleType(java.util.Date.class, 0, typeName);
        } else {
            return dialect.isCompatibleType(Long.class, 0, typeName);
        }
    }

    @Override
    public void appendColumnDefinition(StringBuilder sql, Dialect dialect, MemberOperationsMeta member, String columnName) {
        if (columnName.endsWith(FOR_DATE_COLUNM_NAME_SUFIX)) {
            sql.append(dialect.getSqlType(java.util.Date.class));
        } else {
            sql.append(dialect.getSqlType(Long.class));
        }
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        IVersionedEntity<?> childEntity = (IVersionedEntity<?>) value;
        Key primaryKey = childEntity.getPrimaryKey();
        if (primaryKey == null) {
            stmt.setNull(parameterIndex, sqlTypeKey);
            stmt.setNull(parameterIndex + 1, sqlTypeForDate);
        } else {
            stmt.setLong(parameterIndex, primaryKey.asLong());
            Calendar c = new GregorianCalendar();
            if (primaryKey.getVersion() >= Key.VERSION_DRAFT) {
                c.setTimeInMillis(primaryKey.getVersion());
            } else {
                c.setTime(persistenceContext.getTimeNow());
                // DB does not store Milliseconds
                c.set(Calendar.MILLISECOND, 0);
                childEntity.setPrimaryKey(primaryKey.asVersionKey(c.getTime()));
            }
            stmt.setTimestamp(parameterIndex + 1, new java.sql.Timestamp(c.getTimeInMillis()));
        }
        return 2;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        long value = rs.getLong(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            IEntity entity = EntityFactory.create(entityClass);
            java.sql.Timestamp forDate = rs.getTimestamp(memberSqlName + FOR_DATE_COLUNM_NAME_SUFIX);
            if (!rs.wasNull()) {
                entity.setPrimaryKey(new Key(value).asVersionKey(new java.util.Date(forDate.getTime())));
            } else {
                entity.setPrimaryKey(new Key(value));
            }
            entity.setValueDetached();
            return entity;
        }
    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        if (value instanceof IEntity) {
            return this;
        } else {
            return new ValueBindAdapter() {

                @Override
                public List<String> getColumnNames(String memberSqlName) {
                    List<String> columnNames = new Vector<String>();
                    columnNames.add(memberSqlName);
                    return columnNames;
                }

                @Override
                public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
                    if (value == null) {
                        stmt.setNull(parameterIndex, sqlTypeKey);
                    } else {
                        long key;
                        if (value instanceof Key) {
                            key = ((Key) value).asLong();
                        } else {
                            key = (Long) value;
                        }
                        stmt.setLong(parameterIndex, key);
                    }
                    return 1;
                }

            };
        }
    }

    @Override
    public Serializable ensureType(Serializable value) {
        return value;
    }

    @Override
    public String toString() {
        return "EntityVersioned '" + entityClass.getSimpleName() + "'";
    }
}
