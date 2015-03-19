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

public class SingleAppApplicationContextResolver extends AbstractApplicationContextResolver {

    private final VistaApplication application;

    public SingleAppApplicationContextResolver(String dnsName, VistaApplication application) {
        super(dnsName);
        this.application = application;
    }

    @Override
    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, String normalizedHostName) {
        return application;
    }

    @Override
    protected String resolveNamespaceProposal(HttpServletRequest httpRequest, String normalizedHostName, VistaApplication application) {
        return normalizedHostName;
    }

}
