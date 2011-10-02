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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

class ValueAdapterEntity implements ValueAdapter {

    protected int sqlTypeKey;

    private final Class<? extends IEntity> entityClass;

    protected ValueAdapterEntity(Dialect dialect, Class<? extends IEntity> entityClass) {
        sqlTypeKey = dialect.getTargetSqlType(Long.class);
        this.entityClass = entityClass;
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        List<String> columnNames = new Vector<String>();
        columnNames.add(memberSqlName);
        return columnNames;
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String coumnName) {
        return dialect.isCompatibleType(Long.class, 0, typeName);
    }

    @Override
    public void appendColumnDefinition(StringBuilder sql, Dialect dialect, MemberOperationsMeta member, String coumnName) {
        sql.append(dialect.getSqlType(Long.class));
    }

    @Override
    public int bindValue(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
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
    public Object retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        long value = rs.getLong(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            IEntity entity = EntityFactory.create(entityClass);
            entity.setPrimaryKey(new Key(value));
            entity.setValuesDetached();
            return entity;
        }
    }
}
