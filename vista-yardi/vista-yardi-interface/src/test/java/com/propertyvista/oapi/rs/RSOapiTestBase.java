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
package com.propertyvista.oapi.rs;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.test.framework.JerseyTest;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.test.preloader.BuildingDataModel;
import com.propertyvista.test.preloader.LocationsDataModel;
import com.propertyvista.test.preloader.PreloadConfig;
import com.propertyvista.test.preloader.ProductItemTypesDataModel;

public class RSOapiTestBase extends JerseyTest {

    private static final Logger log = LoggerFactory.getLogger(RSOapiTestBase.class);

    public RSOapiTestBase(String... packages) {
        super(packages);
    }

    @Before
    public void initDB() throws Exception {
        VistaTestDBSetup.init();
    }

    protected void preloadData() {
        PreloadConfig config = new PreloadConfig();
        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate();
    }

    protected void preloadBuilding() {
        PreloadConfig config = new PreloadConfig();

        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate();

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel(config);
        productItemTypesDataModel.generate();

        BuildingDataModel buildingDataModel = new BuildingDataModel(config, productItemTypesDataModel);
        buildingDataModel.generate();
    }
}
