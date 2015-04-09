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
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.dialect.Dialect;

abstract class ValueAdapterPrimitive implements ValueAdapter {

    protected final Class<?> valueClass;

    protected int sqlType;

    protected ValueAdapterPrimitive(Dialect dialect, Class<?> valueClass) {
        this.valueClass = valueClass;
        sqlType = dialect.getTargetSqlType(valueClass);
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        return Arrays.asList(memberSqlName);
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String columnName) {
        return dialect.isCompatibleType(valueClass, member.getMemberMeta().getLength(), typeName);
    }

    @Override
    public void appendColumnDefinition(StringBuilder sql, Dialect dialect, MemberOperationsMeta member, String columnName) {
        sql.append(dialect.getSqlType(valueClass, member.getMemberMeta().getLength()));
    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        return this;
    }

    @Override
    public Serializable ensureType(Serializable value) {
        assert (value == null) || valueClass.isAssignableFrom(value.getClass()) : "Trying to set value of a wrong type '" + value.getClass() + "', expected "
                + valueClass;
        return value;
    }

    @Override
    public String toString() {
        return "Primitive " + valueClass.getSimpleName();
    }

}
