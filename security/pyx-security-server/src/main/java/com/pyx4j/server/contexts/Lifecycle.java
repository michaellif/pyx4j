/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 11-Sep-06
 * @author vlads
 */
package com.pyx4j.server.contexts;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.server.LifecycleListener;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.i18n.server.I18nManager;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification.ChangeType;
import com.pyx4j.security.server.AclCreatorAllowAll;
import com.pyx4j.security.server.AclRevalidator;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;

public class Lifecycle {

    private final static Logger log = LoggerFactory.getLogger(Lifecycle.class);

    private static String END_SESSION_ATR = "pyx.endSession";

    private static String END_REQUEST_ATR = "pyx.endRpcRequest";

    public static void beginRequest(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
        //long start = System.nanoTime();
        ServerContext.beginRequest(httprequest, httpresponse);
        HttpSession session = httprequest.getSession(false);
        ServerContext.setSession(session);
        if (session != null) {
            String namespace = (String) session.getAttribute(ServerContext.NAMESPACE);
            Visit visit = (Visit) session.getAttribute(ServerContext.SESSION_VISIT);
            if ((visit != null) && (!NamespaceManager.getNamespace().equals(namespace))) {
                throw new RuntimeException("namespace access error");
            }
            ServerContext.setVisit(visit);

            if ((visit != null) && visit.isUserLoggedIn()) {
                LoggerConfig.mdcPut(LoggerConfig.MDC_userID, visit.getUserVisit().toString());
                revalidateAclIfRequired();
            }

        }

        //log.debug("beginRequest, took {}ms", (int) (System.nanoTime() - start) / Consts.MSEC2NANO);
        for (LifecycleListener lifecycleListener : ServerSideConfiguration.instance().getLifecycleListeners()) {
            lifecycleListener.onRequestBegin();
        }
    }

    public static void revalidateAclIfRequired() {
        Visit visit = ServerContext.getVisit();
        if ((visit != null) && visit.isUserLoggedIn()) {
            String clientAclTimeStamp = ServerContext.getRequestHeader(RemoteService.SESSION_ACL_TIMESTAMP_HEADER);
            if (visit.isAclRevalidationRequired(clientAclTimeStamp)) {
                AclRevalidator acv = ServerSideConfiguration.instance().getAclRevalidator();
                if (acv != null) {
                    Set<Behavior> behaviours = acv.getCurrentBehaviours(visit.getUserVisit().getPrincipalPrimaryKey(), visit.getAcl().getBehaviours(),
                            visit.getAclTimeStamp());
                    if (behaviours == null) {
                        endSession();
                        ServerContext.addResponseSystemNotification(new AuthorizationChangedSystemNotification(ChangeType.sessionTerminated));
                    } else {
                        Set<Behavior> assignedBehaviours = SecurityController.instance().getAllBehaviors(behaviours);
                        if (!EqualsHelper.equals(assignedBehaviours, visit.getAcl().getBehaviours())) {
                            log.info("AuthorizationChanged {} -> {}", visit.getAcl().getBehaviours(), assignedBehaviours);
                            acv.reAuthorizeCurrentVisit(behaviours);
                        } else {
                            if ((clientAclTimeStamp != null) && (visit.getAclTimeStamp() != Long.parseLong(clientAclTimeStamp))) {
                                log.info("AuthorizationChanged client needs sync {}", visit.getAcl().getBehaviours());
                                ServerContext.addResponseSystemNotification(new AuthorizationChangedSystemNotification(ChangeType.syncRequired));
                            }
                            visit.aclRevalidated();
                        }
                    }
                }
            }
        }
    }

    public static void endRequest() {
        try {
            if ((ServerContext.getRequest() != null) && ServerContext.getRequest().getAttribute(END_REQUEST_ATR) == null) {
                endRpcRequest();
            }
            HttpSession session = ServerContext.getSession();
            if (session != null) {
                Visit visit = ServerContext.getVisit();
                if ((visit != null) && (visit.isChanged())) {

                    // Cleanup transient fields since they are preserved in GAE development environment
                    visit.setAclChanged(false);
                    visit.unChanged();

                    // Force object update in GAE session.
                    session.setAttribute(ServerContext.SESSION_VISIT, visit);
                }
            }
            HttpServletRequest request = ServerContext.getRequest();
            if ((request != null) && (request.getAttribute(END_SESSION_ATR) != null)) {
                // Remove Session Cookie
                Cookie c = new Cookie(ServerSideConfiguration.instance().getSessionCookieName(), "");
                c.setPath("/");
                c.setMaxAge(0);
                ServerContext.getResponse().addCookie(c);
            }
        } finally {
            endContext();
        }
    }

    public static void endContext() {
        try {
            for (LifecycleListener lifecycleListener : ServerSideConfiguration.instance().getLifecycleListeners()) {
                lifecycleListener.onContextEnd();
            }
        } finally {
            removeContext();
        }
    }

    public static void removeContext() {
        ServerContext.remove();
        NamespaceManager.remove();
        I18nManager.removeThreadLocale();
        LoggerConfig.mdcClear();
    }

    public static void endRpcRequest() {
        try {
            if (ServerContext.getRequest().getAttribute(END_SESSION_ATR) != null) {
                // Remove Session Cookie
                Cookie c = new Cookie(ServerSideConfiguration.instance().getSessionCookieName(), "");
                c.setPath("/");
                c.setMaxAge(0);
                ServerContext.getResponse().addCookie(c);
            }

            for (LifecycleListener lifecycleListener : ServerSideConfiguration.instance().getLifecycleListeners()) {
                lifecycleListener.onRequestEnd();
            }
        } finally {
            ServerContext.getRequest().setAttribute(END_REQUEST_ATR, Boolean.TRUE);
        }
    }

    public static void beginAnonymousSession() {
        HttpSession session = ServerContext.getSession();
        if (session == null) {
            beginSession(ServerContext.getRequest().getSession(true));
            log.info("Anonymous Session {} starts", ServerContext.getSession().getId());
        }
        //Context.getVisit().beginAnonymousSession(JAASHelper.anonymousLogin());
    }

    public static void changeSessionAuthorization(Behavior... behaviour) {
        changeSessionAuthorization(Arrays.asList(behaviour));
    }

    public static void changeSessionAuthorization(Collection<Behavior> newBehaviours) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.addAll(newBehaviours);
        beginSession(ServerContext.getVisit().getUserVisit(), behaviors);
        ServerContext.addResponseSystemNotification(new AuthorizationChangedSystemNotification(ChangeType.behavioursChanged));
    }

    //TODO  Change the implementation to use Authorization functions
    public static String beginSession(UserVisit userVisit, Set<Behavior> behaviours) {
        Visit currentVisit = ServerContext.getVisit();
        if ((currentVisit != null) && (userVisit != null) && (currentVisit.isUserLoggedIn())
                && EqualsHelper.equals(userVisit.getPrincipalPrimaryKey(), currentVisit.getUserVisit().getPrincipalPrimaryKey())) {
            // The same user. No need to create new session, consider that behaviors are updated
        } else {
            HttpSession session = ServerContext.getSession();
            // Preserve some administration and debug session attributes
            Map<String, Object> keepAttributes = new HashMap<String, Object>();
            if (session != null) {
                for (Enumeration<String> en = session.getAttributeNames(); en.hasMoreElements();) {
                    String attrName = en.nextElement();
                    if (attrName.startsWith("com.pyx4j.keep.")) {
                        keepAttributes.put(attrName, session.getAttribute(attrName));
                    }
                }
                try {
                    log.info("Session {} ends", session.getId());
                    session.invalidate();
                } catch (IllegalStateException e) {
                }
            }
            HttpSession newSession = ServerContext.getRequest().getSession(true);
            log.info("Session {} starts for {}", newSession.getId(), userVisit);
            for (Map.Entry<String, Object> me : keepAttributes.entrySet()) {
                newSession.setAttribute(me.getKey(), me.getValue());
            }
            beginSession(newSession);

            if (userVisit != null) {
                //"Guessed User name" org.apache.catalina.manager.util.SessionUtils
                newSession.setAttribute("User", userVisit);
            }
        }
        ServerContext.getVisit().beginSession(userVisit, SecurityController.instance().authorize(behaviours));
        ServerContext.getRequest().removeAttribute(END_SESSION_ATR);
        return ServerContext.getVisit().getSessionToken();
    }

    public static String beginSession(HttpSession session) {
        ServerContext.setSession(session);
        LoggerConfig.mdcPut(LoggerConfig.MDC_sessionNum, session.getId());

        if (ServerSideConfiguration.instance().getOverrideSessionMaxInactiveInterval() != null) {
            session.setMaxInactiveInterval(ServerSideConfiguration.instance().getOverrideSessionMaxInactiveInterval());
        }

        String sessionToken;
        SecureRandom random = new SecureRandom();
        sessionToken = Long.toHexString(random.nextLong());
        log.info("Session {} X-XSRF token {}", session.getId(), sessionToken);

        Visit visit = new Visit(sessionToken);
        session.setAttribute(ServerContext.SESSION_VISIT, visit);
        ServerContext.setVisit(visit);
        session.setAttribute(ServerContext.NAMESPACE, NamespaceManager.getNamespace());

        return sessionToken;
    }

    public static void inheritUserContext(InheritableUserContext inheritableUserContext) {
        ServerContext.setVisit(inheritableUserContext.abstractVisit);
        ServerContext.setSession(inheritableUserContext.session);
        ServerContext.setDevSession(inheritableUserContext.devSession);
        NamespaceManager.setNamespace(inheritableUserContext.namespace);
        I18nManager.setThreadLocale(inheritableUserContext.locale);
        if (inheritableUserContext.sysDate != null) {
            SystemDateManager.setDate(inheritableUserContext.sysDate);
        }
    }

    public static void inheritDevSession(String devSessionId) {
        DevSession devSession = DevSession.getSession(devSessionId);
        if (devSession != null) {
            ServerContext.setDevSession(devSession);
        }
    }

    public static void startElevatedUserContext() {
        Visit visit = new Visit(null);
        visit.beginSession(null, new AclCreatorAllowAll().createAcl(null));
        visit.setAttribute("inheritableUserContext", ServerContext.getInheritableUserContext());
        ServerContext.setVisit(visit);
    }

    public static void endElevatedUserContext() {
        Visit visit = ServerContext.getVisit();
        inheritUserContext((InheritableUserContext) visit.getAttribute("inheritableUserContext"));
    }

    public static Visit getVisitFromSession(HttpSession session) {
        return (Visit) session.getAttribute(ServerContext.SESSION_VISIT);
    }

    public static String getNamespaceFromSession(HttpSession session) {
        return (String) session.getAttribute(ServerContext.NAMESPACE);
    }

    public static void endSession() {
        endSession(ServerContext.getSession());
    }

    public static void endSession(HttpSession session) {
        if (ServerContext.getVisit() != null) {
            ServerContext.getVisit().endSession();
        }
        if (session != null) {
            ServerContext.endSession();
            try {
                log.info("Session {} ends", session.getId());
                session.invalidate();
            } catch (IllegalStateException e) {
                // this method is called already
            }
            ServerContext.getRequest().setAttribute(END_SESSION_ATR, Boolean.TRUE);
        }
    }

}
