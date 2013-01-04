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
package com.propertyvista.oapi.ws;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.test.preloader.LocationsDataModel;
import com.propertyvista.test.preloader.PreloadConfig;

public class WSOapiTestBase {

    private static final Logger log = LoggerFactory.getLogger(WSOapiTestBase.class);

    private int port;

    private Class<?> serviceClass;

    Endpoint endpoint = null;

    @Before
    public void initDB() throws Exception {
        VistaTestDBSetup.init();
    }

    @After
    public void stop() {
        if (endpoint != null) {
            endpoint.stop();
        }
        endpoint = null;
    }

    public int getPort() {
        return port;
    }

    private String getAddress(int port) {
        return "http://localhost:" + port + "/WS/" + serviceClass.getSimpleName();
    }

    String getAddress() {
        return getAddress(port);
    }

    void publish(Class<?> serviceClass) throws Exception {
        assert endpoint == null;
        this.serviceClass = serviceClass;
        port = 7771;
        int monitorPort = port;
        //For TCP/IP monitor
        if (false) {
            port = 9999;
            monitorPort = 9090;
        }
        String address = getAddress(monitorPort);
        log.info("Address: {}", address);
        endpoint = Endpoint.publish(address, Class.forName(serviceClass.getName() + "Impl").newInstance());
    }

    int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

    protected void preloadData() {
        PreloadConfig config = new PreloadConfig();
        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate();

//        TaxesDataModel taxesDataModel = new TaxesDataModel(config, locationsDataModel);
//        taxesDataModel.generate();
//
//        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel(config);
//        productItemTypesDataModel.generate();
//
//        LeaseAdjustmentReasonDataModel leaseAdjustmentReasonDataModel = new LeaseAdjustmentReasonDataModel(config);
//        leaseAdjustmentReasonDataModel.generate();
//
//        BuildingDataModel buildingDataModel = new BuildingDataModel(config, productItemTypesDataModel);
//        buildingDataModel.generate();
//
//        IdAssignmentPolicyDataModel idAssignmentPolicyDataModel = new IdAssignmentPolicyDataModel(config);
//        idAssignmentPolicyDataModel.generate();
//
//        ProductTaxPolicyDataModel productTaxPolicyDataModel = new ProductTaxPolicyDataModel(config, productItemTypesDataModel, taxesDataModel,
//                buildingDataModel);
//        productTaxPolicyDataModel.generate();
//
//        DepositPolicyDataModel depositPolicyDataModel = new DepositPolicyDataModel(config, productItemTypesDataModel, buildingDataModel);
//        depositPolicyDataModel.generate();
//
//        LeaseAdjustmentPolicyDataModel leaseAdjustmentPolicyDataModel = new LeaseAdjustmentPolicyDataModel(config, leaseAdjustmentReasonDataModel,
//                taxesDataModel, buildingDataModel);
//        leaseAdjustmentPolicyDataModel.generate();
//
//        TenantDataModel tenantDataModel = new TenantDataModel(config);
//        tenantDataModel.generate();
//
//        //TODO if commented - check exception
//        LeaseBillingPolicyDataModel leaseBillingPolicyDataModel = new LeaseBillingPolicyDataModel(config, buildingDataModel);
//        leaseBillingPolicyDataModel.generate();
//
//        ARPolicyDataModel arPolicyDataModel = new ARPolicyDataModel(config, buildingDataModel);
//        arPolicyDataModel.generate();
    }

}
