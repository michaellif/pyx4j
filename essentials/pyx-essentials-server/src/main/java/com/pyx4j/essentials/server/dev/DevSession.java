/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-06-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.google.gwt.user.server.Util;

import com.pyx4j.commons.Consts;
import com.pyx4j.server.contexts.Context;

/**
 * This is session using for development of applications, it is detached from container session.
 */
public class DevSession {

    public static String DEV_SESSION_COOKIE_NAME = "pyx_dev_access";

    public static String DEV_SESSION_REQUEST_ATTRIBUTE = "com.pyx.pyx_dev_access";

    private static Map<String, DevSession> sessions = new Hashtable<String, DevSession>();

    private static int sessionDuration = Consts.DAY2HOURS * Consts.HOURS2SEC;

    private String id;

    private long eol;

    protected Map<String, Object> attributes = new Hashtable<String, Object>();

    private DevSession() {

    }

    public static DevSession getSession() {
        DevSession session = (DevSession) Context.getRequest().getAttribute(DEV_SESSION_REQUEST_ATTRIBUTE);
        if (session == null) {
            Cookie sessionCookie = Util.getCookie(Context.getRequest(), DEV_SESSION_COOKIE_NAME, true);
            if (sessionCookie != null) {
                session = sessions.get(sessionCookie.getValue());
                if ((session != null && session.eol <= System.currentTimeMillis())) {
                    sessions.remove(session.id);
                    session = null;
                }
            }
            if (session == null) {
                session = new DevSession();
            }
            Context.getRequest().setAttribute(DEV_SESSION_REQUEST_ATTRIBUTE, session);
        }

        return session;
    }

    public static DevSession beginSession() {
        DevSession session = new DevSession();

        SecureRandom random = new SecureRandom();
        session.id = Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());

        Cookie sessionCookie = new Cookie(DEV_SESSION_COOKIE_NAME, session.id);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(sessionDuration);
        Context.getResponse().addCookie(sessionCookie);
        session.eol = System.currentTimeMillis() + sessionDuration * Consts.SEC2MILLISECONDS;

        sessions.put(session.id, session);
        Context.getRequest().setAttribute(DEV_SESSION_REQUEST_ATTRIBUTE, session);

        return session;
    }

    public static void endSession() {
        DevSession session = getSession();
        if (session != null) {
            sessions.remove(session.id);

            // Remove Session Cookie 
            Cookie sessionCookie = new Cookie(DEV_SESSION_COOKIE_NAME, "");
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(0);
            Context.getResponse().addCookie(sessionCookie);
        }
    }

    public String getId() {
        return id;
    }

    public boolean isAlive() {
        return id != null;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }
}
