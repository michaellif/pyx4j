/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.util.List;

import com.propertvista.generator.BuildingsGenerator;
import com.propertvista.generator.LeaseHelper;
import com.propertvista.generator.ProductCatalogGenerator;
import com.propertvista.generator.ProductItemTypesGenerator;
import com.propertvista.generator.TenantsGenerator;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.ProductItemTypesGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.server.common.generator.LocationsGenerator;

public class BillingTestBase extends VistaDBTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ProductItemTypesGDO productItemTypesGDO;

        Building building;

        AptUnit unit;

        {

            List<Province> provinces = LocationsGenerator.loadProvincesFromFile();
            List<Country> countries = LocationsGenerator.createCountries(provinces);

            Persistence.service().persist(countries);
            Persistence.service().persist(provinces);

        }

        {
            ProductItemTypesGenerator generator = new ProductItemTypesGenerator();
            productItemTypesGDO = generator.getGdo();
            Persistence.service().persist(generator.getServiceItemTypes());
            Persistence.service().persist(generator.getFeatureItemTypes());
        }

        {
            BuildingsGenerator generator = new BuildingsGenerator(1);
            building = generator.createBuilding(1);
            Persistence.service().persist(building);

            Floorplan floorplan = EntityFactory.create(Floorplan.class);
            floorplan.building().set(building);
            Persistence.service().persist(floorplan);

            unit = EntityFactory.create(AptUnit.class);
            unit.belongsTo().set(building);
            unit.floorplan().set(floorplan);
            Persistence.service().persist(unit);

        }

        {
            ProductCatalogGenerator productCatalogGenerator = new ProductCatalogGenerator(productItemTypesGDO);

            // Service Catalog:
            ProductCatalog catalog = EntityFactory.create(ProductCatalog.class);

            productCatalogGenerator.createProductCatalog(catalog);
            catalog.belongsTo().set(building);

            Persistence.service().persist(catalog);

            Persistence.service().persist(catalog.features());
            Persistence.service().persist(catalog.concessions());
            Persistence.service().persist(catalog.services());

            Persistence.service().merge(catalog);

            //       List<ProductItem> serviceItems = productCatalogGenerator.createAptUnitServices(catalog, unit);
            //   Persistence.service().persist(serviceItems);

            building.serviceCatalog().set(catalog);

        }

        {
            TenantsGenerator generator = new TenantsGenerator(1);
            Tenant tenant = generator.createTenant();
            Persistence.service().persist(tenant);

            ApplicationSummaryGDO lease = generator.createLease(tenant, unit);

            LeaseHelper.updateLease(lease.lease());
            Persistence.service().persist(lease.lease());
            Persistence.service().persist(lease.lease().leaseFinancial());
            for (TenantSummaryGDO tenantSummary : lease.tenants()) {
                Persistence.service().persist(tenantSummary.tenantInLease());
            }

        }

    }
}
