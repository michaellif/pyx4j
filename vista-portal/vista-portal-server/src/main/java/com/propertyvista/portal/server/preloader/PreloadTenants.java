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
package com.propertyvista.portal.server.preloader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.server.generator.TenantsGenerator;

public class PreloadTenants extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadTenants.class);

    public PreloadTenants(PreloadConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Tenant.class, Person.class, Company.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        TenantsGenerator generator = new TenantsGenerator(DemoData.TENANTS_GENERATION_SEED);

        List<Tenant> tenants = generator.createTenants(config.getNumTenants());
        for (Tenant tenant : tenants) {
            persistTenant(tenant);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(tenants.size()).append(" tenants");
        return sb.toString();
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nLoaded ");

        List<Tenant> tenants = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Tenant>(Tenant.class));
        sb.append(tenants.size()).append(" tenants\n");
        for (Tenant tenant : tenants) {
            sb.append("\t");
            sb.append(tenant);
            sb.append("\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    public void persistTenant(Tenant tenant) {
        switch (tenant.type().getValue()) {
        case person:
            log.debug("Persisting tenant {}", tenant.person().name());
            persist(tenant.person());
            break;
        case company:
            log.debug("Persisting tenant {}", tenant.company().name());
            CmpanyVendorPersistHelper.persistCompany(tenant.company());
            break;
        }
        persist(tenant);
    }

}