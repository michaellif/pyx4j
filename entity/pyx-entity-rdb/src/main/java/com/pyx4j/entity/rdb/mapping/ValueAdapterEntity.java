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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.IEntity;

class ValueAdapterEntity implements ValueAdapter {

    private static final Logger log = LoggerFactory.getLogger(ValueAdapterEntity.class);

    protected int sqlType;

    protected ValueAdapterEntity(Dialect dialect) {
        sqlType = dialect.getTargetSqlType(Long.class);
    }

    @Override
    public List<String> getColumnNames(MemberOperationsMeta member) {
        List<String> columnNames = new Vector<String>();
        columnNames.add(member.sqlName());
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
    public int bindValue(PreparedStatement stmt, int parameterIndex, IEntity entity, MemberOperationsMeta member) throws SQLException {
        IEntity childEntity = (IEntity) member.getMember(entity);
        Key primaryKey = childEntity.getPrimaryKey();
        if (primaryKey == null) {
            if (!childEntity.isNull()) {
                log.error("Saving non persisted reference {}", childEntity);
                throw new Error("Saving non persisted reference " + member.getMemberMeta().getValueClass().getName() + " "
                        + member.getMemberMeta().getCaption() + " of " + entity.getEntityMeta().getCaption());
            } else {
                stmt.setNull(parameterIndex, sqlType);
            }
        } else {
            stmt.setLong(parameterIndex, primaryKey.asLong());
        }
        return 1;
    }

    @Override
    public void retrieveValue(ResultSet rs, IEntity entity, MemberOperationsMeta member) throws SQLException {
        long value = rs.getLong(member.sqlName());
        Key pk;
        if (rs.wasNull()) {
            pk = null;
        } else {
            pk = new Key(value);
        }
        ((IEntity) member.getMember(entity)).setPrimaryKey(pk);
    }
}
