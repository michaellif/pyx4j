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
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.ExportChartOfAccounts;
import com.yardi.ws.operations.ExportChartOfAccountsResponse;
import com.yardi.ws.operations.GetPropertyConfigurations;
import com.yardi.ws.operations.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.GetResidentLeaseCharges_Login;
import com.yardi.ws.operations.GetResidentLeaseCharges_LoginResponse;
import com.yardi.ws.operations.GetResidentTransaction_Login;
import com.yardi.ws.operations.GetResidentTransaction_LoginResponse;
import com.yardi.ws.operations.GetResidentTransactions_ByApplicationDate_Login;
import com.yardi.ws.operations.GetResidentTransactions_ByApplicationDate_LoginResponse;
import com.yardi.ws.operations.GetResidentTransactions_ByChargeDate_Login;
import com.yardi.ws.operations.GetResidentTransactions_ByChargeDate_LoginResponse;
import com.yardi.ws.operations.GetResidentTransactions_Login;
import com.yardi.ws.operations.GetResidentTransactions_LoginResponse;
import com.yardi.ws.operations.GetResidentsLeaseCharges_Login;
import com.yardi.ws.operations.GetResidentsLeaseCharges_LoginResponse;
import com.yardi.ws.operations.GetUnitInformation_Login;
import com.yardi.ws.operations.GetUnitInformation_LoginResponse;
import com.yardi.ws.operations.GetVendor_Login;
import com.yardi.ws.operations.GetVendor_LoginResponse;
import com.yardi.ws.operations.GetVendors_Login;
import com.yardi.ws.operations.GetVendors_LoginResponse;
import com.yardi.ws.operations.ImportResidentTransactions_Login;
import com.yardi.ws.operations.ImportResidentTransactions_LoginResponse;
import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;
import com.yardi.ws.operations.TransactionXml_type1;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean2.PhysicalProperty;

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
        c.transactionId++;
        c.setCurrentAction(Action.ping);

        Ping ping = new Ping();
        PingResponse pr = c.getResidentTransactionsService().ping(ping);
        log.info("Connection to Yardi works: {}", pr.getPingResult());
    }

    /**
     * Allows export of the Property Configuration with the
     * Database. The Unique Interface Entity name is needed in order
     * to return the Property ID's the third-party has access to.
     * 
     * This is just a list of properties with not much information in it:
     * Property has code, address, marketing name, accounts payable and accounts receivable
     * 
     * @throws RemoteException
     * @throws AxisFault
     * @throws JAXBException
     */
    public static Properties getPropertyConfigurations(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException {
        c.transactionId++;
        c.setCurrentAction(Action.GetPropertyConfigurations);

        GetPropertyConfigurations l = new GetPropertyConfigurations();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setInterfaceEntity(yp.getInterfaceEntity());
        GetPropertyConfigurationsResponse response = c.getResidentTransactionsService().getPropertyConfigurations(l);
        String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();

        log.debug("Result: {}", xml);
        Properties properties = MarshallUtil.unmarshall(Properties.class, xml);
        log.info("\n--- GetPropertyConfigurations ---\n{}\n", properties);
        return properties;
    }

    /**
     * Allows export of all units and corresponding occupancy status for a given property/property list.
     * 
     * @param c
     * @throws RemoteException
     * @throws AxisFault
     * @throws JAXBException
     */
    public static void getUnitInformationLogin(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException {
        c.transactionId++;
        c.setCurrentAction(Action.GetUnitInformation);

        GetUnitInformation_Login l = new GetUnitInformation_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        GetUnitInformation_LoginResponse response = c.getResidentTransactionsService().getUnitInformation_Login(l);
        String xml = response.getGetUnitInformation_LoginResult().getExtraElement().toString();
        log.info("UnitInformationLogin: {}", xml);

        PhysicalProperty physicalProperty = MarshallUtil.unmarshall(PhysicalProperty.class, xml);
        log.info("\n--- GetUnitInformation ---\n{}\n", physicalProperty);
    }

    /**
     * Allows export of resident/transactional data for a given property/property list.
     */
    public static void getResidentTransactions(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentTransactions);

        GetResidentTransactions_Login l = new GetResidentTransactions_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        GetResidentTransactions_LoginResponse response = c.getResidentTransactionsService().getResidentTransactions_Login(l);
        String xml = response.getGetResidentTransactions_LoginResult().getExtraElement().toString();
        log.info("GetResidentTransactions: {}", xml);
    }

    /**
     * Allows export of resident/transactional data for a given property/property list;
     * will only return one resident based on the Yardi tenant code supplied.
     */
    public static void getResidentTransaction(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentTransaction);

        GetResidentTransaction_Login l = new GetResidentTransaction_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        if (yp.getTenantId() == null) {
            throw new IllegalArgumentException("Tenant ID must be provided when calling GetResidentTransaction");
        }
        l.setTenantId(yp.getTenantId());

        GetResidentTransaction_LoginResponse response = c.getResidentTransactionsService().getResidentTransaction_Login(l);
        String xml = response.getGetResidentTransaction_LoginResult().getExtraElement().toString();
        log.info("ResidentTransactionLogin: {}", xml);
    }

    /**
     * Allows export of resident/transactional data for a given property/property list;
     * will only extract tenants with new charges within the specified date range.
     * Only allows the consumer to pull 7 days worth of information (hours not included).
     */
    public static void getResidentTransactionsByChargeDate(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentTransactions_ByChargeDate);

        GetResidentTransactions_ByChargeDate_Login l = new GetResidentTransactions_ByChargeDate_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        // Consumer may only query for a date range of 31 days or less
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

    /**
     * Allows login & export of resident/transactional data for a given property/property list;
     * will only extract tenants who have submitted an application within the specified date range.
     * Only allows the consumer to pull 7 days worth of information (hours not included).
     */
    public static void getResidentTransactionsByApplicationDate(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentTransactions_ByApplicationDate);

        GetResidentTransactions_ByApplicationDate_Login l = new GetResidentTransactions_ByApplicationDate_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        // Consumer may only query for a date range of 31 days or less
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

        GetResidentTransactions_ByApplicationDate_LoginResponse response = c.getResidentTransactionsService()
                .getResidentTransactions_ByApplicationDate_Login(l);
        String xml = response.getGetResidentTransactions_ByApplicationDate_LoginResult().getExtraElement().toString();
        log.info("GetResidentTransactionsByApplicationDate: {}", xml);
    }

    /**
     * Allows export of resident/transactional data for a given property/property list;
     * will only return one resident based on the Yardi tenant code supplied.
     */
    public static void getResidentsLeaseCharges(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentsLeaseCharges);

        GetResidentsLeaseCharges_Login l = new GetResidentsLeaseCharges_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        Calendar when = GregorianCalendar.getInstance();
        when.set(Calendar.YEAR, 2011);
        when.set(Calendar.MONTH, 1);

        l.setPostMonth(when);

        GetResidentsLeaseCharges_LoginResponse response = c.getResidentTransactionsService().getResidentsLeaseCharges_Login(l);
        String xml = response.getGetResidentsLeaseCharges_LoginResult().getExtraElement().toString();
        log.info("GetResidentsLeaseCharges: {}", xml);
    }

    /**
     * Allows export of resident/transactional data for a given property/property list;
     * will only return one resident based on the Yardi tenant code supplied.
     */
    public static void getResidentLeaseCharges(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentLeaseCharges);

        GetResidentLeaseCharges_Login l = new GetResidentLeaseCharges_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        if (yp.getTenantId() == null) {
            throw new IllegalArgumentException("Tenant Id must be provided for GetResidentLeaseCharges");
        }
        l.setTenantId(yp.getTenantId());

        // post month
        Calendar when = GregorianCalendar.getInstance();
        when.set(Calendar.YEAR, 2011);
        when.set(Calendar.MONTH, 1);
        l.setPostMonth(when);

        GetResidentLeaseCharges_LoginResponse response = c.getResidentTransactionsService().getResidentLeaseCharges_Login(l);
        String xml = response.getGetResidentLeaseCharges_LoginResult().getExtraElement().toString();
        log.info("ResidentLeaseCharges: {}", xml);
    }

    /**
     * Allows export of vendor information from a Yardi database
     */
    public static void getVendors(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetVendors);

        GetVendors_Login l = new GetVendors_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        GetVendors_LoginResponse response = c.getResidentTransactionsService().getVendors_Login(l);
        String xml = response.getGetVendors_LoginResult().getExtraElement().toString();
        log.info("GetVendors: {}", xml);
    }

    /**
     * Allows export of vendor information from a Yardi database
     */
    public static void getVendor(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.GetVendor);

        GetVendor_Login l = new GetVendor_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setYardiPropertyId(yp.getYardiPropertyId());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        if (yp.getVendorId() == null) {
            throw new IllegalArgumentException("Vendor ID must be provided when calling GetVendor");
        }
        l.setVendorId(yp.getVendorId());

        GetVendor_LoginResponse response = c.getResidentTransactionsService().getVendor_Login(l);
        String xml = response.getGetVendor_LoginResult().getExtraElement().toString();
        log.info("GetVendor: {}", xml);
    }

    /**
     * Allows Export of a Chart of Accounts.
     * Yardi Property ID is needed in the case of multiple charts.
     */
    public static void getExportChartOfAccounts(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.ExportChartOfAccounts);

        ExportChartOfAccounts l = new ExportChartOfAccounts();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setInterfaceEntity(yp.getInterfaceEntity());
        l.setPropertyId(yp.getYardiPropertyId());

        ExportChartOfAccountsResponse response = c.getResidentTransactionsService().exportChartOfAccounts(l);
        String xml = response.getExportChartOfAccountsResult().getExtraElement().toString();
        log.info("ExportChartOfAccounts: {}", xml);
    }

    public static void importResidentTransactions(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException, XMLStreamException {
        c.transactionId++;
        c.setCurrentAction(Action.ImportResidentTransactions);

        ImportResidentTransactions_Login l = new ImportResidentTransactions_Login();
        l.setUserName(yp.getUsername());
        l.setPassword(yp.getPassword());
        l.setServerName(yp.getServerName());
        l.setDatabase(yp.getDatabase());
        l.setPlatform(yp.getPlatform());
        l.setInterfaceEntity(yp.getInterfaceEntity());

        TransactionXml_type1 transactionXml = new TransactionXml_type1();
        OMElement element = AXIOMUtil.stringToOM(yp.getTransactionXml());
        transactionXml.setExtraElement(element);
        l.setTransactionXml(transactionXml);
        ImportResidentTransactions_LoginResponse response = c.getResidentTransactionsService().importResidentTransactions_Login(l);
        String xml = response.getImportResidentTransactions_LoginResult().getExtraElement().toString();

        log.info("Result: {}", xml);
//        Properties properties = MarshallUtil.unmarshall(Properties.class, xml);
//        log.info("\n--- ImportResidentTransactions ---\n{}\n", properties);
    }
}
