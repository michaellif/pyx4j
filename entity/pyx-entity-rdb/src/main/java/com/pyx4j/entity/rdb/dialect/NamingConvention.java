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

public interface NamingConvention {

    public String sqlTableName(String javaPersistenceName);

    public String sqlTableSequenceName(String javaPersistenceName);

    /**
     * Defines the name for table use in OneToMany associations.
     * 
     * TODO find a better name
     */
    public String sqlChildTableName(String javaPersistenceTableName, String javaPersistenceChildTableName);

    public String sqlFieldName(String javaPersistenceFieldName);

    public String sqlEmbededFieldName(List<String> path, String javaPersistenceFieldName);

    public String sqlEmbededTableName(String javaPersistenceTableName, List<String> path, String javaPersistenceFieldName);

    public String sqlChildTableSequenceName(String tableName);

}
