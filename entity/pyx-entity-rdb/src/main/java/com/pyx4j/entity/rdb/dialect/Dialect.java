/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-07-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.rdb.mapping.QueryBuilder;
import com.pyx4j.entity.shared.IEntity;

public abstract class Dialect {

    protected final Map<Class<?>, TypeMeta> typeNames = new HashMap<Class<?>, TypeMeta>();

    private final NamingConvention namingConvention;

    private final DatabaseType databaseType;

    private final MultitenancyType multitenancyType;

    private final boolean multitenantSharedSchema;

    private final boolean multitenantSeparateSchemas;

    public static enum LimitOffsetSyntax {

        Standard,

        AbsolutCriteria,

        OffsetOnly
    }

    protected Dialect(DatabaseType databaseType, NamingConvention namingConvention, MultitenancyType multitenancyType) {
        this.databaseType = databaseType;
        this.namingConvention = namingConvention;
        this.multitenancyType = multitenancyType;
        this.multitenantSharedSchema = (multitenancyType == MultitenancyType.SharedSchema);
        this.multitenantSeparateSchemas = (multitenancyType == MultitenancyType.SeparateSchemas);
        addTypeMeta(Integer.class, "integer");
        addTypeMeta(Character.class, "char");
        addTypeMeta(String.class, "varchar");
        addTypeMeta(Float.class, "float");
        addTypeMeta(Double.class, "double");

        addTypeMeta(java.sql.Date.class, "date");
        addTypeMeta(LogicalDate.class, "date");
        addTypeMeta(java.sql.Time.class, "time");
    }

    public DatabaseType databaseType() {
        return databaseType;
    }

    public abstract int identifierMaximumLength();

    public NamingConvention getNamingConvention() {
        return namingConvention;
    }

    public String sqlDiscriminatorColumnName() {
        return getNamingConvention().sqlIdColumnName() + getNamingConvention().sqlDiscriminatorColumnNameSufix();
    }

    public MultitenancyType getMultitenancyType() {
        return multitenancyType;
    }

    public boolean isMultitenantSharedSchema() {
        return multitenantSharedSchema;
    }

    public boolean isMultitenantSeparateSchemas() {
        return multitenantSeparateSchemas;
    }

    protected void addTypeMeta(TypeMeta typeMeta) {
        typeNames.put(typeMeta.javaClass, typeMeta);
    }

    protected void addTypeMeta(Class<?> javaClass, String sqlType) {
        typeNames.put(javaClass, new TypeMeta(javaClass, sqlType));
    }

    protected void addTypeMeta(Class<?> javaClass, String sqlType, String... compatibleTypeNames) {
        typeNames.put(javaClass, new TypeMeta(javaClass, sqlType, compatibleTypeNames));
    }

    protected void addTypeMeta(Class<?> javaClass, String sqlType, int precision, int scale) {
        typeNames.put(javaClass, new TypeMeta(javaClass, sqlType, precision, scale));
    }

    public String getGeneratedIdColumnString() {
        return "";
    }

    public String sqlAlterIdentityColumn(String tableName, int identityOffset) {
        return null;
    }

    public Class<?> getType(Class<?> klass) {
        if (Enum.class.isAssignableFrom(klass)) {
            return String.class;
        } else if (Key.class.isAssignableFrom(klass)) {
            return Long.class;
        } else if (IEntity.class.isAssignableFrom(klass)) {
            return Long.class;
        } else {
            return klass;
        }
    }

    public String getSqlType(Class<?> klass) {
        return getSqlType(klass, 0);
    }

    public String getSqlType(Class<?> klass, int length) {
        TypeMeta typeMeta = typeNames.get(getType(klass));
        if (typeMeta == null) {
            throw new RuntimeException("Undefined SQL type for class " + getType(klass).getName());
        }
        return typeMeta.getSqlType(length);
    }

    public String getSqlType(Class<?> klass, TypeMetaConfiguration tmc) {
        TypeMeta typeMeta = typeNames.get(getType(klass));
        if (typeMeta == null) {
            throw new RuntimeException("Undefined SQL type for class " + getType(klass).getName());
        }
        return typeMeta.getSqlType(tmc);
    }

    public boolean isCompatibleType(Class<?> klass, int length, String typeName) {
        TypeMeta typeMeta = typeNames.get(getType(klass));
        if (typeMeta == null) {
            throw new RuntimeException("Undefined SQL type for class " + getType(klass).getName());
        }
        return typeMeta.isCompatibleType(typeName);
    }

    public int getTargetSqlType(Class<?> valueClass) {
        if (valueClass.equals(String.class)) {
            return Types.VARCHAR;
        } else if (valueClass.equals(Double.class)) {
            return Types.DOUBLE;
        } else if (valueClass.equals(Float.class)) {
            return Types.FLOAT;
        } else if (valueClass.equals(Long.class)) {
            return Types.BIGINT;
        } else if (valueClass.equals(Key.class)) {
            return Types.BIGINT;
        } else if (valueClass.equals(Integer.class)) {
            return Types.INTEGER;
        } else if (valueClass.equals(java.sql.Date.class)) {
            return Types.DATE;
        } else if (valueClass.equals(LogicalDate.class)) {
            return Types.DATE;
        } else if (valueClass.equals(java.util.Date.class)) {
            return Types.TIMESTAMP;
        } else if (valueClass.equals(java.sql.Time.class)) {
            return Types.TIME;
        } else if (valueClass.isEnum()) {
            return Types.VARCHAR;
        } else if (valueClass.equals(Boolean.class)) {
            return Types.BOOLEAN;
        } else if (valueClass.equals(BigDecimal.class)) {
            return Types.NUMERIC;
        } else if (valueClass.equals(Short.class)) {
            return Types.SMALLINT;
        } else if (valueClass.equals(Byte.class)) {
            return Types.TINYINT;
        } else if (valueClass.equals(byte[].class)) {
            return Types.BLOB;
        } else {
            throw new RuntimeException("Unsupported type " + valueClass.getName());
        }
    }

    @SuppressWarnings("incomplete-switch")
    public String sqlFunction(QueryBuilder<?> queryBuilder, SQLAggregateFunctions func, String args) {
        StringBuilder sql = new StringBuilder();
        sql.append(func.name());

        switch (func) {
        case COUNT:
            if (queryBuilder.addDistinct()) {
                if (args == null) {
                    args = queryBuilder.getMainTableSqlAlias() + "." + this.getNamingConvention().sqlIdColumnName();
                }
                args = "DISTINCT " + args;
            }
        }

        if (args == null) {
            sql.append("(*)");
        } else {
            sql.append("( ").append(args).append(" )");
        }
        return sql.toString();
    }

    public char likeWildCards() {
        return '%';
    }

    // case-insensitive search. TODO use lower(col) = lower(?)
    public String likeOperator() {
        return "LIKE";
    }

    public boolean isFunctionIndexesSupported() {
        return false;
    }

    public boolean isSequencesBaseIdentity() {
        return false;
    }

    public String sqlSequenceMetaData() {
        throw new Error("Dialect does not support sequences");
    }

    public String getSequenceNextValSql(String sequenceName) {
        throw new Error("Dialect does not support sequences");
    }

    public String getCreateSequenceSql(String sequenceName, int identityOffset) {
        throw new Error("Dialect does not support sequences");
    }

    public String getDropSequenceSql(String sequenceName) {
        throw new Error("Dialect does not support sequences");
    }

    public abstract String sqlDropForeignKey(String tableName, String constraintName);

    public boolean isForeignKeyDeferrableSupported() {
        return false;
    }

    public String applyLimitCriteria(String sql) {
        throw new Error("Dialect does not support limit");
    }

    public LimitOffsetSyntax limitCriteriaType() {
        throw new Error("Dialect does not support limit");
    }

    public String sqlSortNulls(boolean descending) {
        return "";
    }

    public abstract boolean isUniqueConstraintException(SQLException e);

    public boolean isIntegrityConstraintException(SQLException e) {
        return (e instanceof SQLIntegrityConstraintViolationException);
    }

}
