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
 * Created on Mar 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.util.List;

public class NamingConventionOracle implements NamingConvention {

    private final int identifierMaximumLength;

    public NamingConventionOracle() {
        this.identifierMaximumLength = -1;
    }

    public NamingConventionOracle(int identifierMaximumLength) {
        this.identifierMaximumLength = identifierMaximumLength;
    }

    public static String splitCapitals(String word) {
        StringBuilder b = new StringBuilder();
        boolean inWord = false;
        for (char c : word.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (inWord) {
                    b.append('_');
                    inWord = false;
                }
            } else {
                c = Character.toUpperCase(c);
                inWord = true;
            }
            b.append(c);
        }
        return b.toString();
    }

    @Override
    public String sqlTableName(String javaPersistenceName) {
        return splitCapitals(javaPersistenceName);
    }

    @Override
    public String sqlChildTableName(String javaPersistenceTableName, String javaPersistenceChildTableName) {
        return sqlTableName(javaPersistenceTableName) + "_" + sqlTableName(javaPersistenceChildTableName);
    }

    @Override
    public String sqlFieldName(String javaPersistenceFieldName) {
        return splitCapitals(javaPersistenceFieldName).replace('-', '_');
    }

    @Override
    public String sqlEmbededFieldName(List<String> path, String javaPersistenceFieldName) {
        StringBuilder sql = new StringBuilder();
        for (String pathPart : path) {
            sql.append(splitCapitals(pathPart));
            sql.append('_');
        }
        sql.append(splitCapitals(javaPersistenceFieldName));
        return sql.toString();
    }

    @Override
    public String sqlEmbededTableName(String javaPersistenceTableName, List<String> path, String javaPersistenceFieldName) {
        StringBuilder sql = new StringBuilder();
        sql.append(splitCapitals(javaPersistenceTableName));
        for (String pathPart : path) {
            sql.append(splitCapitals(pathPart));
            sql.append('_');
        }
        sql.append(splitCapitals(javaPersistenceFieldName));
        return sql.toString();
    }

}
