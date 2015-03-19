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
public class EnvNApplicationContextResolver extends AbstractApplicationContextResolver {

    public EnvNApplicationContextResolver(String dnsNameBase) {
        super(dnsNameBase);
    }

    @Override
    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, String normalizedServerName) {
        String[] appByDomainTokens = normalizedServerName.split("-");
        if (appByDomainTokens.length >= 2) {
            VistaApplication app = VistaApplication.getVistaApplicationByDnsNameFragment(appByDomainTokens[1]);
            if (app != null && app.requirePmcResolution()) {
                if (app == VistaApplication.resident && "prospect".equalsIgnoreCase(HttpRequestUtils.getRootServletPath(httpRequest))) {
                    return VistaApplication.prospect;
                }
                return app;
            }
        } else if (appByDomainTokens.length == 1) {
            VistaApplication app = VistaApplication.getVistaApplicationByDnsNameFragment(appByDomainTokens[0]);
            if (app != null && !app.requirePmcResolution()) {
                return app;
            }
        }
        return null;
    }

    @Override
    protected String resolveNamespaceProposal(HttpServletRequest httpRequest, String normalizedServerName, VistaApplication application) {
        if (application.getFixedNamespace() != null) {
            return application.getFixedNamespace();
        } else {
            String[] appByDomainTokens = normalizedServerName.split("-");
            if (appByDomainTokens.length >= 2) {
                return appByDomainTokens[0];
            } else {
                return null;
            }
        }
    }
}
