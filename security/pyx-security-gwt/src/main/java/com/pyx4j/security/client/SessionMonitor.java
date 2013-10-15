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
 * Created on May 15, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RPCStatusChangeEvent;
import com.pyx4j.rpc.client.RPCStatusChangeHandler;
import com.pyx4j.webstorage.client.HTML5Storage;
import com.pyx4j.webstorage.client.StorageEvent;
import com.pyx4j.webstorage.client.StorageEventHandler;

public class SessionMonitor implements RPCStatusChangeHandler, StorageEventHandler {

    private static final Logger log = LoggerFactory.getLogger(SessionMonitor.class);

    private static SessionMonitor instance;

    private boolean monitoring = false;

    private long maxInactiveIntervalMillis;

    private String sessionCookieName;

    private String sessionCookieValue;

    private String sessionCookieValueHashCode;

    private Timer timer;

    private long sessionStart;

    private long lastActivity = 0;

    private boolean logChangeSessionCookieOnce = true;

    private static final String SESSION_ID_KEY = "pyx.session-key";

    public static void startMonitoring() {
        if (instance == null) {
            initialize();
        }
    }

    static void initialize() {
        if (instance != null) {
            return;
        }
        instance = new SessionMonitor();
        ClientSecurityController.addContextInitializeHandler(new ContextInitializeHandler() {

            @Override
            public void onContextInitialize(ContextInitializeEvent event) {
                instance.update();

            }
        });
        ClientSecurityController.addSecurityControllerHandler(new BehaviorChangeHandler() {

            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                instance.onAuthenticationChange();

            }
        });
    }

    public static HandlerRegistration addSessionInactiveHandler(SessionInactiveHandler handler) {
        startMonitoring();
        return ClientEventBus.addHandler(SessionInactiveEvent.TYPE, handler);

    }

    public static long getSessionStartTime() {
        if (instance != null) {
            return instance.sessionStart;
        } else {
            return 0;
        }
    }

    public static long getSessionInactiveTime() {
        if (instance != null) {
            return instance.lastActivity;
        } else {
            return 0;
        }
    }

    protected SessionMonitor() {
        RPCManager.addRPCStatusChangeHandler(this);
        if (HTML5Storage.isSupported()) {
            HTML5Storage.addStorageEventHandler(this);
        }
    }

    private void onAuthenticationChange() {
        if (ClientContext.hasServerSession()) {
            logChangeSessionCookieOnce = true;
            if (!monitoring) {
                start();
            } else {
                update();
            }
        } else {
            update();
            if (monitoring) {
                stop();
            }
        }
    }

    @Override
    public void onRPCStatusChange(RPCStatusChangeEvent event) {
        switch (event.getWhen()) {
        case SUCCESS:
            lastActivity = System.currentTimeMillis();
            break;
        case START:
            checkSessionCookie();
            break;
        }
    }

    private void start() {
        maxInactiveIntervalMillis = Consts.SEC2MSEC * (ClientContext.getServerSession().getMaxInactiveInterval() - 2 * Consts.MIN2SEC);
        if (maxInactiveIntervalMillis <= Consts.MIN2MSEC) {
            // Development testing
            maxInactiveIntervalMillis = Consts.MIN2MSEC;
        }
        update();
        sessionStart = System.currentTimeMillis();
        lastActivity = sessionStart;
        logChangeSessionCookieOnce = true;
        timer = new Timer() {
            @Override
            public void run() {
                checkActivity();
            }
        };
        timer.scheduleRepeating((int) (3 * Consts.MIN2MSEC));
        monitoring = true;
    }

    private void update() {
        if (ClientContext.hasServerSession()) {
            sessionCookieName = ClientContext.getServerSession().getSessionCookieName();
        } else {
            sessionCookieName = null;
        }
        if (sessionCookieName == null) {
            sessionCookieValue = null;
        } else {
            sessionCookieValue = Cookies.getCookie(sessionCookieName);
        }
        if (sessionCookieValue == null) {
            sessionCookieValueHashCode = String.valueOf(ClientContext.visitHashCode());
        } else {
            sessionCookieValueHashCode = String.valueOf(ClientContext.visitHashCode()) + "*" + String.valueOf(sessionCookieValue.hashCode());
        }

        if (HTML5Storage.isSupported()) {
            log.debug("set session code {}", sessionCookieValueHashCode);
            HTML5Storage.getLocalStorage().setItem(SESSION_ID_KEY, sessionCookieValueHashCode);
        }
    }

    private void stop() {
        monitoring = false;
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        log.debug("session monitoring stopped");
    }

    protected void onSessionInactive(boolean timeout) {
        ClientEventBus.fireEvent(new SessionInactiveEvent(timeout));
    }

    private void checkActivity() {
        if ((maxInactiveIntervalMillis > 0) && (System.currentTimeMillis() > (lastActivity + maxInactiveIntervalMillis))) {
            log.debug("Session Inactive; lastActivity {} now {}", lastActivity, System.currentTimeMillis());
            onSessionInactive(true);
        } else {
            checkSessionCookie();
        }
    }

    private void checkSessionCookie() {
        // Look for Cookie changed externally, e.g. other tab of windows of the same browser
        if ((monitoring) && (this.sessionCookieValue != null)) {
            String currentSessionCookie = Cookies.getCookie(sessionCookieName);
            if (!this.sessionCookieValue.equals(currentSessionCookie)) {
                if (logChangeSessionCookieOnce) {
                    log.debug("Session COOKIE CHANGED {}->{} ", sessionCookieValue, currentSessionCookie);
                    //logChangeSessionCookieOnce = false;
                }
                // Avoid infinite loop
                sessionCookieValue = currentSessionCookie;
                onSessionInactive(false);
            }
        }
    }

    @Override
    public void onStorageChange(StorageEvent event) {
        if (ClientContext.authenticationChanging) {
            // Avoid infinite loop if Event handlers are changing Storage 
            return;
        }
        String newHashCode = HTML5Storage.getLocalStorage().getItem(SESSION_ID_KEY);
        if (!CommonsStringUtils.equals(sessionCookieValueHashCode, newHashCode)) {
            log.debug("SessionHash change {} -> {}", sessionCookieValueHashCode, newHashCode);
            ClientContext.obtainAuthenticationData(null, null, true, null);
        }

    }
}
