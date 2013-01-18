/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 13, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.operations.GetPropertyConfigurations;
import com.yardi.ws.operations.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.GetResidentTransactions_Login;
import com.yardi.ws.operations.GetResidentTransactions_LoginResponse;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiServiceException;
import com.propertyvista.yardi.bean.Properties;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiResidentTransactionsService extends YardiAbstarctService {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsService.class);

    private static class SingletonHolder {
        public static final YardiResidentTransactionsService INSTANCE = new YardiResidentTransactionsService();
    }

    private YardiResidentTransactionsService() {
    }

    public static YardiResidentTransactionsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Updates/creates entities basing on data from YARDI System.
     * 
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateAll(PmcYardiCredential yc) throws YardiServiceException {

        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());

        log.info("Get properties information...");
        List<String> propertyCodes = getPropertyCodes(client, yc);

        log.info("Get all resident transactions...");
        List<ResidentTransactions> allTransactions = getAllResidentTransactions(client, yc, propertyCodes);

        updateBuildings(allTransactions);

        updateLeases(allTransactions);

        updateCharges(allTransactions);

    }

    private void updateLeases(List<ResidentTransactions> allTransactions) {
        new YardiLeaseProcessor().updateLeases(allTransactions);
    }

    private void updateBuildings(List<ResidentTransactions> allTransactions) {
        new YardiBuildingProcessor().updateBuildings(allTransactions);
    }

    private void updateCharges(List<ResidentTransactions> allTransactions) {
        new YardiChargeProcessor().updateCharges(allTransactions);
    }

    private List<String> getPropertyCodes(YardiClient client, PmcYardiCredential yc) throws YardiServiceException {
        List<String> propertyCodes = new ArrayList<String>();
        try {
            Properties properties = getPropertyConfigurations(client, yc);
            for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
                if (StringUtils.isNotEmpty(property.getCode())) {
                    propertyCodes.add(property.getCode());
                }
            }
            return propertyCodes;
        } catch (Exception e) {
            throw new YardiServiceException("Fail to get properties information from YARDI System", e);
        }
    }

    private List<ResidentTransactions> getAllResidentTransactions(YardiClient client, PmcYardiCredential yc, List<String> propertyCodes) {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            try {
                ResidentTransactions residentTransactions = getResidentTransactions(client, yc, propertyCode);
                transactions.add(residentTransactions);
            } catch (Exception e) {
                log.error(String.format("Errors during call getResidentTransactions operation for building %s", propertyCode), e);
            }
        }

        return transactions;
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
    private Properties getPropertyConfigurations(YardiClient c, PmcYardiCredential yc) throws AxisFault, RemoteException, JAXBException {
        c.transactionId++;
        c.setCurrentAction(Action.GetPropertyConfigurations);

        GetPropertyConfigurations l = new GetPropertyConfigurations();
        l.setUserName(yc.username().getValue());
        l.setPassword(yc.credential().getValue());
        l.setServerName(yc.serverName().getValue());
        l.setDatabase(yc.database().getValue());
        l.setPlatform(yc.platform().getValue().name());
        l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        GetPropertyConfigurationsResponse response = c.getResidentTransactionsService().getPropertyConfigurations(l);
        String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();

        if (log.isDebugEnabled()) {
            log.debug("GetPropertyConfigurations Result: {}", xml);
        }

        Properties properties = MarshallUtil.unmarshal(Properties.class, xml);

        if (log.isDebugEnabled()) {
            log.debug("\n--- GetPropertyConfigurations ---\n{}\n", properties);
        }

        return properties;
    }

    /**
     * Allows export of resident/transactional data for a given property/property list.
     * 
     * @throws JAXBException
     */
    private ResidentTransactions getResidentTransactions(YardiClient c, PmcYardiCredential yc, String propertyId) throws AxisFault, RemoteException,
            JAXBException {
        c.transactionId++;
        c.setCurrentAction(Action.GetResidentTransactions);

        GetResidentTransactions_Login l = new GetResidentTransactions_Login();
        l.setUserName(yc.username().getValue());
        l.setPassword(yc.credential().getValue());
        l.setServerName(yc.serverName().getValue());
        l.setDatabase(yc.database().getValue());
        l.setPlatform(yc.platform().getValue().name());
        l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        l.setYardiPropertyId(propertyId);

        GetResidentTransactions_LoginResponse response = c.getResidentTransactionsService().getResidentTransactions_Login(l);
        String xml = response.getGetResidentTransactions_LoginResult().getExtraElement().toString();

        log.info("GetResidentTransactions: {}", xml);

        ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
        return transactions;
    }
}
