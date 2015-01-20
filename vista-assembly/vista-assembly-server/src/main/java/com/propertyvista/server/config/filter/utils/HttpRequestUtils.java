/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.utils;

import javax.servlet.http.HttpServletRequest;

import com.propertyvista.domain.security.common.VistaApplication;

public class HttpRequestUtils {

    public static String getAppCacheKey(HttpServletRequest httpRequest) {

        StringBuffer appCacheKey = new StringBuffer();
        appCacheKey.append(httpRequest.getServerName());

        String rootServletPath = getRootServletPath(httpRequest);
        if (rootServletPath.equals(VistaApplication.prospect.name())) {
            appCacheKey.append("/");
            appCacheKey.append(rootServletPath);
        }

        return appCacheKey.toString();
    }

    /**
     * Get first string in request servlet path
     *
     * @return first String in request servlet path or empty string in case servlet path is empty
     */
    public static String getRootServletPath(HttpServletRequest httpRequest) {
        String[] appByPathTokens = httpRequest.getServletPath().split("/");

        if (appByPathTokens.length >= 2) {
            return appByPathTokens[1];
        }

        return "";
    }

    public static String getServerURL(HttpServletRequest httpRequest) {
        return httpRequest.getScheme()
                + "://"
                + httpRequest.getServerName()
                + ("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort() == 80 || "https".equals(httpRequest.getScheme())
                        && httpRequest.getServerPort() == 443 ? "" : ":" + httpRequest.getServerPort());
    }

    public static String getCompleteURLNoContextPath(HttpServletRequest httpRequest) {
        return getCompleteURL(false, httpRequest);
    }

    public static String getCompleteURLWithContextPath(HttpServletRequest httpRequest) {
        return getCompleteURL(true, httpRequest);
    }

    public static String getCompleteURL(boolean returnWithContextPath, HttpServletRequest httpRequest) {
        String requestUri = httpRequest.getRequestURI();
        if (!returnWithContextPath && requestUri != null) {
            String contextPath = httpRequest.getContextPath();
            if (contextPath != null) {
                requestUri = requestUri.replaceAll(contextPath, ""); // hack for duplicate context on request
//                log.info("updatedRequestUri -> " + requestUri);
            }
        }

        return getServerURL(httpRequest) + requestUri + (httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : "");
    }

    public static String getHttpsUrl(HttpServletRequest httpRequest) {
        return new StringBuffer(getCompleteURLNoContextPath(httpRequest)).replace(0, 4, "https").toString();
    }
}
