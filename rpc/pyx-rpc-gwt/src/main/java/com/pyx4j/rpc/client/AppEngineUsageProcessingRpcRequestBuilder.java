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
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

public class AppEngineUsageProcessingRpcRequestBuilder extends RpcRequestBuilder {

    public AppEngineUsageProcessingRpcRequestBuilder() {
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

    @Override
    protected void doSetCallback(RequestBuilder rb, RequestCallback callback) {
        ResponseHeaderRequestCallbackAdapter adapter = new ResponseHeaderRequestCallbackAdapter();
        adapter.callback = callback;
        super.doSetCallback(rb, adapter);
    }
}
