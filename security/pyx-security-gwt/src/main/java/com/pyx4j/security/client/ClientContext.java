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

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.UserVisit;

public class ClientContext {

    private static Logger log = LoggerFactory.getLogger(ClientContext.class);

    private static UserVisit userVisit;

    private static boolean authenticationObtained = false;

    private static String logoutURL;

    private ClientContext() {

    }

    public static UserVisit getUserVisit() {
        return userVisit;
    }

    public static boolean isAuthenticated() {
        return userVisit != null;
    }

    public static void authenticated(AuthenticationResponse authenticationResponse) {
        authenticationObtained = true;
        userVisit = authenticationResponse.getUserVisit();
        if (authenticationResponse.getLogoutURL() != null) {
            logoutURL = authenticationResponse.getLogoutURL();
        }
        log.info("Authenticated {}", userVisit);
        ClientSecurityController.instance().authenticate(authenticationResponse.getBehaviors());
        if (ClientSecurityController.checkBehavior(CoreBehavior.DEVELOPER)) {
            RPCManager.enableAppEngineUsageStats();
        }
    }

    /**
     * Keep in mind when changing URL just after this call, that some fast bowers like
     * Chrome and Safari would not execute RPC call and session would still be active.
     */
    public static void logout(final AsyncCallback<AuthenticationResponse> callback) {
        logout("/", callback);
    }

    public static void logout(String logoutApplicationUrl, final AsyncCallback<AuthenticationResponse> callback) {
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
        RPCManager.execute(AuthenticationServices.Logout.class, logoutApplicationUrl, defaultCallback);
    }

    public static String getCurrentURL() {
        if (History.getToken() != null) {
            return Window.Location.getPath() + Window.Location.getQueryString() + "#" + History.getToken();
        } else {
            return Window.Location.getPath() + Window.Location.getQueryString();
        }
    }

    public static String getLogoutURL() {
        return logoutURL;
    }

    public static void googleAccountsLogin() {
        googleAccountsLogin(getCurrentURL());
    }

    public static void googleAccountsLogin(final String destinationURLComponent) {
        logout(new AsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                googleLogin();
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                googleLogin();
            }

            private void googleLogin() {
                RPCManager.execute(AuthenticationServices.GetGoogleAccountsLoginUrl.class, destinationURLComponent, new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        log.error("Get LoginUrl failure", caught);
                    }

                    @Override
                    public void onSuccess(String result) {
                        Window.Location.replace(result);
                    }
                });
            }
        }

        );
    }

    public static void obtainAuthenticationData() {
        obtainAuthenticationData(null);
    }

    public static void obtainAuthenticationData(final Runnable onAuthenticationAvalable) {
        if (authenticationObtained) {
            if (onAuthenticationAvalable != null) {
                onAuthenticationAvalable.run();
            }
        } else {
            AsyncCallback<AuthenticationResponse> callback = new AsyncCallback<AuthenticationResponse>() {

                @Override
                public void onFailure(Throwable caught) {
                    log.error("obtain authentication failure", caught);
                    authenticationObtained = true;
                    if (onAuthenticationAvalable != null) {
                        onAuthenticationAvalable.run();
                    }
                }

                @Override
                public void onSuccess(AuthenticationResponse result) {
                    ClientContext.authenticated(result);
                    if (onAuthenticationAvalable != null) {
                        onAuthenticationAvalable.run();
                    }
                }
            };
            RPCManager.executeBackground(AuthenticationServices.GetStatus.class, null, callback);
        }
    }

}
