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
 * Created on Sep 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

public class ValueAdapterEntityVirtual implements ValueAdapter {

    private static final Logger log = LoggerFactory.getLogger(ValueAdapterEntity.class);

    private static String DISCRIMINATOR_COLUNM_NAME_SUFIX = "_disc";

    protected int sqlTypeKey;

    protected int sqlTypeDiscriminator;

    private final Map<String, Class<? extends IEntity>> impClasses = new HashMap<String, Class<? extends IEntity>>();

    protected ValueAdapterEntityVirtual(Dialect dialect, Class<? extends IEntity> entityClass) {
        sqlTypeKey = dialect.getTargetSqlType(Long.class);
        sqlTypeDiscriminator = dialect.getTargetSqlType(String.class);

        for (Class<? extends IEntity> ec : ServerEntityFactory.getAllEntityClasses()) {
            if (entityClass.isAssignableFrom(ec)) {
                DiscriminatorValue discriminator = ec.getAnnotation(DiscriminatorValue.class);
                if (discriminator != null) {
                    impClasses.put(discriminator.value(), ec);
                }
            }
        }
    }

    @Override
    public List<String> getColumnNames(MemberOperationsMeta member) {
        List<String> columnNames = new Vector<String>();
        columnNames.add(member.sqlName());
        columnNames.add(member.sqlName() + DISCRIMINATOR_COLUNM_NAME_SUFIX);
        return columnNames;
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String coumnName) {
        if (coumnName.endsWith(DISCRIMINATOR_COLUNM_NAME_SUFIX)) {
            return dialect.isCompatibleType(String.class, TableModel.ENUM_STRING_LENGHT_MAX, typeName);
        } else {
            return dialect.isCompatibleType(Long.class, 0, typeName);
        }
    }

    @Override
    public void appendColumnDefinition(StringBuilder sql, Dialect dialect, MemberOperationsMeta member, String coumnName) {
        if (coumnName.endsWith(DISCRIMINATOR_COLUNM_NAME_SUFIX)) {
            sql.append(dialect.getSqlType(String.class));
            sql.append('(').append(TableModel.ENUM_STRING_LENGHT_MAX).append(')');
        } else {
            sql.append(dialect.getSqlType(Long.class));
        }
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
                stmt.setNull(parameterIndex, sqlTypeKey);
                stmt.setNull(parameterIndex + 1, sqlTypeDiscriminator);
            }
        } else {
            stmt.setLong(parameterIndex, primaryKey.asLong());
            DiscriminatorValue discriminator = childEntity.getInstanceValueClass().getAnnotation(DiscriminatorValue.class);
            stmt.setString(parameterIndex + 1, discriminator.value());
        }
        return 2;
    }

    @Override
    public void retrieveValue(ResultSet rs, IEntity entity, MemberOperationsMeta member) throws SQLException {
        IEntity memberEntity = (IEntity) member.getMember(entity);
        long value = rs.getLong(member.sqlName());
        Key pk;
        if (rs.wasNull()) {
            pk = null;
            memberEntity.setValue(null);
        } else {
            pk = new Key(value);
            String discriminatorValue = rs.getString(member.sqlName() + DISCRIMINATOR_COLUNM_NAME_SUFIX);
            Class<? extends IEntity> entityClass = impClasses.get(discriminatorValue);
            IEntity entityValue = EntityFactory.create(entityClass);
            entityValue.setPrimaryKey(pk);
            entityValue.setValuesDetached();
            memberEntity.set(entityValue);
        }
    }

}
