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
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.dialect.Dialect;

interface ValueAdapter extends ValueBindAdapter {

    String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String sqlColumnName);

    boolean isCompatibleType(Dialect dialect, String typeName, MemberOperationsMeta member, String sqlColumnName);

    boolean isColumnTypeChanges(Dialect dialect, String typeName, int columnSize, MemberOperationsMeta member, String sqlColumnName);

    String toSqlValue(Dialect dialect, String columnName, String argumentPlaceHolder);

    Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException;

    ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value);

    Serializable ensureType(Serializable value);
}
