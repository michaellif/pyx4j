/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2015
 * @author vlads
 */
package com.propertyvista.server.config.filter.special;

import javax.servlet.http.HttpServletRequest;

public enum SpecialURL {

    envLinks(Hidden.url + "/envLinks"),

    staticContext("/static");

    public final String url;

    private SpecialURL(String url) {
        this.url = url;
    }

    public String getNewURLRequest(HttpServletRequest httprequest) {
        String newUri = url;
        newUri += httprequest.getServletPath();
        if (httprequest.getPathInfo() != null) {
            newUri += httprequest.getPathInfo();
        }
        return newUri;
    }

}
