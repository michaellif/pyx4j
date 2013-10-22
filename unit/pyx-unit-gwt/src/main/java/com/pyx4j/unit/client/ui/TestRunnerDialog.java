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
 * Created on Dec 29, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.unit.client.ui;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import com.pyx4j.unit.client.GCaseMeta;
import com.pyx4j.unit.client.GUnitTester;
import com.pyx4j.widgets.client.dialog.Dialog;

public class TestRunnerDialog extends Dialog {

    public TestRunnerDialog() {
        this(GUnitTester.getAllGCaseMeta());
    }

    public TestRunnerDialog(Collection<List<GCaseMeta>> testCases) {
        this(new TestRunner(testCases));
    }

    public TestRunnerDialog(TestRunner runner) {
        super("JUnit Client Side", runner, runner);
        runner.setSize("100%", "100%");
    }

    public static void createAsync() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                //TODO
                //MessageDialog_v2.error("Error", "Tests are not available", reason);
            }

            @Override
            public void onSuccess() {
                TestRunnerDialog dialogBox = new TestRunnerDialog();
                dialogBox.show();
            }

        });
    }

}
