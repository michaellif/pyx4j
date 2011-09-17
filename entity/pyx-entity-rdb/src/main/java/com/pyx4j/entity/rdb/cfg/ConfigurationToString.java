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
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.cfg;

public class ConfigurationToString {

    public static String toString(Configuration conf) {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass           : ").append(conf.getClass().getName()).append("\n");
        b.append("connectionUrl                : ").append(conf.connectionUrl()).append("\n");
        b.append("dbHost                       : ").append(conf.dbHost()).append("\n");
        b.append("dbName                       : ").append(conf.dbName()).append("\n");
        b.append("userName                     : ").append(conf.userName()).append("\n");
        b.append("Multitenant                  : ").append(conf.isMultitenant()).append("\n");
        b.append("minPoolSize                  : ").append(conf.minPoolSize()).append("\n");
        b.append("maxPoolSize                  : ").append(conf.maxPoolSize()).append("\n");
        b.append("unreturnedConnectionTimeout  : ").append(conf.unreturnedConnectionTimeout()).append("\n");
        return b.toString();
    }
}
