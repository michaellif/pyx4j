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
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.serverside.rpc;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("serial")
public class UnitTestInfo implements Serializable {

    private String testClassName;

    private List<String> testNames;

    public UnitTestInfo() {

    }

    public UnitTestInfo(String testClassName) {
        this.testClassName = testClassName;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    public List<String> getTestNames() {
        return testNames;
    }

    public void setTestNames(List<String> testNames) {
        this.testNames = testNames;
    }

    public void addTestName(String testName) {
        if (this.testNames == null) {
            this.testNames = new Vector<String>();
        }
        this.testNames.add(testName);
    }

    @Override
    public boolean equals(Object obj) {
        return testClassName.equals(((UnitTestInfo) obj).getTestClassName());
    }
}
