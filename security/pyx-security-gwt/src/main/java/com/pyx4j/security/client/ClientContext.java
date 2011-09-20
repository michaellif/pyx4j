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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableBlockingAsyncCallback;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.security.rpc.UserVisitChangedSystemNotification;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.UserVisit;

public class ClientContext {

    public static String USER_VISIT_ATTRIBUTE = "UserVisit";

    public static String TOKEN_STORAGE_ATTRIBUTE = "stk";

    private static Logger log = LoggerFactory.getLogger(ClientContext.class);

    public static class ServerSession {

        private final String sessionCookieName;

        private final int maxInactiveInterval;

        public ServerSession(String sessionCookieName, int maxInactiveInterval) {
            this.sessionCookieName = sessionCookieName;
            this.maxInactiveInterval = maxInactiveInterval;
        }

        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        public String getSessionCookieName() {
            return sessionCookieName;
        }

    }

    private static UserVisit userVisit;

    private static ServerSession serverSession;

    private static AuthenticationService service;

    private static boolean authenticationObtained = false;

    private static List<AsyncCallback<Boolean>> onAuthenticationAvailableQueue = null;

    private static String logoutURL;

    private static String sessionToken;

    private static final Map<String, Object> attributes = new HashMap<String, Object>();

    private static EventBus eventBus;

    static {
        RPCManager.addSystemNotificationHandler(new SystemNotificationHandler() {
            @Override
            public void onSystemNotificationReceived(SystemNotificationEvent event) {
                if (event.getSystemNotification() instanceof AuthorizationChangedSystemNotification) {
                    if (((AuthorizationChangedSystemNotification) event.getSystemNotification()).isSessionTerminated()) {
                        log.debug("Session terminated");
                        ClientContext.terminateSession();
                    } else {
                        log.debug("Authorization Changed");
                        ClientContext.obtainAuthenticationData(null, null, true, false);
                    }
                } else if (event.getSystemNotification() instanceof UserVisitChangedSystemNotification) {
                    userVisit = ((UserVisitChangedSystemNotification) event.getSystemNotification()).getUserVisit();
                    if (userVisit != null) {
                        RPCManager.setUserVisitHashCode(userVisit.getServerSideHashCode());
                    } else {
                        RPCManager.setUserVisitHashCode(null);
                    }
                    if (eventBus != null) {
                        eventBus.fireEvent(new ContextChangeEvent(USER_VISIT_ATTRIBUTE, userVisit));
                    }
                }
            }
        });
    }

    private ClientContext() {

    }

    public static UserVisit getUserVisit() {
        return userVisit;
    }

    public static int visitHashCode() {
        if (userVisit == null) {
            return -1;
        } else {
            return userVisit.hashCode() * 0x1F + ClientSecurityController.instance().getAcl().hashCode();
        }
    }

    public static boolean isAuthenticated() {
        return userVisit != null;
    }

    public static boolean hasServerSession() {
        return serverSession != null;
    }

    public static ServerSession getServerSession() {
        return serverSession;
    }

    public static String getSessionToken() {
        return sessionToken;
    }

    public static Object getAttribute(String name) {
        return attributes.get(name);
    }

    public static Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    public static Object removeAttribute(String name) {
        Object prev = attributes.remove(name);
        if (eventBus != null) {
            eventBus.fireEvent(new ContextChangeEvent(name, null));
        }
        return prev;
    }

    public static void setAttribute(String name, Object value) {
        attributes.put(name, value);
        if (eventBus != null) {
            eventBus.fireEvent(new ContextChangeEvent(name, value));
        }
    }

    public static HandlerRegistration addContextChangeHandler(ContextChangeHandler handler) {
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(ContextChangeEvent.TYPE, handler);
    }

    public static void authenticated(AuthenticationResponse authenticationResponse) {
        authenticationObtained = true;
        userVisit = authenticationResponse.getUserVisit();
        if (authenticationResponse.getLogoutURL() != null) {
            logoutURL = authenticationResponse.getLogoutURL();
        }
        if (authenticationResponse.getSessionCookieName() != null) {
            serverSession = new ServerSession(authenticationResponse.getSessionCookieName(), authenticationResponse.getMaxInactiveInterval());
        } else {
            // Session ends.
            if ((serverSession != null) && (serverSession.getSessionCookieName() != null)) {
                RPCManager.setSessionToken(null, null);
                Cookies.removeCookie(serverSession.getSessionCookieName());
            }
            serverSession = null;
        }
        String sessionToken = authenticationResponse.getSessionToken();
        if (sessionToken != null) {
            if (sessionToken.equals("")) {
                ClientContext.sessionToken = null;
            } else {
                ClientContext.sessionToken = sessionToken;
            }
            RPCManager.setSessionToken(ClientContext.sessionToken, authenticationResponse.getAclTimeStamp());
        }
        if (userVisit != null) {
            RPCManager.setUserVisitHashCode(userVisit.getServerSideHashCode());
        } else {
            RPCManager.setUserVisitHashCode(null);
        }
        log.info("Authenticated {}", userVisit);
        attributes.clear();
        ClientSecurityController.instance().authenticate(authenticationResponse.getBehaviors());
        if (eventBus != null) {
            eventBus.fireEvent(new ContextChangeEvent(USER_VISIT_ATTRIBUTE, userVisit));
        }
        if (Storage.isSupported()) {
            Storage.getLocalStorageIfSupported().setItem(TOKEN_STORAGE_ATTRIBUTE, sessionToken);
        }
        if (ClientSecurityController.checkBehavior(CoreBehavior.DEVELOPER)) {
            RPCManager.enableAppEngineUsageStats();
        }
    }

    /**
     * Generally called when logout call to server failed e.g. Server is down or GAE read
     * only Maintenance
     */
    public static void terminateSession() {
        log.error("terminateSession");
        userVisit = null;
        attributes.clear();
        RPCManager.setSessionToken(null, null);
        RPCManager.setUserVisitHashCode(null);
        if ((serverSession != null) && (serverSession.getSessionCookieName() != null)) {
            Cookies.removeCookie(serverSession.getSessionCookieName());
        }
        serverSession = null;
        ClientSecurityController.instance().authenticate(null);
        if (eventBus != null) {
            eventBus.fireEvent(new ContextChangeEvent(USER_VISIT_ATTRIBUTE, null));
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
                    ClientContext.terminateSession();
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
                        log.debug("go {}", result);
                        Window.Location.replace(result);
                    }
                });
            }
        }

        );
    }

    public static HandlerRegistration addAuthenticationObtainedHandler(InitializeHandler handler) {
        return ClientSecurityController.instance().addInitializeHandler(handler);
    }

    public static boolean isAuthenticationObtained() {
        return authenticationObtained;
    }

    public static void obtainAuthenticationData() {
        obtainAuthenticationData(null);
    }

    public static void obtainAuthenticationData(final AsyncCallback<Boolean> onAuthenticationAvailable) {
        obtainAuthenticationData(null, onAuthenticationAvailable, false, true);
    }

    public static void obtainAuthenticationData(AuthenticationService authenticationService, final AsyncCallback<Boolean> onAuthenticationAvailable) {
        obtainAuthenticationData(authenticationService, onAuthenticationAvailable, false, true);
    }

    public static void obtainAuthenticationData(AuthenticationService authenticationService, final AsyncCallback<Boolean> onAuthenticationAvailable,
            boolean force, boolean executeBackground) {
        if (authenticationService != null) {
            service = authenticationService;
        }
        if (!force && authenticationObtained) {
            if (onAuthenticationAvailable != null) {
                onAuthenticationAvailable.onSuccess(isAuthenticated());
            }
        } else {
            if (onAuthenticationAvailableQueue != null) {
                if (onAuthenticationAvailable != null) {
                    onAuthenticationAvailableQueue.add(onAuthenticationAvailable);
                }
                return;
            }

            onAuthenticationAvailableQueue = new Vector<AsyncCallback<Boolean>>();

            AsyncCallback<AuthenticationResponse> callback = new RecoverableBlockingAsyncCallback<AuthenticationResponse>() {

                @Override
                public void onFailure(Throwable caught) {
                    log.error("obtain authentication failure", caught);
                    ClientContext.authenticated(new AuthenticationResponse());
                    if (onAuthenticationAvailable != null) {
                        onAuthenticationAvailable.onFailure(caught);
                    }
                    if (onAuthenticationAvailableQueue != null) {
                        for (AsyncCallback<Boolean> queued : onAuthenticationAvailableQueue) {
                            queued.onFailure(caught);
                        }
                        onAuthenticationAvailableQueue = null;
                    }
                }

                @Override
                public void onSuccess(AuthenticationResponse result) {
                    ClientContext.authenticated(result);
                    if (onAuthenticationAvailable != null) {
                        onAuthenticationAvailable.onSuccess(isAuthenticated());
                    }
                    if (onAuthenticationAvailableQueue != null) {
                        for (AsyncCallback<Boolean> queued : onAuthenticationAvailableQueue) {
                            queued.onSuccess(isAuthenticated());
                        }
                        onAuthenticationAvailableQueue = null;
                    }
                }
            };

            if (service != null) {
                String sessionToken = null;
                if (Storage.isSupported()) {
                    sessionToken = Storage.getLocalStorageIfSupported().getItem(TOKEN_STORAGE_ATTRIBUTE);
                }
                log.debug("authenticate {}", sessionToken);
                service.authenticate(callback, sessionToken);
            } else {
                if (executeBackground) {
                    RPCManager.executeBackground(AuthenticationServices.GetStatus.class, null, callback);
                } else {
                    RPCManager.execute(AuthenticationServices.GetStatus.class, null, callback);
                }
            }
        }
    }

}
