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
 * @version $Id$
 */
package com.pyx4j.entity.core.adapters;

import java.io.Serializable;

/**
 * Allow to customize data type persistence and data types
 */
public interface PersistenceAdapter<ValueType extends Serializable, DatabaseType extends Serializable> {

    Class<DatabaseType> getDatabaseType();

    Class<ValueType> getValueType();

    /**
     * Convert a value type to a bound type.
     * 
     * @param v
     *            The value to be converted. Can be null.
     */
    DatabaseType persist(ValueType value);

    /**
     * Convert a bound type to a value type.
     * 
     * @param v
     *            The value to be convereted. Can be null.
     */
    ValueType retrieve(DatabaseType databaseValue);
}
