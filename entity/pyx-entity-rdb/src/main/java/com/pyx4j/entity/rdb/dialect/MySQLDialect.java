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

public class MySQLDialect extends Dialect {

    public MySQLDialect(NamingConvention namingConvention) {
        super(namingConvention);
        addTypeMeta(Integer.class, "int");
        addTypeMeta(Short.class, "smallint");
        addTypeMeta(Long.class, "bigint");
        addTypeMeta(Double.class, "double");
        addTypeMeta(Boolean.class, "bit");
        addTypeMeta(byte[].class, "blob");
        addTypeMeta(java.util.Date.class, "datetime");
    }

    @Override
    public String getGeneratedIdColumnString() {
        return "NOT NULL AUTO_INCREMENT";
    }
}
