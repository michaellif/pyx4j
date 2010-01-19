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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class Context {

    static final String SESSION_VISIT = "visit";

    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();

    private static final ThreadLocal<Visit> abstractVisit = new ThreadLocal<Visit>();

    private static final ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();

    private static final ThreadLocal<Boolean> security = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.TRUE;
        }
    };

    public static Visit getVisit() {
        return abstractVisit.get();
    }

    static void setVisit(Visit v) {
        abstractVisit.set(v);
    }

    /**
     * TODO remove public.
     * 
     * @return
     */
    public static HttpSession getSession() {
        return session.get();
    }

    static void setSession(HttpSession s) {
        session.set(s);
    }

    public static String getSessionId() {
        HttpSession s = getSession();
        if (s == null) {
            return null;
        } else {
            return s.getId();
        }
    }

    static HttpServletRequest getRequest() {
        return request.get();
    }

    static void setRequest(HttpServletRequest r) {
        request.set(r);
    }

    public static String getRequestHeader(String name) {
        return getRequest().getHeader(name);
    }

    public static String getRequestServerName() {
        return getRequest().getServerName();
    }

    public static String getRequestScheme() {
        return getRequest().getScheme();
    }

    public static boolean isSecurityEnabled() {
        return security.get();
    }

    public static void setSecurityEnabled(boolean v) {
        security.set(v);
    }

    static void remove() {
        request.remove();
        abstractVisit.remove();
        session.remove();
    }
}
