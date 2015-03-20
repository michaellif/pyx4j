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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.security.common.VistaApplication;

public class FixedDNSApplicationContextResolver extends AbstractApplicationContextResolver {

    private static Logger log = LoggerFactory.getLogger(FixedDNSApplicationContextResolver.class);

    private final VistaApplication application;

    private final String namespaceProposal;

    public FixedDNSApplicationContextResolver(String dnsName, VistaApplication application) {
        this(dnsName, application, application.getFixedNamespace());
    }

    public FixedDNSApplicationContextResolver(String dnsName, VistaApplication application, String namespaceProposal) {
        super(dnsName);
        if (namespaceProposal == null) {
            throw new IllegalArgumentException();
        }
        this.application = application;
        this.namespaceProposal = namespaceProposal;
        log.debug("Fixed DNS {} -> {} {} resolver created", dnsName, application, namespaceProposal);
    }

    @Override
    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, String normalizedHostName) {
        if (normalizedHostName.length() == 0) {
            return application;
        } else {
            return null;
        }
    }

    @Override
    protected String resolveNamespaceProposal(HttpServletRequest httpRequest, String normalizedHostName, VistaApplication application) {
        return namespaceProposal;
    }

}
