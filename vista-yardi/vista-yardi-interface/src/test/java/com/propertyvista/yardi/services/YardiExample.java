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

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.server.config.DevYardiCredentials;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.bean.Properties;

public class YardiExample {

    private final static Logger log = LoggerFactory.getLogger(YardiExample.class);

    public static void main(String[] args) {
        YardiClient client = ServerSideFactory.create(YardiClient.class);
        client.setPmcYardiCredential(DevYardiCredentials.getTestPmcYardiCredential());
        getPropertyConfigurations(client);
        getPropertyCodes(client);
        getResidentTransactions(client);
        getUnitInformation(client);
    }

    private static void getPropertyConfigurations(YardiClient client) {
        try {
            Properties properties = YardiResidentTransactionsService.getInstance().getPropertyConfigurations(client,
                    DevYardiCredentials.getTestPmcYardiCredential());
            System.out.println(properties);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }

    private static void getPropertyCodes(YardiClient client) {
        try {
            List<String> properties = YardiResidentTransactionsService.getInstance().getPropertyCodes(client, DevYardiCredentials.getTestPmcYardiCredential());
            System.out.println(properties);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }

    private static void getResidentTransactions(YardiClient client) {
        try {
            ResidentTransactions residentTransactions = YardiResidentTransactionsService.getInstance().getResidentTransactions(client,
                    DevYardiCredentials.getTestPmcYardiCredential(), DevYardiCredentials.getTestPmcYardiCredential().propertyCode().getValue());
            System.out.println(residentTransactions);
        } catch (Throwable e) {
            log.error("error", e);
        }

    }

    private static void getUnitInformation(YardiClient client) {
        try {
            YardiResidentTransactionsService.getInstance().getUnitInformation(client, DevYardiCredentials.getTestPmcYardiCredential(),
                    DevYardiCredentials.getTestPmcYardiCredential().propertyCode().getValue());

        } catch (Throwable e) {
            log.error("error", e);
        }

    }
}
