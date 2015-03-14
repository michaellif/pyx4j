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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class SpecialURLMaping {

    final static Map<String, SpecialURL> prodUrls = new HashMap<>();

    // TODO Make it environment dependent in VistaServerSideConfiguration
    // List to be initialized once from configuration...
    static {
        prodUrls.put("static.propertyvista.com", SpecialURL.staticContext);
        prodUrls.put("static.propertyvista.net", SpecialURL.staticContext);

        prodUrls.put("env-prod.propertyvista.com", SpecialURL.envLinks);
        prodUrls.put("env-staging.propertyvista.net", SpecialURL.envLinks);
    }

    public static SpecialURL resolve(HttpServletRequest httpRequest) {
        String serverName = httpRequest.getServerName();
        return prodUrls.get(serverName);
    }
}
