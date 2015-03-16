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
package com.propertyvista.server.config.filter.namespace.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.propertyvista.domain.security.common.VistaApplication;

public class NamesConfigProd implements NamesConfig {

    final static Map<String, VistaApplication> appByDomin = new HashMap<>();

    final static Set<String> prodSystemDnsBase = new HashSet<String>();

    final static Set<String> stagingDnsBase = new HashSet<String>();

    static {
        appByDomin.put("propertyvista.com", VistaApplication.crm);
        appByDomin.put("my-community.co", VistaApplication.resident);

        prodSystemDnsBase.add("my-community.co");
        prodSystemDnsBase.add("residentportalsite.com");
        prodSystemDnsBase.add("propertyvista.com");

        stagingDnsBase.add("-staging.propertyvista.net");
    }

    @Override
    public Collection<String> baseUrlsHostPmc() {
        return prodSystemDnsBase;
    }

    @Override
    public Collection<String> baseUrlsHostNameAppPart() {
        return stagingDnsBase;
    }

    @Override
    public VistaApplication getAppSugestionByDomainPart(String dnsBase) {
        return appByDomin.get(dnsBase);
    }

}
