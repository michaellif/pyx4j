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
 * Created on 2011-03-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.selenium;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.unit.shared.UnitDebugId;
import com.pyx4j.widgets.client.dialog.DialogDebugId;

public abstract class UnitTestExecutionTestBase extends SeleniumTestBase {

    protected void executeAllClientUnitTests(int waitSeconds) {
        selenium.waitFor(UnitDebugId.JUnit_StartClientTests);
        selenium.click(UnitDebugId.JUnit_StartClientTests);
        selenium.waitFor(UnitDebugId.JUnit_SelectAll);
        selenium.setValue(UnitDebugId.JUnit_SelectAll, true);
        selenium.click(DialogDebugId.Dialog_Ok);
        selenium.waitForText(UnitDebugId.JUnit_StatusRunning, "Completed", waitSeconds);
    }

    protected static class ExecutionStatus {

        public int success;

        public int failed;

        public String duration;

    }

    int parsString(String value) {
        if (CommonsStringUtils.isEmpty(value)) {
            return 0;
        } else {
            return Integer.valueOf(value).intValue();
        }
    }

    protected ExecutionStatus getExecutionStatus() {

        ExecutionStatus s = new ExecutionStatus();

        s.success = parsString(selenium.getText(UnitDebugId.JUnit_ResultsSuccess));
        s.failed = parsString(selenium.getText(UnitDebugId.JUnit_ResultsFailed));
        s.duration = selenium.getText(UnitDebugId.JUnit_ResultsDuration);

        return s;
    }
}
