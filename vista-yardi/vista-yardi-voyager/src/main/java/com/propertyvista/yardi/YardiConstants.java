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
        ping, ImportResidentTransactions, GetPropertyConfigurations, GetUnitInformation, GetResidentTransactions, GetResidentTransaction, GetResidentTransactions_ByChargeDate, GetResidentTransactions_ByApplicationDate, GetResidentLeaseCharges, GetResidentsLeaseCharges, ExportChartOfAccounts, GetVendors, GetVendor
    }

    public static final long TIMEOUT = 10; // minutes

    public static final String NAMESPACE = "http://yardi.com/ResidentTransactions20";

    public static final String USERNAME = "sa";

    public static final String PASSWORD = "akan1212";

    public static final String SERVER_NAME = "WIN-CO5DPAKNUA4\\YARDI";

    public static final String DATABASE = "demo1";

    public static final String PLATFORM = "SQL";

    public static final String INTERFACE_ENTITY = "RentPayment";

    public static final String YARDI_PROPERTY_ID = "0001norg";
}
