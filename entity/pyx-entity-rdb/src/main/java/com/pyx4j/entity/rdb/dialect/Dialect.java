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

import java.util.HashMap;
import java.util.Map;

public abstract class Dialect {

    protected final Map<Class<?>, String> typeNames = new HashMap<Class<?>, String>();

    protected Dialect() {
        typeNames.put(Integer.class, "integer");
        typeNames.put(Character.class, "char");
        typeNames.put(String.class, "varchar");
        typeNames.put(Float.class, "float");
        typeNames.put(Double.class, "double");

        typeNames.put(java.util.Date.class, "timestamp");
        typeNames.put(java.sql.Date.class, "date");
    }

    public String getSqlType(Class<?> klass) {
        if (Enum.class.isAssignableFrom(klass)) {
            klass = String.class;
        }
        String name = typeNames.get(klass);
        if (name == null) {
            throw new RuntimeException("Undefined SQL type for class " + klass.getName());
        }
        return name;
    }
}
