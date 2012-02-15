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
package com.propertyvista.server.billing.preload;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseGenerator {

    private final DataModel dataModel;

    private Lease lease;

    public LeaseGenerator(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public void generate() {
        Lease lease = EntityFactory.create(Lease.class);
        TenantInLease tl = EntityFactory.create(TenantInLease.class);
        lease.tenants().add(tl);
        tl.role().setValue(Role.Applicant);
    }

    public static Lease generate(DataModel dataModel) {
        LeaseGenerator generator = new LeaseGenerator(dataModel);
        generator.generate();
        return generator.lease;
    }
}
