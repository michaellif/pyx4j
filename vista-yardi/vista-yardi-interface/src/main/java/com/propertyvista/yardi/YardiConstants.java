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
        Ping, ImportResidentTransactions, GetPropertyConfigurations, GetUnitInformation,

        GetResidentTransactions, GetResidentTransaction, GetResidentTransactions_ByChargeDate,

        GetResidentTransactions_ByApplicationDate, GetResidentLeaseCharges, GetResidentsLeaseCharges,

        GetPropertyMarketingInfo, GetYardiRentableItems, GetYardiMarketingSources, ImportGuestInfo, ImportApplication,

        ExportChartOfAccounts, GetVendors, GetVendor, OpenReceiptBatch, AddReceiptsToBatch, PostReceiptBatch, CancelReceiptBatch,

        GetServiceRequests, CreateOrEditServiceRequests, GetCustomValues
    }

    public static final long TIMEOUT = 180; // minutes

    public static final String INTERFACE_ENTITY = "Property Vista";

    public static final String MAINTENANCE_INTERFACE_ENTITY = "Property Vista-Maintenance";

    public static final String ILS_INTERFACE_ENTITY = "Property Vista-ILS";
}
