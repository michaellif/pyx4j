/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.GetPropertyConfigurations;
import com.yardi.ws.operations.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;

public class YardiTransactions {

    private final static Logger log = LoggerFactory.getLogger(YardiTransactions.class);

    /**
     * The Ping function accepts no parameters, but will return the
     * assembly name of the function being called. Use it to test
     * connectivity.
     * 
     * @throws RemoteException
     * @throws AxisFault
     */
    public static void ping(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId = 1L; // TODO clean up transaction id management
        c.currentActionName = "ping";
        Ping ping = new Ping();
        PingResponse pr = c.getResidentTransactionsService().ping(ping);
        log.info("result [{}]", pr.getPingResult());
    }

    /**
     * Allows export of the Property Configuration with the
     * Database. The Unique Interface Entity name is needed in order
     * to return the Property ID's the third-party has access to.
     * 
     * @throws RemoteException
     * @throws AxisFault
     */
    public static void getPropertyConfigurations(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId = 2L;
        c.currentActionName = "GetPropertyConfigurations";
        GetPropertyConfigurations l = new GetPropertyConfigurations();
        l.setUserName("propertyvistaws");
        l.setPassword("52673");
        l.setServerName("aspdb04");
        l.setDatabase("afqoml_live");
        l.setPlatform("SQL");
        l.setInterfaceEntity("Property Vista");
        GetPropertyConfigurationsResponse response = c.getResidentTransactionsService().getPropertyConfigurations(l);
        String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();
        log.info("Result: " + xml);
        // TODO need to load this XML into Java code generated from xsd
    }
}
