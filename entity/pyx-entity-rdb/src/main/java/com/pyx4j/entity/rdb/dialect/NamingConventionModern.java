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
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.util.List;

public class NamingConventionModern implements NamingConvention {

    private final int identifierMaximumLength;

    private final String childSeparator;

    public NamingConventionModern() {
        this.identifierMaximumLength = -1;
        this.childSeparator = "$";
    }

    public NamingConventionModern(int identifierMaximumLength, String childSeparator) {
        this.identifierMaximumLength = identifierMaximumLength;
        this.childSeparator = childSeparator;
    }

    @Override
    public String sqlTableName(String javaPersistenceName) {
        return javaPersistenceName;
    }

    @Override
    public String sqlChildTableName(String javaPersistenceTableName, String javaPersistenceChildTableName) {
        return sqlTableName(javaPersistenceTableName) + childSeparator + sqlTableName(javaPersistenceChildTableName);
    }

    @Override
    public String sqlFieldName(String javaPersistenceFieldName) {
        return javaPersistenceFieldName.replace('-', '_');
    }

    @Override
    public String sqlEmbededFieldName(List<String> path, String javaPersistenceFieldName) {
        StringBuilder sql = new StringBuilder();
        for (String pathPart : path) {
            sql.append(pathPart);
            sql.append('_');
        }
        sql.append(javaPersistenceFieldName);
        return sql.toString();
    }

    @Override
    public String sqlEmbededTableName(String javaPersistenceTableName, List<String> path, String javaPersistenceFieldName) {
        StringBuilder sql = new StringBuilder();
        sql.append(javaPersistenceTableName);
        for (String pathPart : path) {
            sql.append(pathPart);
            sql.append('_');
        }
        sql.append(javaPersistenceFieldName);
        return sql.toString();
    }

}
