/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.test.preloader.BuildingDataModel;
import com.propertyvista.test.preloader.IdAssignmentPolicyDataModel;
import com.propertyvista.test.preloader.LeaseBillingPolicyDataModel;
import com.propertyvista.test.preloader.LocationsDataModel;
import com.propertyvista.test.preloader.PmcDataModel;
import com.propertyvista.test.preloader.PreloadConfig;
import com.propertyvista.test.preloader.ProductItemTypesDataModel;
import com.propertyvista.test.preloader.TaxesDataModel;

public class YardiTestBase {

    private static final Logger log = LoggerFactory.getLogger(YardiTestBase.class);

    @Before
    public void init() throws Exception {
        VistaTestDBSetup.init();

        Persistence.service().startBackgroundProcessTransaction();

        Lifecycle.startElevatedUserContext();

        NamespaceManager.setNamespace("t" + System.currentTimeMillis());

        TestLifecycle.testSession(null, VistaBasicBehavior.CRM);
        TestLifecycle.testNamespace(NamespaceManager.getNamespace());
        TestLifecycle.beginRequest();

    }

    @After
    public void end() {
        Persistence.service().commit();
    }

    protected void preloadData() {

        PreloadConfig config = new PreloadConfig();
        config.yardiIntegration = true;

        PmcDataModel pmcDataModel = new PmcDataModel(config);
        pmcDataModel.generate();

        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate();

        TaxesDataModel taxesDataModel = new TaxesDataModel(config, locationsDataModel);
        taxesDataModel.generate();

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel(config);
        productItemTypesDataModel.generate();

        BuildingDataModel buildingDataModel = new BuildingDataModel(config, locationsDataModel, productItemTypesDataModel);
        buildingDataModel.generate();

        LeaseBillingPolicyDataModel leaseBillingPolicyDataModel = new LeaseBillingPolicyDataModel(config, buildingDataModel);
        leaseBillingPolicyDataModel.generate();

        IdAssignmentPolicyDataModel idAssignmentPolicyDataModel = new IdAssignmentPolicyDataModel(config, pmcDataModel);
        idAssignmentPolicyDataModel.generate();

        Persistence.service().commit();

    }

}
