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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiParameters;
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
    public void updateAll(YardiParameters yp) throws YardiServiceException {
        validate(yp);

        YardiClient client = new YardiClient(yp.getServiceURL());

        log.info("Get properties information...");
        List<String> propertyCodes = getPropertyCodes(client, yp);

        log.info("Get all resident transactions...");
        List<ResidentTransactions> allTransactions = getAllResidentTransactions(client, yp, propertyCodes);

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

    private List<String> getPropertyCodes(YardiClient client, YardiParameters yp) throws YardiServiceException {
        List<String> propertyCodes = new ArrayList<String>();
        try {
            Properties properties = YardiTransactionUtils.getPropertyConfigurations(client, yp);
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

    private List<ResidentTransactions> getAllResidentTransactions(YardiClient client, YardiParameters yp, List<String> propertyCodes) {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            try {
                ResidentTransactions residentTransactions = YardiTransactionUtils.getResidentTransactions(client, yp, propertyCode);
                transactions.add(residentTransactions);
            } catch (Exception e) {
                log.error(String.format("Errors during call getResidentTransactions operation for building %s", propertyCode), e);
            }
        }

        return transactions;
    }

}
