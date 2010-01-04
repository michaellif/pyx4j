/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
