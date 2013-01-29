/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.settings.PmcYardiCredential;

public class DevYardiCredentials {

    public static PmcYardiCredential get2TestPmcYardiCredential() {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);

        cr.residentTransactionsServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/webservices/ItfResidentTransactions20.asmx");
        cr.sysBatchServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/webservices/ItfResidentTransactions20_SysBatch.asmx");
        cr.username().setValue("propertyvistaws");
        cr.credential().setValue("52673");
        cr.serverName().setValue("aspdb04");
        cr.database().setValue("afqoml_live");
        cr.platform().setValue(PmcYardiCredential.Platform.SQL);

        return cr;
    }

    public static PmcYardiCredential getTestPmcYardiCredential() {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);

        cr.residentTransactionsServiceURL().setValue("https://www.iyardiasp.com/8223thirdqa/webservices/itfResidentTransactions20.asmx");
        cr.sysBatchServiceURL().setValue("https://www.iyardiasp.com/8223thirdqa/webservices/itfResidentTransactions20_SysBatch.asmx");
        cr.username().setValue("propertyvista");
        cr.credential().setValue("52673");
        cr.serverName().setValue("aspdb06\\sql2k5");
        cr.database().setValue("afqoml_qa6008");
        cr.platform().setValue(PmcYardiCredential.Platform.SQL);

        return cr;
    }
    // https://www.iyardiasp.com/8223thirddev/webservices/ItfResidentTransactions20_SysBatch.asmx 

    /**
     * test system
     * "http://yardi.birchwoodsoftwaregroup.com/voyager60/webservices/itfresidenttransactions20.asmx";
     * public static final String USERNAME = "sa";
     * public static final String PASSWORD = "akan1212";
     * public static final String SERVER_NAME = "WIN-CO5DPAKNUA4\\YARDI";
     * public static final String DATABASE = "demo1";
     * public static final String PLATFORM = "SQL";
     * public static final String INTERFACE_ENTITY = "RentPayment";
     */
}
