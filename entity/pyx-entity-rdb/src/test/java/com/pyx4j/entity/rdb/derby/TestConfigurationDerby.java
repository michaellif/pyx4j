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
 * Created on Jul 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.derby;

import com.pyx4j.entity.rdb.RDBDatastorePersistenceEnvironment;
import com.pyx4j.entity.rdb.TestsConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionModern;
import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;
import com.pyx4j.entity.rdb.dialect.ShortWords;

public class TestConfigurationDerby extends com.pyx4j.entity.rdb.cfg.ConfigurationDerby {

    @Override
    public String dbName() {
        return "tst_entity";
    }

    @Override
    public String dbHost() {
        return null;
    }

    @Override
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return new TestsConnectionPoolConfiguration(connectionType);
    }

    @Override
    public boolean sequencesBaseIdentity() {
        //TODO fix sequences support
        return false;
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return RDBDatastorePersistenceEnvironment.getTestsMultitenancyType();
    }

    @Override
    public int tablesIdentityOffset() {
        return 997;
    }

    @Override
    public NamingConvention namingConvention() {
        ShortWords shortWords = new ShortWords();
        shortWords.add("TEST", "T");
        shortWords.add("PARENT", "PA");
        shortWords.add("HOLDER", "HLR");
        shortWords.add("POLYMORPHIC", "PLM");
        shortWords.add("DISCRIMINATOR", "DSCR");

        //TODO testDiferentNamingConvention
        boolean testDiferentNamingConvention = false;
        if (testDiferentNamingConvention) {
            return new NamingConventionModern(64, "_");
        } else {
            return new NamingConventionOracle(64, shortWords);
        }
    }

}
