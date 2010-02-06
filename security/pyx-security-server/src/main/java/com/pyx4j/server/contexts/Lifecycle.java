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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;

public class Lifecycle {

    private final static Logger log = LoggerFactory.getLogger(Lifecycle.class);

    public static void beginRequest(HttpServletRequest httprequest) {
        //long start = System.nanoTime();
        Context.setRequest(httprequest);
        beginRequest(httprequest.getSession(false));
        //log.debug("beginRequest, took {}ms", (int) (System.nanoTime() - start) / Consts.MSEC2NANO);
    }

    public static void beginRequest(HttpSession session) {
        Context.setSession(session);
        if (session != null) {
            Context.setVisit((Visit) session.getAttribute(Context.SESSION_VISIT));
        }
        //        PersistenceServicesFactory.getPersistenceService().startRequest();
    }

    public static void endRequest() {
        //        PersistenceServicesFactory.getPersistenceService().endRequest();
        Context.remove();
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
    public static void beginSession(UserVisit userVisit, Set<Behavior> behaviors) {
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
        log.info("Session {} starts for {}", newSession.getId(), userVisit.getName());
        for (Map.Entry<String, Object> me : keepAttributes.entrySet()) {
            newSession.setAttribute(me.getKey(), me.getValue());
        }
        beginSession(newSession);
        Context.getVisit().beginSession(userVisit, SecurityController.instance().authenticate(behaviors));
    }

    public static void beginSession(HttpSession session) {
        Context.setSession(session);
        Visit visit = new Visit();
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
            Context.remove();
            try {
                log.info("Session {} ends", session.getId());
                session.invalidate();
            } catch (IllegalStateException e) {
                // this method is called already
            }
        }
    }
}
