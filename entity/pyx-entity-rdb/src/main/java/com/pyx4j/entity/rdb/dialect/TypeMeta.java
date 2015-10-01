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
 * Created on Jan 1, 2011
 * @author vlads
 */
package com.pyx4j.entity.rdb.dialect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TypeMeta {

    final Class<?> javaClass;

    final String sqlType;

    final int maxLength;

    int precision = -1;

    int scale = -1;

    Collection<String> compatibleTypeNames;

    Collection<String> requireConversionTypeNames;

    private TreeMap<Integer, String> extendedTypes;

    public TypeMeta(Class<?> javaClass, String sqlType) {
        this.javaClass = javaClass;
        this.sqlType = sqlType.toLowerCase(Locale.ENGLISH);
        this.maxLength = 0;
    }

    public TypeMeta(Class<?> javaClass, String sqlType, String... compatibleTypeNames) {
        this.javaClass = javaClass;
        this.sqlType = sqlType.toLowerCase(Locale.ENGLISH);
        this.maxLength = 0;
        setCompatibleTypeNames(compatibleTypeNames);
    }

    public TypeMeta(Class<?> javaClass, String sqlType, int precision, int scale) {
        this.javaClass = javaClass;
        this.sqlType = sqlType.toLowerCase(Locale.ENGLISH);
        this.maxLength = 0;
        this.scale = scale;
        this.precision = precision;
    }

    public TypeMeta(Class<?> javaClass, int maxLength, String sqlType) {
        this.javaClass = javaClass;
        this.sqlType = sqlType.toLowerCase(Locale.ENGLISH);
        this.maxLength = maxLength;
    }

    public void addSqlType(int maxLength, String sqlType) {
        if (extendedTypes == null) {
            extendedTypes = new TreeMap<Integer, String>();
        }
        extendedTypes.put(maxLength, sqlType.toLowerCase(Locale.ENGLISH));
    }

    public String getSqlType(int length) {
        if ((length < maxLength) || (maxLength == 0)) {
            if (precision >= 0) {
                if (scale >= 0) {
                    return this.sqlType + "(" + precision + ", " + scale + ")";
                } else {
                    return this.sqlType + "(" + precision + ")";
                }
            } else {
                return this.sqlType;
            }
        }
        if (extendedTypes != null) {
            for (Map.Entry<Integer, String> me : extendedTypes.entrySet()) {
                if (me.getKey() > length) {
                    return me.getValue();
                }
            }
        }
        throw new RuntimeException("Undefined SQL type for length " + length + " for class " + javaClass.getName());
    }

    public String getSqlType(TypeMetaConfiguration tmc) {
        String matchingSqlType = null;
        if ((tmc.length < maxLength) || (maxLength == 0)) {
            matchingSqlType = this.sqlType;
        } else if (extendedTypes != null) {
            for (Map.Entry<Integer, String> me : extendedTypes.entrySet()) {
                if (me.getKey() > tmc.length) {
                    matchingSqlType = me.getValue();
                }
            }
        }

        if (matchingSqlType == null) {
            throw new RuntimeException("Undefined SQL type for length " + tmc.length + " for class " + javaClass.getName());
        }

        int typePrecision = this.precision;
        if (tmc.precision >= 0) {
            typePrecision = tmc.precision;
        }
        int typeScale = this.scale;
        if (tmc.scale >= 0) {
            typeScale = tmc.scale;
        }

        if (typePrecision >= 0) {
            if (typeScale >= 0) {
                return matchingSqlType + "(" + typePrecision + ", " + typeScale + ")";
            } else {
                return matchingSqlType + "(" + typePrecision + ")";
            }
        } else {
            return this.sqlType;
        }

    }

    public void setCompatibleTypeNames(String... compatibleTypeNames) {
        this.compatibleTypeNames = new HashSet<>();
        for (String type : compatibleTypeNames) {
            this.compatibleTypeNames.add(type.toLowerCase(Locale.ENGLISH));
        }
    }

    public void requireConversion(String... typeNames) {
        this.requireConversionTypeNames = new HashSet<>();
        for (String type : typeNames) {
            this.requireConversionTypeNames.add(type.toLowerCase(Locale.ENGLISH));
        }
    }

    public boolean isCompatibleType(String typeName) {
        if (sqlType.equals(typeName)) {
            return true;
        }
        if (compatibleTypeNames != null) {
            if (compatibleTypeNames.contains(typeName)) {
                return true;
            }
        }
        if (extendedTypes != null) {
            return extendedTypes.containsValue(typeName);
        }
        return false;
    }

    public boolean isPrimitiveTypeChanges(String typeName) {
        if (sqlType.equals(typeName)) {
            return false;
        }
        if (requireConversionTypeNames != null) {
            if (requireConversionTypeNames.contains(typeName)) {
                return true;
            }
        }
        if (compatibleTypeNames != null) {
            if (compatibleTypeNames.contains(typeName)) {
                return false;
            }
        }
        if (extendedTypes != null) {
            if (extendedTypes.containsValue(typeName)) {
                return false;
            }
        }
        return true;
    }

}
