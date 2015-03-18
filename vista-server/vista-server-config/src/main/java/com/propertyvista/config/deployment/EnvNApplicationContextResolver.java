/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2015
 * @author vlads
 */
package com.propertyvista.config.deployment;

import javax.servlet.http.HttpServletRequest;

import com.propertyvista.domain.security.common.VistaApplication;

/**
 * start-11.devpv.com -> onboarding
 *
 * vista-crm.local.devpv.com -> crm
 * vista-crm-11.devpv.com -> crm
 * vista-crm-staging.propertyvista.net -> crm
 *
 */
public class EnvNApplicationContextResolver extends StandardApplicationContextResolver {

    public EnvNApplicationContextResolver(String dnsNameBase) {

    }

    @Override
    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, String[] serverNameParts) {
        String appByDomain = serverNameParts[0];
        String[] appByDomainTokens = appByDomain.split("-");
        if (appByDomainTokens.length >= 2) {
            return VistaApplication.getVistaApplicationByDnsNameFragment(appByDomainTokens[1]);
        }
        return null;
    }

    @Override
    protected String resolveNamespaceProposal(HttpServletRequest httpRequest, String[] serverNameParts) {
        // TODO Auto-generated method stub
        return null;
    }
}
