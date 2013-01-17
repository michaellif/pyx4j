/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

public class YardiConstants {

    public enum Action {
        ping, ImportResidentTransactions, GetPropertyConfigurations, GetUnitInformation, GetResidentTransactions, GetResidentTransaction, GetResidentTransactions_ByChargeDate, GetResidentTransactions_ByApplicationDate, GetResidentLeaseCharges, GetResidentsLeaseCharges, ExportChartOfAccounts, GetVendors, GetVendor, OpenReceiptBatch, AddReceiptsToBatch, PostReceiptBatch
    }

    public static final long TIMEOUT = 10; // minutes

    public static final String NAMESPACE = "http://yardi.com/ResidentTransactions20";

    /**
     * In tests use DevYardiCredentials.getTestPmcYardiConnection()
     * in Application use VistaDeployment.getPmcYardiConnection();
     */
    @Deprecated
    public static final String USERNAME = "propertyvistaws";

    @Deprecated
    public static final String PASSWORD = "52673";

    @Deprecated
    public static final String SERVER_NAME = "aspdb04";

    @Deprecated
    public static final String DATABASE = "afqoml_live";

    @Deprecated
    public static final String PLATFORM = "SQL";

    public static final String INTERFACE_ENTITY = "Property Vista";

    public static final String YARDI_PROPERTY_ID = "prvista1";
}
