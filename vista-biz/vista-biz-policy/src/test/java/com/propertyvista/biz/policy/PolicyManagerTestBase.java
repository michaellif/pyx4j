/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.AutoPayPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.DepositPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseAdjustmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.MerchantAccountDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.ProductTaxPolicyDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;

public class PolicyManagerTestBase extends IntegrationTestBase {

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(TaxesDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(MerchantAccountDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(ProductTaxPolicyDataModel.class);
        models.add(DepositPolicyDataModel.class);
        models.add(LeaseAdjustmentPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        models.add(AutoPayPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(LeaseDataModel.class);
        return models;
    }

    protected void makeBuilding(Province province) {
        Building buildingOn1 = getDataModel(BuildingDataModel.class).addBuilding("ON");
        getDataModel(BuildingDataModel.class).addResidentialUnitServiceItem(buildingOn1, new BigDecimal("1.00"));
        getDataModel(BuildingDataModel.class).addResidentialUnitServiceItem(buildingOn1, new BigDecimal("1.00"));

        Building buildingOn2 = getDataModel(BuildingDataModel.class).addBuilding("ON");
        Building buildingAb1 = getDataModel(BuildingDataModel.class).addBuilding("AB");
        Building buildingAb2 = getDataModel(BuildingDataModel.class).addBuilding("AB");

    }

}
