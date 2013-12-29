/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock.updater;

import com.yardi.entity.resident.RTCustomer;

public class UnitTransferSimulator extends Updater<RTCustomer, UnitTransferSimulator> {

    private final String propertyID;

    private final String customerID;

    private final String newCustomerID;

    private final String newYardiPersonId;

    private final String[] coTenantCustomerIDs;

    /**
     * 
     * @param propertyID
     * @param customerID
     * 
     * @param newCustomerID
     *            the Id for waisted lease
     * 
     * @param newYardiPersonId
     *            the new PersonId for lease with original id
     * 
     * @param coTenantCustomerIDs
     *            new Id to coTenant that will change in existing lease,
     */
    public UnitTransferSimulator(String propertyID, String customerID, String newCustomerID, String newYardiPersonId, String... coTenantCustomerIDs) {
        assert propertyID != null : "propertyID should not be null";
        this.propertyID = propertyID;
        assert customerID != null : "customerID should not be null";
        this.customerID = customerID;

        assert newCustomerID != null : "newCustomerID should not be null";
        this.newCustomerID = newCustomerID;

        assert newYardiPersonId != null : "newYardiPersonId should not be null";
        this.newYardiPersonId = newYardiPersonId;

        this.coTenantCustomerIDs = coTenantCustomerIDs;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getNewCustomerID() {
        return newCustomerID;
    }

    public String getNewYardiPersonId() {
        return newYardiPersonId;
    }

    public String[] getCoTenantCustomerIDs() {
        return coTenantCustomerIDs;
    }
}
