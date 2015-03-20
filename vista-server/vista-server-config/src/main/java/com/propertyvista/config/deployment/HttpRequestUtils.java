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
package com.propertyvista.config.deployment;

import javax.servlet.http.HttpServletRequest;

import com.propertyvista.domain.security.common.VistaApplication;

class HttpRequestUtils {

    public static String getNamespaceDataCacheKey(HttpServletRequest httpRequest) {

        StringBuffer appCacheKey = new StringBuffer();
        appCacheKey.append(HttpRequestUtils.class.getName());
        appCacheKey.append("/");
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

}
