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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

public class ValueAdapterEntityVirtual implements ValueAdapter {

    private static String DISCRIMINATOR_COLUNM_NAME_SUFIX = "_disc";

    protected int sqlTypeKey;

    protected int sqlTypeDiscriminator;

    private final Map<String, Class<? extends IEntity>> impClasses = new HashMap<String, Class<? extends IEntity>>();

    protected ValueAdapterEntityVirtual(Dialect dialect, Class<? extends IEntity> entityClass) {
        sqlTypeKey = dialect.getTargetSqlType(Long.class);
        sqlTypeDiscriminator = dialect.getTargetSqlType(String.class);

        for (Class<? extends IEntity> ec : ServerEntityFactory.getAllEntityClasses()) {
            if (entityClass.isAssignableFrom(ec)) {
                if (ec.getAnnotation(Transient.class) != null) {
                    continue;
                }
                DiscriminatorValue discriminator = ec.getAnnotation(DiscriminatorValue.class);
                if (discriminator != null) {
                    if (CommonsStringUtils.isEmpty(discriminator.value())) {
                        throw new Error("Missing value of @DiscriminatorValue annotation on class " + ec.getName());
                    }
                    if (impClasses.containsKey(discriminator.value())) {
                        throw new Error("Duplicate value of @DiscriminatorValue annotation on class " + ec.getName() + "; the same as in calss "
                                + impClasses.get(discriminator.value()));
                    }
                    impClasses.put(discriminator.value(), ec);
                } else if (ec.getAnnotation(AbstractEntity.class) == null) {
                    throw new Error("Class " + ec.getName() + " require @AbstractEntity or @DiscriminatorValue annotation");
                }
            }
        }
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        List<String> columnNames = new Vector<String>();
        columnNames.add(memberSqlName + DISCRIMINATOR_COLUNM_NAME_SUFIX);
        columnNames.add(memberSqlName);
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
    public int bindValue(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        IEntity childEntity = (IEntity) value;
        Key primaryKey = childEntity.getPrimaryKey();
        if (primaryKey == null) {
            stmt.setNull(parameterIndex, sqlTypeDiscriminator);
            stmt.setNull(parameterIndex + 1, sqlTypeKey);
        } else {
            assert impClasses.containsValue(childEntity.getInstanceValueClass()) : "Unexpected class " + childEntity.getInstanceValueClass() + "\n"
                    + impClasses.values() + "\n" + value;
            DiscriminatorValue discriminator = childEntity.getInstanceValueClass().getAnnotation(DiscriminatorValue.class);
            stmt.setString(parameterIndex, discriminator.value());
            stmt.setLong(parameterIndex + 1, primaryKey.asLong());
        }
        return 2;
    }

    @Override
    public Object retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        long value = rs.getLong(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            String discriminatorValue = rs.getString(memberSqlName + DISCRIMINATOR_COLUNM_NAME_SUFIX);
            Class<? extends IEntity> entityClass = impClasses.get(discriminatorValue);
            IEntity entityValue = EntityFactory.create(entityClass);
            entityValue.setPrimaryKey(new Key(value));
            entityValue.setValueDetached();
            return entityValue;
        }
    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        assert ((value == null) || (value instanceof IEntity)) : "Can't query by polymorphic member using value of class " + value.getClass();

        if ((value != null) && (((IEntity) value).getPrimaryKey() == null)) {
            return new ValueBindAdapter() {

                @Override
                public int bindValue(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
                    IEntity childEntity = (IEntity) value;
                    assert impClasses.containsValue(childEntity.getInstanceValueClass()) : "Unexpected class " + childEntity.getInstanceValueClass() + "\n"
                            + impClasses.values() + "\n" + value;
                    DiscriminatorValue discriminator = childEntity.getInstanceValueClass().getAnnotation(DiscriminatorValue.class);
                    stmt.setString(parameterIndex, discriminator.value());
                    return 1;
                }

                @Override
                public List<String> getColumnNames(String memberSqlName) {
                    List<String> columnNames = new Vector<String>();
                    columnNames.add(memberSqlName + DISCRIMINATOR_COLUNM_NAME_SUFIX);
                    return columnNames;
                }
            };
        } else {
            return this;
        }
    }

    @Override
    public Object ensureType(Object value) {
        return value;
    }

    @Override
    public String toString() {
        return "EntityVirtual '" + impClasses.toString() + "'";
    }
}
