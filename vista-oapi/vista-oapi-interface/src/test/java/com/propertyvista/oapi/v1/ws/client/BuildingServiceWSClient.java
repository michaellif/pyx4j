/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.ws.client;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.ws.WSPropertyService;
import com.propertyvista.oapi.v1.ws.WSPropertyServiceStub;

public class BuildingServiceWSClient {

    private static boolean isLocal = false;

    public static void main(String[] args) throws MalformedURLException {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("m001@pyx4j.com:vista", "m001@pyx4j.com".toCharArray());
            }
        });

        String address = null;
        if (isLocal) {
            address = "http://localhost:8888/vista/interfaces/oapi/ws/WSPropertyService";
        } else {
            address = "http://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/ws/WSPropertyService?wsdl";
        }

        WSPropertyServiceStub stub = new WSPropertyServiceStub(new URL(address));

        WSPropertyService service = stub.getPropertyServicePort();

        BuildingListIO buildings = service.listAllBuildings();

    }
}
