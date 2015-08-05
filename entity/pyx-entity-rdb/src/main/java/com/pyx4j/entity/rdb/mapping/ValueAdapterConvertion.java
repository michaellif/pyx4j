/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 18, 2013
 * @author vlads
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.pyx4j.entity.core.adapters.PersistenceAdapter;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;

@SuppressWarnings("rawtypes")
class ValueAdapterConvertion implements ValueAdapter {

    private final ValueAdapter valueAdapter;

    private final PersistenceAdapter persistenceAdapter;

    private final Class<?> valueClass;

    public ValueAdapterConvertion(ValueAdapter valueAdapter, PersistenceAdapter persistenceAdapter) {
        super();
        this.valueAdapter = valueAdapter;
        this.persistenceAdapter = persistenceAdapter;
        this.valueClass = persistenceAdapter.getValueType();
    }

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        return valueAdapter.getColumnNames(memberSqlName);
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        return valueAdapter.sqlColumnTypeDefinition(dialect, member, columnName);
    }

    @Override
    public boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String columnName) {
        return valueAdapter.isCompatibleType(dialect, typeName, member, columnName);
    }

    @Override
    public boolean isColumnTypeChanges(Dialect dialect, String typeName, int columnSize, MemberOperationsMeta member, String sqlColumnName) {
        return valueAdapter.isColumnTypeChanges(dialect, typeName, columnSize, member, sqlColumnName);
    }

    @Override
    public String toSqlValue(Dialect dialect, String columnName, String argumentPlaceHolder) {
        return valueAdapter.toSqlValue(null, columnName, argumentPlaceHolder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        return valueAdapter.bindValue(persistenceContext, stmt, parameterIndex, persistenceAdapter.persist((Serializable) value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        return persistenceAdapter.retrieve(valueAdapter.retrieveValue(rs, memberSqlName));
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

}
