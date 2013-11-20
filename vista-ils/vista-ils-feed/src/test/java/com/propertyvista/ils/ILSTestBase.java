/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.MerchantAccountDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;

public abstract class ILSTestBase extends IntegrationTestBase {

    private Building building;

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(TaxesDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        return models;
    }

    protected Building getBuilding() {
        if (building == null) {
            building = getDataModel(BuildingDataModel.class).addBuilding();
            getDataModel(MerchantAccountDataModel.class).addMerchantAccount(building);
            Persistence.service().commit();
        }
        return building;
    }
}
