/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2012
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi;

import com.propertyvista.config.tests.VistaTestDBSetup;

public class YardiServicesClient {

    private static final String serviceURL = "https://www.iyardiasp.com/8223thirddev/webservices/itfresidenttransactions20.asmx";

    private static YardiPropertyService propertyService = new YardiPropertyService();

    /**
     * @param args
     * @throws YardiServiceException
     */
    public static void main(String[] args) throws YardiServiceException {
        YardiParameters yp = new YardiParameters();
        yp.setServiceURL(serviceURL);
        yp.setUsername(YardiConstants.USERNAME);
        yp.setPassword(YardiConstants.PASSWORD);
        yp.setServerName(YardiConstants.SERVER_NAME);
        yp.setDatabase(YardiConstants.DATABASE);
        yp.setPlatform(YardiConstants.PLATFORM);
        yp.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        yp.setYardiPropertyId(YardiConstants.YARDI_PROPERTY_ID);

        //db setup
        VistaTestDBSetup.init();

        //test services
        propertyService.updateBuildings(yp);
        propertyService.updateBuilding("anya_4", yp);
        propertyService.updateUnits("anya_4", yp);
        //throws exception 
        propertyService.updateUnit("anya_4", "555", yp);
    }
}
