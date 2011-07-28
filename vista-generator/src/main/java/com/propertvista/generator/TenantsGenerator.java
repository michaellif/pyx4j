/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.tenant.Tenant;

public class TenantsGenerator {

    public TenantsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
    }

    public List<Tenant> createTenants(int numTenants) {
        List<Tenant> tenants = new ArrayList<Tenant>();
        for (int i = 0; i < numTenants; i++) {
            tenants.add(createTenant());
        }
        return tenants;
    }

    public Tenant createTenant() {
        Tenant tenant = EntityFactory.create(Tenant.class);

        tenant.type().setValue(RandomUtil.random(Tenant.Type.values()));
        switch (tenant.type().getValue()) {
        case person:
            tenant.person().set(CommonsGenerator.createPerson());
            break;
        case company:
            tenant.company().set(CompanyVendor.createCompany());
            break;
        }

        return tenant;
    }
}
