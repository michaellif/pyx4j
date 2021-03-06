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
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;

public class ValueAdapterEntityPolymorphic extends ValueBindAdapterAbstract implements ValueAdapter {

    protected int sqlTypeKey;

    protected int sqlTypeDiscriminator;

    protected final Dialect dialect;

    private final Map<String, Class<? extends IEntity>> impClasses = new HashMap<String, Class<? extends IEntity>>();

    private final String discriminatorColumnNameSufix;

    private final boolean singlePkRoot;

    protected ValueAdapterEntityPolymorphic(Dialect dialect, Class<? extends IEntity> entityClass) {
        this.dialect = dialect;
        sqlTypeKey = dialect.getTargetSqlType(Long.class);
        sqlTypeDiscriminator = dialect.getTargetSqlType(String.class);
        discriminatorColumnNameSufix = dialect.getNamingConvention().sqlDiscriminatorColumnNameSufix();

        Class<? extends IEntity> persitableSuperClass = EntityFactory.getEntityMeta(entityClass).getPersistableSuperClass();
        if (persitableSuperClass != null) {
            singlePkRoot = true;
        } else {
            Inheritance inheritance = entityClass.getAnnotation(Inheritance.class);
            if ((inheritance != null) && (inheritance.strategy() == Inheritance.InheritanceStrategy.SINGLE_TABLE)) {
                singlePkRoot = true;
            } else {
                singlePkRoot = false;
            }
        }

        for (Class<? extends IEntity> subclass : Mappings.getPersistableAssignableFrom(entityClass)) {
            DiscriminatorValue discriminator = Mappings.getDiscriminatorfor(subclass);
            if (discriminator != null) {
                if (impClasses.containsKey(discriminator.value())) {
                    throw new Error("Duplicate value of @DiscriminatorValue annotation on class " + subclass.getName() + "; the same as in class "
                            + impClasses.get(discriminator.value()));
                }
                impClasses.put(discriminator.value(), subclass);
            }
        }
    }

    public String getDiscriminatorColumnName(String memberSqlName) {
        return memberSqlName + discriminatorColumnNameSufix;
    }

    public Set<String> getDiscriminatorValues() {
        return impClasses.keySet();
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        return Arrays.asList(memberSqlName, getDiscriminatorColumnName(memberSqlName));
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String columnName) {
        if (columnName.endsWith(discriminatorColumnNameSufix)) {
            return dialect.isCompatibleType(String.class, TableModel.ENUM_STRING_LENGHT_MAX, typeName);
        } else {
            return dialect.isCompatibleType(Long.class, 0, typeName);
        }
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        if (columnName.endsWith(discriminatorColumnNameSufix)) {
            return dialect.getSqlType(String.class) //
                    + '(' + TableModel.ENUM_STRING_LENGHT_MAX + ')';
        } else {
            return dialect.getSqlType(Long.class);
        }
    }

    @Override
    public boolean isColumnTypeChanges(Dialect dialect, String typeName, int columnSize, MemberOperationsMeta member, String sqlColumnName) {
        return false;
    }

    @Override
    public String toSqlValue(Dialect dialect, String columnName, String argumentPlaceHolder) {
        return argumentPlaceHolder;
    }

    @Override
    public String querySqlFunctionOnValue(Dialect dialect, Restriction restriction, String argumentPlaceHolder) {
        return argumentPlaceHolder;
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        IEntity childEntity = (IEntity) value;
        Key primaryKey = childEntity.getPrimaryKey();
        if (primaryKey == null) {
            stmt.setNull(parameterIndex, sqlTypeKey);
            stmt.setNull(parameterIndex + 1, sqlTypeDiscriminator);
        } else {
            assert impClasses.containsValue(childEntity.getInstanceValueClass()) : "Unexpected class " + childEntity.getInstanceValueClass() + "\n"
                    + impClasses.values() + "\n" + value;
            DiscriminatorValue discriminator = childEntity.getInstanceValueClass().getAnnotation(DiscriminatorValue.class);
            stmt.setLong(parameterIndex, primaryKey.asLong());
            stmt.setString(parameterIndex + 1, discriminator.value());
        }
        return 2;
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        long value = rs.getLong(memberSqlName);
        if (rs.wasNull()) {
            return null;
        } else {
            String discriminatorValue = rs.getString(memberSqlName + discriminatorColumnNameSufix);
            if (discriminatorValue == null) {
                throw new SQLException("Missing discriminatorValue '" + memberSqlName + discriminatorColumnNameSufix + "' " + SQLUtils.debugInfo(dialect, rs));
            }
            Class<? extends IEntity> entityClass = impClasses.get(discriminatorValue);
            if (entityClass == null) {
                throw new SQLException("Unmapped discriminator '" + discriminatorValue + "' " + SQLUtils.debugInfo(dialect, rs));
            }
            IEntity entityValue = EntityFactory.create(entityClass);
            entityValue.setPrimaryKey(new Key(value));
            entityValue.setValueDetached();
            return entityValue;
        }
    }

    private class DiscriminatorQueryValueBindAdapter extends ValueBindAdapterAbstract {

        @Override
        public List<String> getColumnNames(String memberSqlName) {
            return Arrays.asList(memberSqlName + discriminatorColumnNameSufix);
        }

        @Override
        public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
            IEntity childEntity = (IEntity) value;
            assert impClasses.containsValue(childEntity.getInstanceValueClass()) : "Unexpected class " + childEntity.getInstanceValueClass() + "\n"
                    + impClasses.values() + "\n" + value;
            DiscriminatorValue discriminator = childEntity.getInstanceValueClass().getAnnotation(DiscriminatorValue.class);
            stmt.setString(parameterIndex, discriminator.value());
            return 1;
        }

    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        assert (singlePkRoot || (value == null) || (value instanceof Collection)
                || (value instanceof IEntity)) : "Can't query by polymorphic member using value of class " + value.getClass();

        if ((value instanceof IEntity) && (((IEntity) value).isPrototype() || (((IEntity) value).getPrimaryKey() == null))) {
            return new DiscriminatorQueryValueBindAdapter();
        } else if ((value instanceof Key) || (value instanceof Long)) {
            return new ValueAdapterEntity.QueryByEntityValueBindAdapter(sqlTypeKey);
        } else if (value instanceof Collection) {
            if (((Collection<?>) value).isEmpty()) {
                return this;
            } else {
                Object fistValue = ((Collection<?>) value).iterator().next();
                if ((fistValue instanceof IEntity) && (((IEntity) fistValue).getPrimaryKey() == null)) {
                    return new DiscriminatorQueryValueBindAdapter();
                } else if (singlePkRoot) {
                    return new ValueAdapterEntity.QueryByEntityValueBindAdapter(sqlTypeKey);
                } else {
                    return this;
                }
            }
        } else {
            return this;
        }
    }

    @Override
    public Serializable ensureType(Serializable value) {
        return value;
    }

    @Override
    public String toString() {
        return "EntityVirtual '" + impClasses.toString() + "'";
    }
}
