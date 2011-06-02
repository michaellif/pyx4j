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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YardiExample {

    private final static Logger log = LoggerFactory.getLogger(YardiExample.class);

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
//        yp.setYardiPropertyId("anya_2");

        // execute different actions
        try {
            // the order of this call should match the document order
            YardiTransactions.ping(c);
            YardiTransactions.getResidentTransactions(c, yp);
//            YardiTransactions.getResidentTransaction(c, yp);
            YardiTransactions.getResidentTransactionsByChargeDate(c, yp);
            YardiTransactions.getResidentTransactionsByApplicationDate(c, yp);
            YardiTransactions.getResidentsLeaseCharges(c, yp);
//            YardiTransactions.getResidentLeaseCharges(c, yp);
            YardiTransactions.getUnitInformationLogin(c, yp);
            YardiTransactions.getVendors(c, yp);
            YardiTransactions.getExportChartOfAccounts(c, yp);
            YardiTransactions.getPropertyConfigurations(c, yp);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }
}
