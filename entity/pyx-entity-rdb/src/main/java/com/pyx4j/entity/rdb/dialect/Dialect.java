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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;

public abstract class Dialect {

    protected final Map<Class<?>, TypeMeta> typeNames = new HashMap<Class<?>, TypeMeta>();

    protected Dialect() {
        addTypeMeta(Integer.class, "integer");
        addTypeMeta(Character.class, "char");
        addTypeMeta(String.class, "varchar");
        addTypeMeta(Float.class, "float");
        addTypeMeta(Double.class, "double");

        addTypeMeta(java.util.Date.class, "timestamp");
        addTypeMeta(java.sql.Date.class, "date");
    }

    protected void addTypeMeta(Class<?> javaClass, String sqlType) {
        typeNames.put(javaClass, new TypeMeta(javaClass, sqlType));
    }

    protected void addTypeMeta(Class<?> javaClass, String sqlType, String... compatibleTypeNames) {
        typeNames.put(javaClass, new TypeMeta(javaClass, sqlType, compatibleTypeNames));
    }

    public String getGeneratedIdColumnString() {
        return "";
    }

    public Class<?> getType(Class<?> klass) {
        if (Enum.class.isAssignableFrom(klass)) {
            return String.class;
        } else if (IEntity.class.isAssignableFrom(klass)) {
            return Long.class;
        } else {
            return klass;
        }
    }

    public String getSqlType(Class<?> klass) {
        TypeMeta typeMeta = typeNames.get(getType(klass));
        if (typeMeta == null) {
            throw new RuntimeException("Undefined SQL type for class " + getType(klass).getName());
        }
        return typeMeta.sqlType;
    }

    public boolean isCompatibleType(Class<?> klass, String typeName) {
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
        } else if (valueClass.equals(Integer.class)) {
            return Types.INTEGER;
        } else if (valueClass.equals(java.sql.Date.class)) {
            return Types.DATE;
        } else if (valueClass.equals(java.util.Date.class)) {
            return Types.TIMESTAMP;
        } else if (valueClass.isEnum()) {
            return Types.VARCHAR;
        } else if (valueClass.equals(Boolean.class)) {
            return Types.BOOLEAN;
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
}
