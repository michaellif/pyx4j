/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertvista.generator.II.test;

import com.propertvista.generator.II.DataModel;
import com.propertvista.generator.II.TreeGenerator;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaDevPreloadConfig;

public class LeaseGenerator implements TreeGenerator<Lease> {

    private final DataModel dataSource;

    private final VistaDevPreloadConfig config;

    public LeaseGenerator(DataModel dataSource, VistaDevPreloadConfig config) {
        this.dataSource = dataSource;
        this.config = config;
    }

    @Override
    public Lease generate() {
        Lease lease = EntityFactory.create(Lease.class);

        for (int i = 0; i < config.numTenantsInLease; i++) {

        }
        TenantInLease tl = EntityFactory.create(TenantInLease.class);
        lease.tenants().add(tl);
        tl.role().setValue(Role.Applicant);
        // tl.tenant().set(dataSource.retreive(Tenant.class, null));

        return lease;
    }
}
