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
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.GetPropertyConfigurations;
import com.yardi.ws.operations.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.GetResidentTransactions_ByChargeDate_Login;
import com.yardi.ws.operations.GetResidentTransactions_ByChargeDate_LoginResponse;
import com.yardi.ws.operations.GetResidentTransactions_Login;
import com.yardi.ws.operations.GetResidentTransactions_LoginResponse;
import com.yardi.ws.operations.GetResidentsLeaseCharges_Login;
import com.yardi.ws.operations.GetResidentsLeaseCharges_LoginResponse;
import com.yardi.ws.operations.GetUnitInformation_Login;
import com.yardi.ws.operations.GetUnitInformation_LoginResponse;
import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.yardi.bean.Properties;

public class YardiTransactions {

    private final static Logger log = LoggerFactory.getLogger(YardiTransactions.class);

    public static final String USERNAME = "propertyvistaws";

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
     * @throws JAXBException
     */
    public static Properties getPropertyConfigurations(YardiClient c) throws AxisFault, RemoteException, JAXBException {
        c.transactionId = 2L;
        c.currentActionName = "GetPropertyConfigurations";
        GetPropertyConfigurations l = new GetPropertyConfigurations();
        l.setUserName(USERNAME);
        l.setPassword("52673");
        l.setServerName("aspdb04");
        l.setDatabase("afqoml_live");
        l.setPlatform("SQL");
        l.setInterfaceEntity("Property Vista");
        GetPropertyConfigurationsResponse response = c.getResidentTransactionsService().getPropertyConfigurations(l);
        String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();

        log.info("Result: {}", xml);
        Properties properties = MarshallUtil.unmarshall(Properties.class, xml);
        log.info("Properties: {}", properties);
        return properties;
    }

    /**
     * Allows export of all units and corresponding occupancy status for a given property/property list.
     * 
     * @param c
     * @throws RemoteException
     * @throws AxisFault
     */
    public static void getUnitInformationLogin(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId = 3L;

        c.currentActionName = "GetUnitInformation";
        GetUnitInformation_Login l = new GetUnitInformation_Login();
        l.setUserName(USERNAME);
        l.setPassword("52673");
        l.setServerName("aspdb04");
        l.setDatabase("afqoml_live");
        l.setPlatform("SQL");
        l.setYardiPropertyId("prvista1");
        l.setInterfaceEntity("Property Vista");
        GetUnitInformation_LoginResponse response = c.getResidentTransactionsService().getUnitInformation_Login(l);
        String xml = response.getGetUnitInformation_LoginResult().getExtraElement().toString();
        log.info("UnitInformationLogin: {}", xml);
    }

    public static void getResidentTransactionsLogin(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId++;
        c.currentActionName = "GetResidentTransactions";

        GetResidentTransactions_Login l = new GetResidentTransactions_Login();
        l.setUserName(USERNAME);
        l.setPassword("52673");
        l.setServerName("aspdb04");
        l.setDatabase("afqoml_live");
        l.setPlatform("SQL");
        l.setYardiPropertyId("prvista1");
        l.setInterfaceEntity("Property Vista");

        GetResidentTransactions_LoginResponse response = c.getResidentTransactionsService().getResidentTransactions_Login(l);
        String xml = response.getGetResidentTransactions_LoginResult().getExtraElement().toString();
        log.info("ResidentTransactionsLogin: {}", xml);
    }

    public static void getResidentTransactionsByChargeDate(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId++;
        c.currentActionName = "GetResidentTransactions_ByChargeDate";
        GetResidentTransactions_ByChargeDate_Login l = new GetResidentTransactions_ByChargeDate_Login();
        l.setUserName(USERNAME);
        l.setPassword("52673");
        l.setServerName("aspdb04");
        l.setDatabase("afqoml_live");
        l.setPlatform("SQL");
        l.setYardiPropertyId("prvista1");
        l.setInterfaceEntity("Property Vista");

        //Consumer may only query for a date range of 31 days or less
        Calendar from = GregorianCalendar.getInstance();
        from.set(Calendar.YEAR, 2011);
        from.set(Calendar.MONTH, 2);
        from.set(Calendar.DAY_OF_MONTH, 1);

        l.setFromDate(from);

        Calendar to = GregorianCalendar.getInstance();
        to.set(Calendar.YEAR, 2011);
        to.set(Calendar.MONTH, 2);
        to.set(Calendar.DAY_OF_MONTH, 28);

        l.setToDate(to);

        GetResidentTransactions_ByChargeDate_LoginResponse response = c.getResidentTransactionsService().getResidentTransactions_ByChargeDate_Login(l);
        String xml = response.getGetResidentTransactions_ByChargeDate_LoginResult().getExtraElement().toString();
        log.info("GetResidentTransactionsByChargeDate: {}", xml);
    }

    public static void getResidentsLeaseCharges(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId++;

        c.currentActionName = "GetResidentsLeaseCharges";
        GetResidentsLeaseCharges_Login l = new GetResidentsLeaseCharges_Login();
        l.setUserName(USERNAME);
        l.setPassword("52673");
        l.setServerName("aspdb04");
        l.setDatabase("afqoml_live");
        l.setPlatform("SQL");
        l.setYardiPropertyId("prvista1");
        l.setInterfaceEntity("Property Vista");

        Calendar when = GregorianCalendar.getInstance();
        when.set(Calendar.YEAR, 2011);
        when.set(Calendar.MONTH, 1);

        l.setPostMonth(when);

        GetResidentsLeaseCharges_LoginResponse response = c.getResidentTransactionsService().getResidentsLeaseCharges_Login(l);
        String xml = response.getGetResidentsLeaseCharges_LoginResult().getExtraElement().toString();
        log.info("ResidentsLeaseCharges: {}", xml);
    }
}
