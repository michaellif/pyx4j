/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.Calendar;
import java.util.Date;

import com.propertvista.generator.LeaseGenerator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.portal.server.preloader.util.AptUnitSource;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.portal.server.preloader.util.LeaseLifecycleSim;
import com.propertyvista.portal.server.preloader.util.LeaseLifecycleSim.LeaseLifecycleSimBuilder;

public class LeasePreloader extends BaseVistaDevDataPreloader {

    private static final int MAX_NUM_OF_LEASES_WITH_SIM_BILLING = 3;

    @Override
    public String create() {
        int numCreated = 0;
        int numCreatedWithBilling = 0;

        LeaseGenerator generator = new LeaseGenerator(config());

        // Start creating leases from 4 years ago
        Calendar fourYearsAgo = Calendar.getInstance();
        fourYearsAgo.setTime(new Date());
        fourYearsAgo.add(Calendar.YEAR, -4);

        AptUnitSource aptUnitSource = new AptUnitSource(1);

        Customer dualTenantCustomer = null;
        for (int i = 0; i < config().numTenants; i++) {

            AptUnit unit = aptUnitSource.next();
            Lease lease = generator.createLease(unit);
            LeaseGenerator.attachDocumentData(lease);
            LeaseGenerator.assigneLeaseProducts(lease);

            if (i < DemoData.UserType.TENANT.getDefaultMax()) {
                Tenant mainTenant = lease.version().tenants().get(0);
                String email = DemoData.UserType.TENANT.getEmail(i + 1);
                mainTenant.customer().person().email().setValue(email);
                // Make one (Third) Customer with Two Leases
                if (i == 2) {
                    dualTenantCustomer = mainTenant.customer();
                }
            } else if (i == DemoData.UserType.TENANT.getDefaultMax()) {
                Tenant mainTenant = lease.version().tenants().get(0);
                mainTenant.customer().set(dualTenantCustomer);
                mainTenant.screening().set(mainTenant.customer()._PersonScreenings().iterator().next());
            }

            // Create normal Active Lease first for Shortcut users
            if (i <= DemoData.UserType.TENANT.getDefaultMax()) {
                ServerSideFactory.create(LeaseFacade.class).initLease(lease);
                ServerSideFactory.create(LeaseFacade.class).approveApplication(lease, null, null);
                //TODO
                // ServerSideFactory.create(LeaseFacade.class).activate(lease.getPrimaryKey());
            } else {
                LeaseLifecycleSimBuilder simBuilder = LeaseLifecycleSim.sim().start(new LogicalDate(fourYearsAgo.getTime()));
                if (numCreatedWithBilling < MAX_NUM_OF_LEASES_WITH_SIM_BILLING) {
                    ++numCreatedWithBilling;
                    simBuilder.runBilling();
                }
                simBuilder.create().generateRandomLeaseLifeCycle(lease);
            }

            numCreated++;
        }

        Customer dualPotentialCustomer = null;
        for (int i = 0; i < config().numPotentialTenants; i++) {

            AptUnit unit = aptUnitSource.next();
            Lease lease = generator.createLease(unit);
            LeaseGenerator.attachDocumentData(lease);

            //Set users that can login using UI
            if (i < DemoData.UserType.PTENANT.getDefaultMax()) {
                Tenant mainTenant = lease.version().tenants().get(0);
                String email = DemoData.UserType.PTENANT.getEmail(i + 1);
                mainTenant.customer().person().email().setValue(email);

                // Make one (Third) Customer with Two Applications
                if (i == 2) {
                    dualPotentialCustomer = mainTenant.customer();
                }

                //Set PCOAPPLICANT users that can login using UI
                if (lease.version().tenants().size() > 1) {
                    Tenant tenant = lease.version().tenants().get(1);
                    String email2 = DemoData.UserType.PCOAPPLICANT.getEmail(i + 1);
                    tenant.customer().person().email().setValue(email2);

                    tenant.role().setValue(LeaseParticipant.Role.CoApplicant);
                    tenant.takeOwnership().setValue(false);
                }
            } else if (i == DemoData.UserType.PTENANT.getDefaultMax()) {
                Tenant mainTenant = lease.version().tenants().get(0);
                mainTenant.customer().set(dualPotentialCustomer);
                mainTenant.screening().set(mainTenant.customer()._PersonScreenings().iterator().next());
            }

            ServerSideFactory.create(LeaseFacade.class).initLease(lease);
            ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(lease.getPrimaryKey());
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " leases");
        return b.toString();
    }

    @Override
    public String delete() {
        return null;
    }

}
