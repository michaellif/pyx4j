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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;

class ValueAdapterEntity extends ValueBindAdapterAbstract implements ValueAdapter {

    protected int sqlTypeKey;

    private final Class<? extends IEntity> entityClass;

    protected ValueAdapterEntity(Dialect dialect, Class<? extends IEntity> entityClass) {
        sqlTypeKey = dialect.getTargetSqlType(Long.class);
        this.entityClass = entityClass;
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        return Arrays.asList(memberSqlName);
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String columnName) {
        return dialect.isCompatibleType(Long.class, 0, typeName);
    }

    @Override
    public boolean isColumnTypeChanges(Dialect dialect, String typeName, int columnSize, MemberOperationsMeta member, String sqlColumnName) {
        return false;
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        return dialect.getSqlType(Long.class);
    }

    @Override
    public String toSqlValue(Dialect dialect, String columnName, String argumentPlaceHolder) {
        return argumentPlaceHolder;
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        IEntity childEntity = (IEntity) value;
        Key primaryKey = childEntity.getPrimaryKey();
        if (primaryKey == null) {
            stmt.setNull(parameterIndex, sqlTypeKey);
        } else {
            stmt.setLong(parameterIndex, primaryKey.asLong());
        }
        return 1;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        long value = rs.getLong(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            IEntity entity = EntityFactory.create(entityClass);
            entity.setPrimaryKey(new Key(value));
            entity.setValueDetached();
            return entity;
        }
    }

    static class QueryByEntityValueBindAdapter extends ValueBindAdapterAbstract {

        private final int sqlTypeKey;

        QueryByEntityValueBindAdapter(int sqlTypeKey) {
            this.sqlTypeKey = sqlTypeKey;
        }

        @Override
        public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
            if (value == null) {
                stmt.setNull(parameterIndex, sqlTypeKey);
            } else {
                long key;
                if (value instanceof Key) {
                    key = ((Key) value).asLong();
                } else if (value instanceof IEntity) {
                    key = ((IEntity) value).getPrimaryKey().asLong();
                } else {
                    key = (Long) value;
                }
                stmt.setLong(parameterIndex, key);
            }
            return 1;
        }

    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        if (value instanceof IEntity) {
            return this;
        } else {
            return new QueryByEntityValueBindAdapter(sqlTypeKey);
        }
    }

    @Override
    public Serializable ensureType(Serializable value) {
        return value;
    }

    @Override
    public String toString() {
        return "Entity '" + entityClass.getSimpleName() + "'";
    }
}
