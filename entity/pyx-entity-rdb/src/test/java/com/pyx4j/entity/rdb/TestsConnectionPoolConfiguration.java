/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-04-13
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class TestsConnectionPoolConfiguration extends ConnectionPoolConfiguration {

    static final ThreadLocal<Integer> overrideUnreturnedConnectionTimeout = new ThreadLocal<Integer>();

    public TestsConnectionPoolConfiguration(ConnectionPoolType connectionType) {
        super(connectionType);
        minPoolSize = 1;
        maxPoolSize = 5;
        if (overrideUnreturnedConnectionTimeout.get() != null) {
            unreturnedConnectionTimeout = overrideUnreturnedConnectionTimeout.get();
        }
        if (unreturnedConnectionTimeout != 0) {
            switch (connectionType) {
            case BackgroundProcess:
                unreturnedConnectionTimeout = 1 * Consts.MIN2SEC + 20;
                break;
            case TransactionProcessing:
                unreturnedConnectionTimeout = 1 * Consts.MIN2SEC + 10;
                break;
            default:
                break;
            }
        }
    }

}
