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

    private final ShortWords shortWords;

    public NamingConventionOracle() {
        this(-1, null);
    }

    public NamingConventionOracle(int identifierMaximumLength, ShortWords shortWords) {
        this.identifierMaximumLength = identifierMaximumLength;
        this.shortWords = shortWords;
    }

    public String splitCapitals(String word) {
        StringBuilder b = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        boolean wordStart = true;
        for (char c : word.toCharArray()) {
            if (c == '_') {
                if (currentWord.length() > 0) {
                    b.append(shortForm(currentWord.toString()));
                    currentWord = new StringBuilder();
                    wordStart = true;
                }
                b.append('_');
            } else if (Character.isUpperCase(c)) {
                if (!wordStart) {
                    b.append(shortForm(currentWord.toString()));
                    currentWord = new StringBuilder();
                    b.append('_');
                    wordStart = true;
                }
                currentWord.append(c);
            } else {
                wordStart = false;
                currentWord.append(Character.toUpperCase(c));
            }
        }

        if (currentWord.length() > 0) {
            b.append(shortForm(currentWord.toString()));
        }

        return b.toString();
    }

    public String shortForm(String word) {
        if (shortWords != null) {
            return shortWords.getShortForm(word);
        } else {
            return word;
        }
    }

    @Override
    public String sqlTableName(String javaPersistenceName) {
        return splitCapitals(javaPersistenceName);
    }

    @Override
    public String sqlTableSequenceName(String javaPersistenceName) {
        return splitCapitals(javaPersistenceName) + "_SEQ";
    }

    @Override
    public String sqlChildTableSequenceName(String tableName) {
        return tableName + "_SEQ";
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
