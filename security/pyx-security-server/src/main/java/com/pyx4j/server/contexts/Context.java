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

import java.io.Serializable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class Context {

    static final String SESSION_VISIT = "visit";

    private static class RequestContext {

        HttpServletRequest request;

        HttpServletResponse response;

        HttpSession session;

        Visit abstractVisit;

        boolean sessionEnd;

        Vector<Serializable> systemNotifications;

    }

    private static final ThreadLocal<RequestContext> requestLocal = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            return new RequestContext();
        }
    };

    public static Visit getVisit() {
        return requestLocal.get().abstractVisit;
    }

    public static boolean isUserLoggedIn() {
        Visit v = Context.getVisit();
        return (v != null) && (v.isUserLoggedIn());
    }

    static void setVisit(Visit v) {
        requestLocal.get().abstractVisit = v;
    }

    /**
     * TODO remove public.
     * 
     * @return
     */
    public static HttpSession getSession() {
        return requestLocal.get().session;
    }

    static void setSession(HttpSession s) {
        requestLocal.get().session = s;
        if (s != null) {
            requestLocal.get().sessionEnd = false;
        }
    }

    public static boolean isSessionEnd() {
        return requestLocal.get().sessionEnd;
    }

    public static String getSessionId() {
        HttpSession s = getSession();
        if (s == null) {
            return null;
        } else {
            return s.getId();
        }
    }

    public static Object getSessionAttribute(String name) {
        HttpSession s = getSession();
        if (s == null) {
            return null;
        } else {
            return s.getAttribute(name);
        }
    }

    static void beginRequest(HttpServletRequest request, HttpServletResponse response) {
        requestLocal.get().request = request;
        requestLocal.get().response = response;
    }

    public static HttpServletRequest getRequest() {
        return requestLocal.get().request;
    }

    public static HttpServletResponse getResponse() {
        return requestLocal.get().response;
    }

    public static void addResponseSystemNotification(Serializable systemNotification) {
        RequestContext ctx = requestLocal.get();
        if (ctx.systemNotifications == null) {
            ctx.systemNotifications = new Vector<Serializable>();
        }
        ctx.systemNotifications.add(systemNotification);
    }

    public static Vector<Serializable> getResponseSystemNotifications() {
        return requestLocal.get().systemNotifications;
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

    public static String getRequestRemoteAddr() {
        return getRequest().getRemoteAddr();
    }

    static void remove() {
        requestLocal.remove();
    }

    static void endSession() {
        RequestContext rc = requestLocal.get();
        rc.session = null;
        rc.abstractVisit = null;
        rc.sessionEnd = true;
    }
}
