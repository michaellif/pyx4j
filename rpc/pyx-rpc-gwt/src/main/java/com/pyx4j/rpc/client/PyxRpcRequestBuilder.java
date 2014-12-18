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
 * Created on Jan 31, 2010
 * @author vlads
 */
package com.pyx4j.rpc.client;

import java.io.Serializable;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.rpc.shared.RemoteService;

public class PyxRpcRequestBuilder extends RpcRequestBuilder {

    private boolean collectAppEngineUsageStats;

    private String serviceInterfaceMarker;

    private String sessionAclTimeStamp;

    private String sessionToken;

    public PyxRpcRequestBuilder() {
    }

    private static class ResponseHeaderRequestCallbackAdapter implements RequestCallback {

        RequestCallback callback;

        @Override
        public void onError(Request request, Throwable exception) {
            callback.onError(request, exception);
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            callback.onResponseReceived(request, response);
            AppEngineUsage.onResponseReceived(response);
        }
    }

    /**
     * Mark service entry point with service name to be able to view statistic in GAE
     * console.
     */
    void executing(String serviceInterfaceName, Serializable request) {
        int simpleNameIdx = serviceInterfaceName.lastIndexOf(".");
        if (simpleNameIdx == -1) {
            serviceInterfaceMarker = serviceInterfaceName;
        } else {
            serviceInterfaceMarker = serviceInterfaceName.substring(simpleNameIdx + 1).replace('$', '.');
        }
        if (request instanceof IHaveServiceCallMarker) {
            serviceInterfaceMarker += "/" + ((IHaveServiceCallMarker) request).getServiceCallMarker();
        }
    }

    void setSessionToken(String sessionToken, String sessionAclTimeStamp) {
        this.sessionToken = sessionToken;
        this.sessionAclTimeStamp = sessionAclTimeStamp;
    }

    @Override
    protected RequestBuilder doCreate(String serviceEntryPoint) {
        RequestBuilder rb = super.doCreate(serviceEntryPoint + "/" + serviceInterfaceMarker);
        if (sessionToken != null) {
            rb.setHeader(RemoteService.SESSION_TOKEN_HEADER, sessionToken);
        }
        if (sessionAclTimeStamp != null) {
            rb.setHeader(RemoteService.SESSION_ACL_TIMESTAMP_HEADER, sessionAclTimeStamp);
        }
        // Hack to solve HTTP Status 12152
        if (BrowserType.isIE()) {
            rb.setHeader("Connection", "close");
        }
        return rb;
    }

    void enableAppEngineUsageStats() {
        collectAppEngineUsageStats = true;
    }

    @Override
    protected void doSetCallback(RequestBuilder rb, RequestCallback callback) {
        if (collectAppEngineUsageStats) {
            ResponseHeaderRequestCallbackAdapter adapter = new ResponseHeaderRequestCallbackAdapter();
            adapter.callback = callback;
            super.doSetCallback(rb, adapter);
        } else {
            super.doSetCallback(rb, callback);
        }
    }
}
