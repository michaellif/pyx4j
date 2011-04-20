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
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TypeMeta {

    final Class<?> javaClass;

    final String sqlType;

    final int maxLength;

    int precision = -1;

    int scale = -1;

    String[] compatibleTypeNames;

    private TreeMap<Integer, String> extendedTypes;

    public TypeMeta(Class<?> javaClass, String sqlType) {
        this.javaClass = javaClass;
        this.sqlType = sqlType;
        this.maxLength = 0;
    }

    public TypeMeta(Class<?> javaClass, String sqlType, String... compatibleTypeNames) {
        this.javaClass = javaClass;
        this.sqlType = sqlType;
        this.maxLength = 0;
        this.compatibleTypeNames = compatibleTypeNames;
    }

    public TypeMeta(Class<?> javaClass, String sqlType, int precision, int scale) {
        this.javaClass = javaClass;
        this.sqlType = sqlType;
        this.maxLength = 0;
        this.scale = scale;
        this.precision = precision;
    }

    public TypeMeta(Class<?> javaClass, int maxLength, String sqlType) {
        this.javaClass = javaClass;
        this.sqlType = sqlType;
        this.maxLength = maxLength;
    }

    public void addSqlType(int maxLength, String sqlType) {
        if (extendedTypes == null) {
            extendedTypes = new TreeMap<Integer, String>();
        }
        extendedTypes.put(maxLength, sqlType);
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

    public void setCompatibleTypeNames(String... compatibleTypeNames) {
        this.compatibleTypeNames = compatibleTypeNames;
    }

    public boolean isCompatibleType(String typeName) {
        if (sqlType.equalsIgnoreCase(typeName)) {
            return true;
        }
        if (compatibleTypeNames != null) {
            for (String name : compatibleTypeNames) {
                if (name.equalsIgnoreCase(typeName)) {
                    return true;
                }
            }
        }
        if (extendedTypes != null) {
            return extendedTypes.containsValue(typeName.toLowerCase(Locale.ENGLISH));
        }
        return false;
    }

}
