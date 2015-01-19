/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.base;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import com.pyx4j.unit.server.mock.MockHttpServletRequest;

import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName.DnsNameTarget;
import com.propertyvista.server.config.filter.VistaTestDBSetupForNamespace;
import com.propertyvista.server.config.filter.util.PMCTestCreator;

public class VistaNamespaceResolverTestBase extends TestCase {

    protected MockHttpServletRequest req;

    @Override
    @Before
    public void setUp() throws Exception {
        // Init HSQL DB
        VistaTestDBSetupForNamespace.init();

        // Create testing PMCs
        createActivePMC("vista");
        createActivePMC("testnamespace");
        createActivePMC("testpmcs");
        createInactivePMC("inactivepmc");

        // Create different PMC with custom DNS aliases for applications
        createActivePMCWithActiveAlias("customizablePmc1", "custom.crm.server.canada.com", DnsNameTarget.crm);
        createActivePMCWithActiveAlias("customizablePmc2", "portalito.canada.com", DnsNameTarget.portal);
        createActivePMCWithActiveAlias("customizablePmc3", "mysite-bestseller.canada.com", DnsNameTarget.site);
        createInactivePMCWithActiveAlias("customizablePmc4", "customizableportal.server.canada.com", DnsNameTarget.portal);
        createActivePMCWithInactiveAlias("customizablePmc5", "customer.site.client-custom.canada.com", DnsNameTarget.site);

    }

    @Override
    @After
    public void tearDown() throws Exception {
        // Reset DB to normal
        VistaTestDBSetupForNamespace.resetDatabase();
    }

    private void createActivePMC(String namespace) {
        new PMCTestCreator.PMCTestCreatorBuilder(namespace, PmcStatus.Active).build();
    }

    private void createInactivePMC(String namespace) {
        new PMCTestCreator.PMCTestCreatorBuilder(namespace, PmcStatus.Activating).build();
    }

    private void createActivePMCWithActiveAlias(String namespace, String dnsAlias, DnsNameTarget targetApp) {
        new PMCTestCreator.PMCTestCreatorBuilder(namespace, PmcStatus.Active).addDNSAlias(dnsAlias, targetApp, true).build();
    }

    private void createInactivePMCWithActiveAlias(String namespace, String dnsAlias, DnsNameTarget targetApp) {
        new PMCTestCreator.PMCTestCreatorBuilder(namespace, PmcStatus.Activating).addDNSAlias(dnsAlias, targetApp, true).build();
    }

    private void createActivePMCWithInactiveAlias(String namespace, String dnsAlias, DnsNameTarget targetApp) {
        new PMCTestCreator.PMCTestCreatorBuilder(namespace, PmcStatus.Active).addDNSAlias(dnsAlias, DnsNameTarget.crm, false).build();
    }

}
