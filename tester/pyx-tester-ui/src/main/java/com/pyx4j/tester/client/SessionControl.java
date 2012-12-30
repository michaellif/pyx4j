/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-12-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.core.client.GWT;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.tester.shared.TesterAuthenticationService;

public class SessionControl {

    public static void ensureSession() {
        ClientContext.obtainAuthenticationData(GWT.<AuthenticationService> create(TesterAuthenticationService.class), new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (!result) {
                    login();
                }
            }
        });
    }

    public static void login() {
        ClientContext.authenticate(GWT.<AuthenticationService> create(TesterAuthenticationService.class), null, new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }
        });
    }
}
