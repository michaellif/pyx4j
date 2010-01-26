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
 * Created on Jan 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.shared.UserVisit;

public class ClientContext {

    private static Logger log = LoggerFactory.getLogger(ClientContext.class);

    private static UserVisit userVisit;

    private ClientContext() {

    }

    public static UserVisit getUserVisit() {
        return userVisit;
    }

    public static boolean isAuthenticated() {
        return userVisit != null;
    }

    public static void authenticated(AuthenticationResponse authenticationResponse) {
        userVisit = authenticationResponse.getUserVisit();
        log.info("Authenticated {}", userVisit);
        ClientSecurityController.instance().authenticate(authenticationResponse.getBehaviors());
    }

    /**
     * Keep in mind when changing URL just after this call, that some fast bowers like
     * Chrome and Safari would not execute RPC call and session would still be active.
     */
    public static void logout(final AsyncCallback<AuthenticationResponse> callback) {
        AsyncCallback<AuthenticationResponse> defaultCallback = new AsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                if (callback == null) {
                    log.error("Logout failure", caught);
                } else {
                    callback.onFailure(caught);
                }
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
        };
        RPCManager.execute(AuthenticationServices.Logout.class, null, defaultCallback);
    }

    public static void obtainAuthenticationData() {
        AsyncCallback<AuthenticationResponse> callback = new AsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                log.error("Logout failure", caught);
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }
        };
        RPCManager.executeBackground(AuthenticationServices.GetStatus.class, null, callback);
    }
}
