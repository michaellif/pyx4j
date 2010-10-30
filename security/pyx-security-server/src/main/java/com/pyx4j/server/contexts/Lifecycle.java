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
 * @version $Id$
 */
package com.pyx4j.server.contexts;

import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.security.shared.AclRevalidator;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;

public class Lifecycle {

    private final static Logger log = LoggerFactory.getLogger(Lifecycle.class);

    public static void beginRequest(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
        //long start = System.nanoTime();
        Context.beginRequest(httprequest, httpresponse);
        HttpSession session = httprequest.getSession(false);
        Context.setSession(session);
        if (session != null) {
            Visit visit = (Visit) session.getAttribute(Context.SESSION_VISIT);
            Context.setVisit(visit);
            String clientAclTimeStamp = httprequest.getHeader(RemoteService.SESSION_ACL_TIMESTAMP_HEADER);
            if ((visit != null) && visit.isUserLoggedIn() && visit.isAclRevalidationRequired(clientAclTimeStamp)) {
                AclRevalidator acv = ServerSideConfiguration.instance().getAclRevalidator();
                if (acv != null) {
                    Set<Behavior> behaviours = acv.getCurrentBehaviours(visit.getUserVisit().getPrincipalPrimaryKey(), visit.getAcl().getBehaviours(),
                            visit.getAclTimeStamp());
                    if (behaviours == null) {
                        endSession(session);
                        Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification(true));
                    } else if (behaviours != visit.getAcl().getBehaviours()) {
                        log.info("AuthorizationChanged {} -> {}", visit.getAcl().getBehaviours(), behaviours);
                        visit.beginSession(visit.getUserVisit(), SecurityController.instance().authenticate(behaviours));
                        visit.setAclChanged(true);
                        Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification());
                    } else {
                        if ((clientAclTimeStamp != null) && (visit.getAclTimeStamp() != Long.parseLong(clientAclTimeStamp))) {
                            log.info("AuthorizationChanged client needs sync {}", visit.getAcl().getBehaviours());
                            Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification());
                        }
                        visit.aclRevalidated();
                    }
                }
            }
        }

        //log.debug("beginRequest, took {}ms", (int) (System.nanoTime() - start) / Consts.MSEC2NANO);
        // PersistenceServicesFactory.getPersistenceService().startRequest();
    }

    public static void endRequest() {
        try {
            //        PersistenceServicesFactory.getPersistenceService().endRequest();
            HttpSession session = Context.getSession();
            if (session != null) {
                Visit visit = Context.getVisit();
                if ((visit != null) && (visit.isChanged())) {

                    // Cleanup transient fields since they are preserved in GAE development environment
                    visit.setAclChanged(false);
                    visit.unChanged();

                    // Force object update in GAE session.
                    session.setAttribute(Context.SESSION_VISIT, visit);
                }
            }
        } finally {
            Context.remove();
        }
    }

    public static void beginAnonymousSession() {
        HttpSession session = Context.getSession();
        if (session == null) {
            beginSession(Context.getRequest().getSession(true));
            log.info("Anonymous Session {} starts", Context.getSession().getId());
        }
        //Context.getVisit().beginAnonymousSession(JAASHelper.anonymousLogin());
    }

    @SuppressWarnings("unchecked")
    public static void beginSession(UserVisit userVisit, Set<Behavior> behaviours) {
        Visit currentVisit = Context.getVisit();
        if ((currentVisit != null) && (currentVisit.isUserLoggedIn())
                && EqualsHelper.equals(userVisit.getPrincipalPrimaryKey(), currentVisit.getUserVisit().getPrincipalPrimaryKey())) {
            // The same user. No need to create new session, consider that behaviours are updated
        } else {
            HttpSession session = Context.getSession();
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
            HttpSession newSession = Context.getRequest().getSession(true);
            log.info("Session {} starts for {}", newSession.getId(), userVisit);
            for (Map.Entry<String, Object> me : keepAttributes.entrySet()) {
                newSession.setAttribute(me.getKey(), me.getValue());
            }
            beginSession(newSession);
        }
        Context.getVisit().beginSession(userVisit, SecurityController.instance().authenticate(behaviours));
    }

    public static void beginSession(HttpSession session) {
        Context.setSession(session);

        String sessionToken;
        SecureRandom random = new SecureRandom();
        sessionToken = Long.toHexString(random.nextLong());
        log.info("Session {} X-XSRF token {}", session.getId(), sessionToken);

        Visit visit = new Visit(sessionToken);
        session.setAttribute(Context.SESSION_VISIT, visit);
        Context.setVisit(visit);
    }

    public static void endSession() {
        endSession(Context.getSession());
    }

    public static void endSession(HttpSession session) {
        if (Context.getVisit() != null) {
            Context.getVisit().endSession();
        }
        if (session != null) {
            Context.endSession();
            try {
                log.info("Session {} ends", session.getId());
                session.invalidate();
            } catch (IllegalStateException e) {
                // this method is called already
            }
            // Remove Session Cookie 
            Cookie c = new Cookie(ServerSideConfiguration.instance().getSessionCookieName(), "");
            c.setPath("/");
            c.setMaxAge(0);
            Context.getResponse().addCookie(c);
        }
    }
}
