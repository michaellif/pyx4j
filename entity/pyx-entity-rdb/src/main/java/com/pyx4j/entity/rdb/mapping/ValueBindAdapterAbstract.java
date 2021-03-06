/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Oct 26, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.dialect.Dialect;

public abstract class ValueBindAdapterAbstract implements ValueBindAdapter {

    @Override
    public List<String> getColumnNames(String memberSqlName) {
        return Arrays.asList(memberSqlName);
    }

    @Override
    public List<String> querySQLFunctionOnColumn(Dialect dialect, Restriction restriction, List<String> columnNames) {
        return columnNames;
    }

    @Override
    public String querySqlFunctionOnValue(Dialect dialect, Restriction restriction, String argumentPlaceHolder) {
        return argumentPlaceHolder;
    }
}
