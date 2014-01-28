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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.client.ClientApplicationBackendConfig;
import com.pyx4j.config.client.ClientApplicationVersion;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableBlockingAsyncCallback;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.security.rpc.UserVisitChangedSystemNotification;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.webstorage.client.HTML5Storage;

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

        /**
         * @return an integer specifying the number of seconds this session remains open
         *         between client requests
         */
        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        public String getSessionCookieName() {
            return sessionCookieName;
        }

    }

    private static UserVisit userVisit;

    private static ServerSession serverSession;

    private static AuthenticationService authenticationService;

    private static boolean authenticationObtained = false;

    private static List<AsyncCallback<Boolean>> onAuthenticationAvailableQueue = null;

    private static String logoutURL;

    private static String sessionToken;

    private static final Map<String, Object> attributes = new HashMap<String, Object>();

    private static ClientSystemInfo clientSystemInfo;

    private static SystemWallMessage systemWallMessage;

    private static String enviromentName;

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
                        ClientContext.obtainAuthenticationData(null, null, true, null);
                    }
                } else if (event.getSystemNotification() instanceof UserVisitChangedSystemNotification) {
                    log.debug("UserVisit Changed");
                    userVisit = ((UserVisitChangedSystemNotification) event.getSystemNotification()).getUserVisit();
                    if (userVisit != null) {
                        RPCManager.setUserVisitHashCode(userVisit.getServerSideHashCode());
                    } else {
                        RPCManager.setUserVisitHashCode(null);
                    }
                    ClientEventBus.fireEvent(new ContextChangeEvent(USER_VISIT_ATTRIBUTE, userVisit));
                } else if (event.getSystemNotification() instanceof SystemWallMessage) {
                    systemWallMessage = (SystemWallMessage) event.getSystemNotification();
                }
            }
        });

        clientSystemInfo = new ClientSystemInfo();
        clientSystemInfo.setScript(GWT.isScript());
        clientSystemInfo.setStartTime(System.currentTimeMillis());
        clientSystemInfo.setUserAgent(BrowserType.getUserAgent());
        clientSystemInfo.setTimeZoneInfo(TimeUtils.getTimeZoneInfo());
        clientSystemInfo.setBuildLabel(ClientApplicationVersion.instance().getBuildLabel());
    }

    private ClientContext() {

    }

    public static UserVisit getUserVisit() {
        return userVisit;
    }

    /**
     * Convenience method to access custom UserVisit
     * 
     * @param userVisitClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E extends UserVisit> E getUserVisit(Class<E> userVisitClass) {
        return (E) getUserVisit();
    }

    public static ClientSystemInfo getClientSystemInfo() {
        return clientSystemInfo;
    }

    public static Date getServerDate() {
        long timeDelta = ClientContext.getClientSystemInfo().getServerTimeDelta();
        return new Date(System.currentTimeMillis() - timeDelta);
    }

    public static int visitHashCode() {
        if (userVisit == null) {
            return -1;
        } else {
            return userVisit.hashCode();
            //TODO fix this
            /** 0x1F + ClientSecurityController.instance().getAcl().hashCode(); */
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
        ClientEventBus.fireEvent(new ContextChangeEvent(name, null));
        return prev;
    }

    public static void setAttribute(String name, Object value) {
        attributes.put(name, value);
        ClientEventBus.fireEvent(new ContextChangeEvent(name, value));
    }

    public static SystemWallMessage getSystemWallMessage() {
        return systemWallMessage;
    }

    public static String getEnviromentName() {
        return enviromentName;
    }

    public static HandlerRegistration addContextChangeHandler(ContextChangeHandler handler) {
        return ClientEventBus.addHandler(ContextChangeEvent.TYPE, handler);
    }

    static boolean authenticationChanging = false;

    private static String googleAnalyticsKey;

    public static void authenticated(AuthenticationResponse authenticationResponse) {
        try {
            authenticationObtained = true;
            userVisit = authenticationResponse.getUserVisit();
            if (authenticationResponse.getLogoutURL() != null) {
                logoutURL = authenticationResponse.getLogoutURL();
            }
            if (authenticationResponse.getSessionCookieName() != null) {
                log.debug("Session MaxInactiveInterval {} sec", authenticationResponse.getMaxInactiveInterval());
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
            if (HTML5Storage.isSupported()) {
                HTML5Storage.getLocalStorage().setItem(TOKEN_STORAGE_ATTRIBUTE, sessionToken);
            }
            if (userVisit != null) {
                RPCManager.setUserVisitHashCode(userVisit.getServerSideHashCode());
            } else {
                RPCManager.setUserVisitHashCode(null);
            }
            log.info("Authenticated {}", userVisit);
            attributes.clear();
            ClientSecurityController.instance().authorize(authenticationResponse.getBehaviors());
            ClientEventBus.fireEvent(new ContextChangeEvent(USER_VISIT_ATTRIBUTE, userVisit));
            if (ClientSecurityController.checkBehavior(CoreBehavior.DEVELOPER)) {
                RPCManager.enableAppEngineUsageStats();
            }
            systemWallMessage = authenticationResponse.getSystemWallMessage();
            enviromentName = authenticationResponse.getEnviromentName();
            googleAnalyticsKey = authenticationResponse.getGoogleAnalyticsKey();
        } finally {
            authenticationChanging = false;
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
        ClientSecurityController.instance().authorize(null);
        ClientEventBus.fireEvent(new ContextChangeEvent(USER_VISIT_ATTRIBUTE, null));
    }

    public static AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public static void setAuthenticationService(AuthenticationService authenticationService) {
        ClientContext.authenticationService = authenticationService;
    }

    private static AuthenticationService select(AuthenticationService authenticationService) {
        if (authenticationService == null) {
            return ClientContext.authenticationService;
        } else {
            return authenticationService;
        }
    }

    /**
     * Keep in mind when changing URL just after this call, that some fast bowers like
     * Chrome and Safari would not execute RPC call and session would still be active.
     */

    public static void logout(final AsyncCallback<AuthenticationResponse> callback) {
        logout(null, "/", callback);
    }

    public static void logout(AuthenticationService authenticationService, final AsyncCallback<AuthenticationResponse> callback) {
        logout(authenticationService, "/", callback);
    }

    public static void logout(AuthenticationService authenticationService, String logoutApplicationUrl, final AsyncCallback<AuthenticationResponse> callback) {
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
        select(authenticationService).logout(defaultCallback);
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

    public static String getGoogleAnalyticsKey() {
        return googleAnalyticsKey;
    }

    public static void googleAccountsLogin(AuthenticationService authenticationService) {
        googleAccountsLogin(authenticationService, getCurrentURL());
    }

    public static void googleAccountsLogin(final AuthenticationService authenticationService, final String destinationURLComponent) {
        logout(authenticationService, new AsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                googleLogin();
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                googleLogin();
            }

            private void googleLogin() {
                authenticationService.getLoginUrl(new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        log.error("Get LoginUrl failure", caught);
                    }

                    @Override
                    public void onSuccess(String result) {
                        log.debug("go {}", result);
                        Window.Location.replace(result);
                    }
                }, destinationURLComponent);
            }
        });
    }

    public static boolean isAuthenticationObtained() {
        return authenticationObtained;
    }

    public static void obtainAuthenticationData() {
        obtainAuthenticationData(null);
    }

    public static void obtainAuthenticationData(final AsyncCallback<Boolean> onAuthenticationAvailable) {
        obtainAuthenticationData(null, onAuthenticationAvailable, false, null);
    }

    public static void obtainAuthenticationData(AuthenticationService authenticationService, final AsyncCallback<Boolean> onAuthenticationAvailable) {
        obtainAuthenticationData(authenticationService, onAuthenticationAvailable, false, null);
    }

    public static void obtainAuthenticationData(AuthenticationService authenticationService, final AsyncCallback<Boolean> onAuthenticationAvailable,
            boolean force, String authenticationToken) {
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
                    ClientContext.getClientSystemInfo().setServerTimeDelta(System.currentTimeMillis() - result.getServertTime());
                    ClientApplicationBackendConfig.setProductionBackend(result.isProductionBackend());
                    log.debug("Client/Server time delta {}", ClientContext.getClientSystemInfo().getServerTimeDelta());
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

            if ((authenticationToken == null) && HTML5Storage.isSupported()) {
                authenticationToken = HTML5Storage.getLocalStorage().getItem(TOKEN_STORAGE_ATTRIBUTE);
            }
            log.debug("authenticate {}", authenticationToken);
            select(authenticationService).authenticate(callback, clientSystemInfo, authenticationToken);
        }
    }

    public static void authenticate(AuthenticationRequest request, final AsyncCallback<Boolean> callback) {
        authenticate(null, request, callback);
    }

    public static void authenticate(AuthenticationService authenticationService, AuthenticationRequest request, final AsyncCallback<Boolean> callback) {
        AsyncCallback<AuthenticationResponse> rpcCallback = new DefaultAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.getClientSystemInfo().setServerTimeDelta(System.currentTimeMillis() - result.getServertTime());
                ClientApplicationBackendConfig.setProductionBackend(result.isProductionBackend());
                log.debug("Client/Server time delta {}", ClientContext.getClientSystemInfo().getServerTimeDelta());
                ClientContext.authenticated(result);
                callback.onSuccess(isAuthenticated());
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

        };
        select(authenticationService).authenticate(rpcCallback, ClientContext.getClientSystemInfo(), request);
    }
}
