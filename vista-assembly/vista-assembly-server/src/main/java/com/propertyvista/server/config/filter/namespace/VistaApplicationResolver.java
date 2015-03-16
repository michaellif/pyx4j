/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2015
 * @author vlads
 */
package com.propertyvista.server.config.filter.namespace;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.filter.namespace.config.NamesConfig;

public class VistaApplicationResolver {

    public VistaApplication getAppByDomainOrPath(NamesConfig namesConfig, String domain, String rootServletPath) {

        // vista-crm.local.devpv.com          4 parts
        // static.propertyvista.com           3 part

        // vista-crm-11.devpv.com             3 part
        // static-staging.propertyvista.net   3 parts

        String[] serverNameParts = domain.split("\\.");

        if (serverNameParts.length >= 3) {
            String dnsBase = serverNameParts[serverNameParts.length - 2] + "." + serverNameParts[serverNameParts.length - 1];
            String dnsApp = serverNameParts[serverNameParts.length - 3];

            namesConfig.getAppSugestionByDomainPart(dnsBase);
        }

//        name =  
//        if (namesConfig.baseUrlsHostPmc().constaing(name) {
//            / resolution 1
//        } else  if (namesConfig.baseUrlsHostNameAppPart().constaing(name) {
//            / resolution 2
//        }
        return null;
    }
}
