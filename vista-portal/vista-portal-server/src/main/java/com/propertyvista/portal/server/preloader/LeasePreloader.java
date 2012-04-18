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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.LeaseGenerator;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.server.preloader.util.AptUnitSource;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.portal.server.preloader.util.LeaseLifecycleSim;

public class LeasePreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(LeasePreloader.class);

    @Override
    public String create() {
        int numCreated = 0;
        LeaseGenerator generator = new LeaseGenerator(config());
        LeaseLifecycleSim leaseSim = new LeaseLifecycleSim();

        AptUnitSource aptUnitSource = new AptUnitSource(1);

        for (int i = 1; i <= config().numTenants; i++) {

            AptUnit unit = aptUnitSource.next();
            Lease lease = generator.createLease(unit);

            if (i < DemoData.UserType.TENANT.getDefaultMax()) {
                Tenant mainTenant = lease.version().tenants().get(0);
                String email = DemoData.UserType.TENANT.getEmail(i);
                mainTenant.customer().person().email().setValue(email);
            }

            // Create normal Active Lease first for Shortcut users
            if (true || i < DemoData.UserType.TENANT.getDefaultMax()) {
                ServerSideFactory.create(LeaseFacade.class).createLease(lease);
                ServerSideFactory.create(LeaseFacade.class).approveApplication(lease.getPrimaryKey());
                //TODO
                // ServerSideFactory.create(LeaseFacade.class).activate(lease.getPrimaryKey());
            } else {
                leaseSim.createLeaseLifeCycle(lease);
            }

            numCreated++;
        }

        for (int i = 1; i <= config().numPotentialTenants; i++) {

            AptUnit unit = aptUnitSource.next();
            Lease lease = generator.createLease(unit);

            if (i < DemoData.UserType.PTENANT.getDefaultMax()) {
                Tenant mainTenant = lease.version().tenants().get(0);
                String email = DemoData.UserType.PTENANT.getEmail(i);
                mainTenant.customer().person().email().setValue(email);

                //TODO add PCOAPPLICANT users
            }

            ServerSideFactory.create(LeaseFacade.class).createLease(lease);
            //ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(lease.getPrimaryKey());
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
