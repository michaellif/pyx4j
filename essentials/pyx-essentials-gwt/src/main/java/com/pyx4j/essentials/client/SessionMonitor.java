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
package com.pyx4j.essentials.client;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RPCStatusChangeEvent;
import com.pyx4j.rpc.client.RPCStatusChangeHandler;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.webstorage.client.HTML5Storage;
import com.pyx4j.webstorage.client.StorageEvent;
import com.pyx4j.webstorage.client.StorageEventHandler;

public class SessionMonitor implements RPCStatusChangeHandler, StorageEventHandler, SystemNotificationHandler {

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
            instance = new SessionMonitor();
            ClientSecurityController.instance().addValueChangeHandler(new ValueChangeHandler<Set<Behavior>>() {
                @Override
                public void onValueChange(ValueChangeEvent<Set<Behavior>> event) {
                    instance.onAuthenticationChange();
                }
            });
        }
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
        RPCManager.addSystemNotificationHandler(this);
        if (HTML5Storage.isSupported()) {
            HTML5Storage.getLocalStorage().addStorageEventHandler(this);
        }
    }

    private void onAuthenticationChange() {
        if (ClientContext.hasServerSession()) {
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
        if (HTML5Storage.isSupported()) {
            HTML5Storage.getLocalStorage().setItem(SESSION_ID_KEY, sessionCookieValueHashCode);
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
        maxInactiveIntervalMillis = Consts.SEC2MSEC * ClientContext.getServerSession().getMaxInactiveInterval();
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
            sessionCookieValueHashCode = String.valueOf(ClientContext.visitHashCode()) + String.valueOf(sessionCookieValue.hashCode());
        }
    }

    private void stop() {
        monitoring = false;
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        log.debug("session monitoring stoped ");
    }

    protected void onSessionInactive(boolean timeout) {
        SessionInactiveDialog.showSessionInactive(timeout);
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
        if ((monitoring) && (this.sessionCookieValue != null)) {
            String currentSessionCookie = Cookies.getCookie(sessionCookieName);
            if (!this.sessionCookieValue.equals(currentSessionCookie)) {
                if (logChangeSessionCookieOnce) {
                    log.debug("Session COOKIE CHANGED {}->{} ", sessionCookieValue, currentSessionCookie);
                    logChangeSessionCookieOnce = false;
                }
                onSessionInactive(false);
            }
        }
    }

    @Override
    public void onStorageChange(StorageEvent event) {
        String newHashCode = HTML5Storage.getLocalStorage().getItem(SESSION_ID_KEY);
        if (!CommonsStringUtils.equals(sessionCookieValueHashCode, newHashCode)) {
            log.debug("Session change {} -> {}", newHashCode, sessionCookieValueHashCode);
            ClientContext.obtainAuthenticationData(null, true, false);
        }

    }

    @Override
    public void onSystemNotificationReceived(SystemNotificationEvent event) {
        if (event.getSystemNotification() instanceof AuthorizationChangedSystemNotification) {
            if (((AuthorizationChangedSystemNotification) event.getSystemNotification()).isSessionTerminated()) {
                log.debug("Session terminated");
                ClientContext.terminateSession();
            } else {
                log.debug("Authorization Changed");
                ClientContext.obtainAuthenticationData(null, true, false);
            }
        }
    }

}
