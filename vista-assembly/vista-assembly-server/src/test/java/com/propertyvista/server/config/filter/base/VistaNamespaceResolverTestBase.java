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
        createActivePMC("redridge");
        createActivePMC("demo");

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
        PMCTestCreator.createPMC(namespace, PmcStatus.Active).save();
    }

    private void createInactivePMC(String namespace) {
        PMCTestCreator.createPMC(namespace, PmcStatus.Activating).save();
    }

    private void createActivePMCWithActiveAlias(String namespace, String dnsAlias, DnsNameTarget targetApp) {
        PMCTestCreator.createPMC(namespace, PmcStatus.Active).addDNSAlias(dnsAlias, targetApp, true).save();
    }

    private void createInactivePMCWithActiveAlias(String namespace, String dnsAlias, DnsNameTarget targetApp) {
        PMCTestCreator.createPMC(namespace, PmcStatus.Activating).addDNSAlias(dnsAlias, targetApp, true).save();
    }

    private void createActivePMCWithInactiveAlias(String namespace, String dnsAlias, DnsNameTarget targetApp) {
        PMCTestCreator.createPMC(namespace, PmcStatus.Active).addDNSAlias(dnsAlias, targetApp, false).save();
    }

}
