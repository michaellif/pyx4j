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
package com.pyx4j.entity.rdb.oracle;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;
import com.pyx4j.entity.rdb.dialect.ShortWords;

public class TestConfigurationOracle extends com.pyx4j.entity.rdb.cfg.ConfigurationOracle {

    @Override
    public String dbHost() {
        return "localhost";
    }

    @Override
    public String dbName() {
        return "orcl";
    }

    @Override
    public String userName() {
        return "tst_entity";
    }

    @Override
    public String password() {
        return "tst_entity";
    }

    @Override
    public int minPoolSize() {
        return 1;
    }

    @Override
    public int maxPoolSize() {
        return 1;
    }

    @Override
    public int tablesItentityOffset() {
        return 569;
    }

    @Override
    public NamingConvention namingConvention() {
        ShortWords shortWords = new ShortWords();
        shortWords.add("TEST", "T");
        shortWords.add("ORGANIZATION", "ORG");
        shortWords.add("EMPLOYEE", "EMP");
        shortWords.add("DEPARTMENT", "DEPT");
        shortWords.add("DEPARTMENTS", "DEPTS");
        shortWords.add("REFERENCE", "RF");
        shortWords.add("REFERENCES", "RFS");
        shortWords.add("ARCHIVE", "ARC");
        shortWords.add("ENTITY", "ENT");
        shortWords.add("ENTITIES", "ENTS");
        shortWords.add("EMBEDDED", "EB");
        shortWords.add("FORCE", "FRC");
        shortWords.add("SORT", "SR");
        shortWords.add("SORTED", "SRT");
        shortWords.add("SORTABLE", "SRT");
        shortWords.add("STRING", "STR");
        shortWords.add("DETD", "D");
        shortWords.add("DETACHED", "DET");
        shortWords.add("READ", "R");
        shortWords.add("OWNED", "OW");
        shortWords.add("OWNER", "OR");
        shortWords.add("MAIN", "M");
        shortWords.add("MEMBER", "MBR");
        shortWords.add("HOLDER", "HLR");
        shortWords.add("CREATION", "CRN");
        shortWords.add("CONCRETE1", "C1");
        shortWords.add("CONCRETE2", "C2");
        shortWords.add("CONCRETE3", "C3");
        shortWords.add("ASSIGNED", "ASN");
        shortWords.add("PRINCIPAL", "PRNP");
        shortWords.add("UNIDIRECTIONAL", "UD");
        shortWords.add("BIDIRECTIONAL", "BD");
        shortWords.add("PARENT", "PA");
        shortWords.add("CHILD", "CLD");
        shortWords.add("CHILDREN", "CLDN");
        shortWords.add("AUTO", "A");
        shortWords.add("INVERSED", "R");
        shortWords.add("INVER", "R");
        shortWords.add("MANY", "M");
        shortWords.add("LIST", "LST");
        shortWords.add("VALUE", "VL");
        shortWords.add("AMOUNT", "AMT");
        return new NamingConventionOracle(30, shortWords);
    }

    @Override
    public int unreturnedConnectionTimeout() {
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            return 0;
        } else {
            return super.unreturnedConnectionTimeout();
        }
    }
}
