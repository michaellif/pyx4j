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
package com.pyx4j.unit.runner;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.GCaseMeta;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.unit.shared.UnitTestInfo;
import com.pyx4j.unit.shared.UnitTestsServices;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class ServerTestRunner {

    private static final Logger log = LoggerFactory.getLogger(ServerTestRunner.class);

    private ServerTestRunner() {

    }

    public static void createAsync() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                log.error("Tests are not available", reason);
            }

            @Override
            public void onSuccess() {
                loadTestsList();
            }

        });
    }

    private static void loadTestsList() {

        final AsyncCallback<Vector<UnitTestInfo>> callback = new AsyncCallback<Vector<UnitTestInfo>>() {

            public void onFailure(Throwable t) {
                log.error("Can't load Server side TestsList", t);
                MessageDialog.error("Server side Tests", t.getMessage());
            }

            public void onSuccess(Vector<UnitTestInfo> result) {
                log.info("got {} server tests ", result.size());
                Collection<List<GCaseMeta>> testCasesGroups = new Vector<List<GCaseMeta>>();
                for (UnitTestInfo ti : result) {
                    List<GCaseMeta> testCases = new Vector<GCaseMeta>();
                    for (String testName : ti.getTestNames()) {
                        testCases.add(new ServerSideGCaseMeta(ti.getTestClassName(), testName));
                    }
                    testCasesGroups.add(testCases);
                }
                TestRunnerDialog dialogBox = new TestRunnerDialog(testCasesGroups);
                dialogBox.setCaption("JUnit Server Side");
                dialogBox.show();
            }
        };

        RPCManager.execute(UnitTestsServices.GetTestsList.class, null, callback);
    }
}
