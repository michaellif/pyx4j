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

        try {
            YardiTransactions.ping(c);
            YardiTransactions.getPropertyConfigurations(c);
            YardiTransactions.getUnitInformationLogin(c);
            YardiTransactions.getResidentTransactionsLogin(c);
            YardiTransactions.getResidentTransactionsByChargeDate(c);
            YardiTransactions.getResidentsLeaseCharges(c);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }
}
