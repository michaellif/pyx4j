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

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.server.billing.preload.BuildingDataModel;
import com.propertyvista.server.billing.preload.LeaseDataModel;
import com.propertyvista.server.billing.preload.LocationsDataModel;
import com.propertyvista.server.billing.preload.ProductItemTypesDataModel;
import com.propertyvista.server.billing.preload.ProductTaxPolicyDataModel;
import com.propertyvista.server.billing.preload.TaxesDataModel;
import com.propertyvista.server.billing.preload.TenantDataModel;

public class BillingTestBase extends VistaDBTestBase {

    LeaseDataModel leaseDataModel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LocationsDataModel locationsDataModel = new LocationsDataModel();
        locationsDataModel.generate(true);

        TaxesDataModel taxesDataModel = new TaxesDataModel(locationsDataModel);
        taxesDataModel.generate(true);

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel();
        productItemTypesDataModel.generate(true);

        BuildingDataModel buildingDataModel = new BuildingDataModel(productItemTypesDataModel);
        buildingDataModel.generate(true);

        ProductTaxPolicyDataModel productTaxPolicyDataModel = new ProductTaxPolicyDataModel(productItemTypesDataModel, taxesDataModel, buildingDataModel);
        productTaxPolicyDataModel.generate(true);

        TenantDataModel tenantDataModel = new TenantDataModel();
        tenantDataModel.generate(true);

        leaseDataModel = new LeaseDataModel(buildingDataModel, tenantDataModel);
        leaseDataModel.generate(true);

    }
}
