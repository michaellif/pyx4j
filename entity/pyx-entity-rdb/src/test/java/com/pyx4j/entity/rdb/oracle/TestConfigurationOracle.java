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

import com.pyx4j.entity.rdb.TestsConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
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
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return new TestsConnectionPoolConfiguration(connectionType);
    }

    @Override
    public int tablesIdentityOffset() {
        return 569;
    }

    @Override
    public NamingConvention namingConvention() {
        ShortWords shortWords = new ShortWords();
        shortWords.add("TEST", "T");
        shortWords.add("BASE", "B");
        shortWords.add("ASSOCIATION2", "ASC2");
        shortWords.add("ORGANIZATION", "ORG");
        shortWords.add("ORGANIZATION2", "ORG2");
        shortWords.add("EMPLOYEE", "EMP");
        shortWords.add("DEPARTMENT", "DEPT");
        shortWords.add("DEPARTMENTS", "DEPTS");
        shortWords.add("DISCRIMINATOR", "DC");
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
        shortWords.add("DIFERENT", "DIF");
        shortWords.add("DATA", "DT");
        shortWords.add("DATA1", "DT1");
        shortWords.add("READ", "R");
        shortWords.add("OWNED", "OW");
        shortWords.add("OWNER", "O");
        shortWords.add("ONE", "1");
        shortWords.add("POLY", "PL");
        shortWords.add("POLYMORPHIC", "P8");
        shortWords.add("PLM", "P8");
        shortWords.add("MAIN", "M");
        shortWords.add("MEMBER", "MBR");
        shortWords.add("MANAGED", "M");
        shortWords.add("HOLDER", "HLR");
        shortWords.add("CREATION", "CRN");
        shortWords.add("CONCRETE1", "R1");
        shortWords.add("CONCRETE2", "R2");
        shortWords.add("CONCRETE3", "R3");
        shortWords.add("ASSIGNED", "ASN");
        shortWords.add("PRINCIPAL", "PRNP");
        shortWords.add("BIDIRECTIONAL", "BD");
        shortWords.add("UNIDIRECTIONAL", "UD");
        shortWords.add("UNMAINTAINED", "UM");
        shortWords.add("PARENT", "PA");
        shortWords.add("CPARENT", "CPA");
        shortWords.add("STPARENT", "STPA");
        shortWords.add("CHILD", "C1");
        shortWords.add("CCHILD", "C2");
        shortWords.add("STCHILD", "SCL");
        shortWords.add("NCPCHILD", "NCPC1");
        shortWords.add("CHILDREN", "C3");
        shortWords.add("AUTO", "A");
        shortWords.add("INVERSED", "R");
        shortWords.add("INVER", "R");
        shortWords.add("MANY", "M");
        shortWords.add("MANY2", "M2");
        shortWords.add("LIST", "LS");
        shortWords.add("VALUE", "VL");
        shortWords.add("AMOUNT", "AMT");

        return new NamingConventionOracle(30, shortWords);
    }
}
