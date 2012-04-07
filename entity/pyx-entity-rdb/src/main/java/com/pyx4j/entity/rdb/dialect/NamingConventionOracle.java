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

    private final boolean mustBeginWithLetter;

    private final int identifierMaximumLength;

    private final ShortWords shortWords;

    private final boolean forceShortWords;

    private final char childSeparator;

    public NamingConventionOracle() {
        this(30, null);
    }

    public NamingConventionOracle(int identifierMaximumLength, ShortWords shortWords) {
        this(identifierMaximumLength, shortWords, false);
    }

    public NamingConventionOracle(int identifierMaximumLength, ShortWords shortWords, boolean forceShortWords) {
        this(identifierMaximumLength, shortWords, forceShortWords, true, '_');
    }

    public NamingConventionOracle(int identifierMaximumLength, ShortWords shortWords, boolean forceShortWords, boolean mustBeginWithLetter, char childSeparator) {
        this.identifierMaximumLength = identifierMaximumLength;
        this.shortWords = shortWords;
        this.forceShortWords = forceShortWords;
        this.mustBeginWithLetter = mustBeginWithLetter;
        this.childSeparator = childSeparator;
    }

    public String shortForm(String word) {
        if (shortWords != null) {
            return shortWords.getShortForm(word);
        } else {
            return word;
        }
    }

    protected String makeNameFragment(String word, boolean useShortForms) {
        if (useShortForms) {
            return shortForm(word);
        } else {
            return word;
        }
    }

    protected String makeName(String word, boolean useShortForms) {
        StringBuilder b = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        boolean wordStart = true;
        for (char c : word.toCharArray()) {
            if (c == '_' || c == childSeparator) {
                if (currentWord.length() > 0) {
                    b.append(makeNameFragment(currentWord.toString(), useShortForms));
                    currentWord = new StringBuilder();
                    wordStart = true;
                } else if (mustBeginWithLetter) {
                    continue;
                }
                b.append(c);
            } else if (Character.isUpperCase(c)) {
                if (!wordStart) {
                    b.append(makeNameFragment(currentWord.toString(), useShortForms));
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
            b.append(makeNameFragment(currentWord.toString(), useShortForms));
        }
        return b.toString();
    }

    protected String makeName(String name, int maximumLength) {
        String nameConverted = makeName(name, false);
        if ((!forceShortWords) && nameConverted.length() <= maximumLength) {
            return nameConverted;
        } else {
            nameConverted = makeName(name, true);
            if (nameConverted.length() > maximumLength) {
                throw new Error("Can't make name '" + nameConverted + "' shorter");
            }
            return nameConverted;
        }
    }

    protected String makeName(String name) {
        return makeName(name, identifierMaximumLength);
    }

    @Override
    public String sqlTableName(String javaPersistenceName) {
        return makeName(javaPersistenceName);
    }

    @Override
    public String sqlTableSequenceName(String javaPersistenceName) {
        return makeName(javaPersistenceName, identifierMaximumLength - 4) + "_SEQ";
    }

    @Override
    public String sqlTablePKName(String tableName) {
        return makeName(tableName, identifierMaximumLength - 3) + "_PK";
    }

    @Override
    public String sqlChildTableSequenceName(String tableName) {
        return makeName(tableName, identifierMaximumLength - 4) + "_SEQ";
    }

    @Override
    public String sqlTableIndexName(String tableName, List<String> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append(tableName);
        sql.append('_');
        for (String column : columns) {
            sql.append(column);
            sql.append('_');
        }
        sql.append("IDX");
        return makeName(sql.toString());
    }

    @Override
    public String sqlChildTableName(String javaPersistenceTableName, String javaPersistenceChildTableName) {
        return makeName(sqlTableName(javaPersistenceTableName) + childSeparator + sqlTableName(javaPersistenceChildTableName));
    }

    @Override
    public String sqlIdColumnName() {
        return "id";
    }

    @Override
    public String sqlNameSpaceColumnName() {
        return "ns";
    }

    @Override
    public String sqlAutoGeneratedJoinValueColumnName() {
        return "value";
    }

    @Override
    public String sqlAutoGeneratedJoinOwnerColumnName() {
        return "owner";
    }

    @Override
    public String sqlAutoGeneratedJoinOrderColumnName() {
        return "seq";
    }

    @Override
    public String sqlFieldName(String javaPersistenceFieldName) {
        return makeName(javaPersistenceFieldName).replace('-', '_');
    }

    @Override
    public String sqlEmbededFieldName(List<String> path, String javaPersistenceFieldName) {
        StringBuilder sql = new StringBuilder();
        for (String pathPart : path) {
            sql.append(makeName(pathPart));
            sql.append('_');
        }
        sql.append(makeName(javaPersistenceFieldName));
        return makeName(sql.toString());
    }

    @Override
    public String sqlEmbededTableName(String javaPersistenceTableName, List<String> path, String javaPersistenceFieldName) {
        StringBuilder sql = new StringBuilder();
        sql.append(makeName(javaPersistenceTableName));
        for (String pathPart : path) {
            sql.append(makeName(pathPart));
            sql.append(childSeparator);
        }
        sql.append(makeName(javaPersistenceFieldName));
        return makeName(sql.toString());
    }

    @Override
    public String sqlForeignKeyName(String tableFrom, String indexColName, String tableTo) {
        StringBuilder sql = new StringBuilder();
        sql.append(makeName(tableFrom));
        sql.append('_');
        sql.append(makeName(indexColName));
        sql.append("_Fk");
        return makeName(sql.toString());
    }

}
