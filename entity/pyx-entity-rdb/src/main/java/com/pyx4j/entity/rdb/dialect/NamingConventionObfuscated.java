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
 * Created on Dec 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class NamingConventionObfuscated extends NamingConventionOracle {

    protected Map<String, String> obfuscationTable = new HashMap<String, String>();

    public NamingConventionObfuscated() {
        super();
    }

    public NamingConventionObfuscated(int identifierMaximumLength, ShortWords shortWords) {
        super(identifierMaximumLength, shortWords);
    }

    protected String obfuscate(String name) {
        String o = obfuscationTable.get(name);
        if (o == null) {
            synchronized (obfuscationTable) {
                o = obfuscationTable.get(name);
                if (o == null) {
                    CRC32 c = new CRC32();
                    c.update(name.getBytes());
                    String withNumbers = Long.toString(c.getValue(), 26);
                    StringBuilder b = new StringBuilder();
                    for (int index = 0; index < withNumbers.length(); index++) {
                        char cr = withNumbers.charAt(index);
                        if (cr <= '9') {
                            cr = (char) (cr + ('J' - '9'));
                        }
                        b.append(cr);
                    }
                    o = b.toString();
                    obfuscationTable.put(name, o);
                }
            }
        }
        return o;
    }

    @Override
    public String sqlTableName(String javaPersistenceName) {
        return obfuscate(super.sqlTableName(javaPersistenceName));
    }

    @Override
    public String sqlTableSequenceName(String javaPersistenceName) {
        return obfuscate(super.sqlTableSequenceName(javaPersistenceName));
    }

    @Override
    public String sqlTablePKName(String tableName) {
        return obfuscate(super.sqlTablePKName(tableName));
    }

    @Override
    public String sqlChildTableSequenceName(String tableName) {
        return obfuscate(super.sqlChildTableSequenceName(tableName));
    }

    @Override
    public String sqlTableIndexName(String tableName, List<String> columns) {
        return obfuscate(super.sqlTableIndexName(tableName, columns));
    }

    @Override
    public String sqlChildTableName(String javaPersistenceTableName, String javaPersistenceChildTableName) {
        return obfuscate(super.sqlChildTableName(javaPersistenceTableName, javaPersistenceChildTableName));
    }

    @Override
    public String sqlFieldName(String javaPersistenceFieldName) {
        return obfuscate(super.sqlFieldName(javaPersistenceFieldName));
    }

    @Override
    public String sqlEmbededFieldName(List<String> path, String javaPersistenceFieldName) {
        return obfuscate(super.sqlEmbededFieldName(path, javaPersistenceFieldName));
    }

    @Override
    public String sqlEmbededTableName(String javaPersistenceTableName, List<String> path, String javaPersistenceFieldName) {
        return obfuscate(super.sqlEmbededTableName(javaPersistenceTableName, path, javaPersistenceFieldName));
    }

}
