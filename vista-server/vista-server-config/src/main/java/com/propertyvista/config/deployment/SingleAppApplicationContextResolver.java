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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.security.common.VistaApplication;

public class SingleAppApplicationContextResolver extends AbstractApplicationContextResolver {

    private static Logger log = LoggerFactory.getLogger(SingleAppApplicationContextResolver.class);

    private final VistaApplication application;

    private final boolean hasSubApplications;

    public SingleAppApplicationContextResolver(String dnsName, VistaApplication application) {
        super(dnsName);
        this.application = application;
        List<VistaApplication> applications = VistaApplicationDeploymentMap.getSubApplications(application);
        if (applications.size() == 1) {
            hasSubApplications = false;
            log.debug("Single application {} -> {} resolver created", dnsNameBase, application);
        } else {
            hasSubApplications = true;
            log.debug("Single application {} -> {} resolver created", dnsNameBase, applications);
        }
    }

    @Override
    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, String normalizedHostName) {
        if (hasSubApplications) {
            return VistaApplicationDeploymentMap.getVistaSubApplication(application, httpRequest);
        } else {
            return application;
        }
    }

    @Override
    protected String resolveNamespaceProposal(HttpServletRequest httpRequest, String normalizedHostName, VistaApplication application) {
        return normalizedHostName;
    }

}
