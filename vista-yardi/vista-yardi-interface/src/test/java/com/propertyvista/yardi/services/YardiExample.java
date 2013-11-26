/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.domain.property.yardi.YardiPropertyConfiguration;
import com.propertyvista.server.config.DevYardiCredentials;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiExample {

    private final static Logger log = LoggerFactory.getLogger(YardiExample.class);

    public static void main(String[] args) {
        ping();
        getPropertyConfigurations();
        getResidentTransactions();
        getUnitInformation();
        getResidentsLeaseCharges();
    }

    private static void ping() {
        try {
            YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
            YardiResidentTransactionsService.getInstance().ping(stub, DevYardiCredentials.getTestPmcYardiCredential());
        } catch (Throwable e) {
            log.error("error", e);
        }
    }

    private static void getPropertyConfigurations() {
        try {
            YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
            List<YardiPropertyConfiguration> properties = YardiResidentTransactionsService.getInstance().getPropertyConfigurations(stub,
                    DevYardiCredentials.getTestPmcYardiCredential());
            System.out.println(properties);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }

    private static void getResidentTransactions() {
        try {
            YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
            ResidentTransactions residentTransactions = stub.getAllResidentTransactions(DevYardiCredentials.getTestPmcYardiCredential(), DevYardiCredentials
                    .getTestPmcYardiCredential().propertyListCodes().getValue());
            System.out.println(residentTransactions);
        } catch (Throwable e) {
            log.error("error", e);
        }

    }

    private static void getUnitInformation() {
        try {
            YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
            stub.getUnitInformation(DevYardiCredentials.getTestPmcYardiCredential(), DevYardiCredentials.getTestPmcYardiCredential().propertyListCodes().getValue());

        } catch (Throwable e) {
            log.error("error", e);
        }

    }

    private static void getResidentsLeaseCharges() {
        try {
            YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
            stub.getAllLeaseCharges(DevYardiCredentials.getTestPmcYardiCredential(), DevYardiCredentials.getTestPmcYardiCredential().propertyListCodes().getValue(),
                    new LogicalDate());

        } catch (Throwable e) {
            log.error("error", e);
        }

    }
}
