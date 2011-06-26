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
package com.propertyvista.yardi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.reference.SharedData;

public class YardiInteractiveExample {

    private final static Logger log = LoggerFactory.getLogger(YardiInteractiveExample.class);

    public static void main(String[] args) {
        YardiClient c = new YardiClient();

        // Anya, use this code section to configure the parameters you would like to be sending
        YardiParameters yp = new YardiParameters();
        yp.setUsername(YardiConstants.USERNAME);
        yp.setPassword(YardiConstants.PASSWORD);
        yp.setServerName(YardiConstants.SERVER_NAME);
        yp.setDatabase(YardiConstants.DATABASE);
        yp.setPlatform(YardiConstants.PLATFORM);
        yp.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        yp.setYardiPropertyId(YardiConstants.YARDI_PROPERTY_ID);

        // init db
        VistaTestsServerSideConfiguration conf = new VistaTestsServerSideConfiguration(true);
        ServerSideConfiguration.setInstance(conf);
        SharedData.init();

        // execute different actions
        try {
            while (true) {
                StringBuilder sb = new StringBuilder();
                sb.append("Enter command: \n");
                sb.append("1: GetPropertyConfigurations\n");
                sb.append("2: GetResidentTransactions\n");
                sb.append("0: Exit\n");
                System.out.println(sb.toString());
                String command = read();
                if (command.equals("0")) {
                    break;
                }
                process(command, c, yp);
            }
            System.out.println("Interactive session is finished");
        } catch (Throwable e) {
            log.error("error", e);
        }
    }

    private static void process(String command, YardiClient c, YardiParameters yp) throws JAXBException, IOException {
        if (command.equals("1")) {
            doGetPropertyConfigurations(c, yp);
        } else if (command.equals("2")) {
            YardiTransactions.getResidentTransactions(c, yp);
        }
    }

    private static void doGetPropertyConfigurations(YardiClient c, YardiParameters yp) throws JAXBException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("- GetPropertyConfigurations -\n");
        sb.append("1: Download from Yardi\n");
        sb.append("2: Load from local database\n");
        sb.append("3: Merge\n");
        System.out.println(sb);
        String command = read();
        GetPropertyConfigurationLifecycle lifecycle = new GetPropertyConfigurationLifecycle();
        if (command.equals("1")) {
            List<Building> buildings = lifecycle.download(c, yp);
            log.info("Has {} buildings", buildings.size());
        } else if (command.equals("2")) {
            List<Building> buildings = lifecycle.load();
            log.info("Has {} buildings", buildings.size());
        } else if (command.equals("3")) {
            List<Building> buildings = lifecycle.merge(c, yp);
            log.info("Merged {} buildings", buildings.size());
        }
    }

    private static String read() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String token;
        token = br.readLine();
        return token;
    }
}
