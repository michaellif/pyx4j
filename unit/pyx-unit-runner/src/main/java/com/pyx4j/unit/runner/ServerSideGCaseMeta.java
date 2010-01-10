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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.GCaseMeta;
import com.pyx4j.unit.client.GCaseResultAsyncCallback;
import com.pyx4j.unit.client.GResult;
import com.pyx4j.unit.shared.UnitTestExecuteRequest;
import com.pyx4j.unit.shared.UnitTestResult;
import com.pyx4j.unit.shared.UnitTestsServices;

public class ServerSideGCaseMeta implements GCaseMeta {

    private final String className;

    private final String testName;

    public ServerSideGCaseMeta(String className, String testName) {
        this.className = className;
        this.testName = testName;
    }

    @Override
    public String getTestClassName() {
        return className;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public void execute(final GCaseResultAsyncCallback callback) {
        final AsyncCallback<UnitTestResult> rpcCallback = new AsyncCallback<UnitTestResult>() {

            public void onFailure(Throwable t) {
                callback.onComplete(new GResult(false, t.getMessage(), 0));
            }

            public void onSuccess(UnitTestResult result) {
                callback.onComplete(new GResult(result.isSuccess(), result.getExceptionMessage(), result.getDuration()));
            }
        };

        UnitTestExecuteRequest request = new UnitTestExecuteRequest(getTestClassName(), getTestName());

        RPCManager.execute(UnitTestsServices.ExectuteTest.class, request, rpcCallback);
    }

}
