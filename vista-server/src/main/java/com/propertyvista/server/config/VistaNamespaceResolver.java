/*
 * (C) Copyright Pro;perty Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.shared.ApplicationMode;

public class VistaNamespaceResolver implements NamespaceResolver {

    public static final String demoNamespace = "vista";

    @Override
    public String getNamespace(HttpServletRequest httprequest) {
        // Dev: Get the 4th part of URL.
        // www.ABC.22.birchwoodsoftwaregroup.com
        // www.ABC.dev.birchwoodsoftwaregroup.com 

        // Prod: Get the 3rd part of URL.
        // www.ABC.propertyvista.com 

        String host = httprequest.getHeader("x-forwarded-host");
        if (host == null) {
            host = httprequest.getServerName();
        }
        String[] parts = host.split("\\.");
        if (ApplicationMode.isDevelopment()) {
            if (parts.length >= 4) {
                return parts[parts.length - 4];
            }
        } else {
            if (parts.length >= 3) {
                return parts[parts.length - 3];
            }
        }
        return demoNamespace;
    }
}
