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
 */
package com.propertyvista.yardi.mock.updater;

import com.yardi.entity.resident.RTCustomer;

public class RtCustomerUpdater extends Updater<RTCustomer, RtCustomerUpdater> {

    public enum YCUSTOMER implements com.propertyvista.yardi.mock.updater.Name {
        Type, CustomerID, Description /* aka YardiPersonId */;
    }

    public enum YCUSTOMERNAME implements com.propertyvista.yardi.mock.updater.Name {
        FirstName, LastName;
    }

    public enum YCUSTOMERADDRESS implements com.propertyvista.yardi.mock.updater.Name {
        Type, Address1, Address2, City, State, PostalCode, Email;
    }

    public enum YLEASE implements com.propertyvista.yardi.mock.updater.Name {
        CurrentRent, ExpectedMoveInDate, ExpectedMoveOutDate, LeaseFromDate, LeaseToDate, ActualMoveIn, ActualMoveOut, ResponsibleForLease;
    }

    public enum RTCUSTOMER implements com.propertyvista.yardi.mock.updater.Name {
        LeaseID, CustomerID, PaymentAccepted;
    }

    public enum UNITINFO implements com.propertyvista.yardi.mock.updater.Name {
        UnitID, UnitType, UnitBedrooms, UnitBathrooms, UnitRent, FloorPlanID, FloorplanName;
    }

    private final String customerID;

    private final String propertyID;

    public RtCustomerUpdater(String propertyID, String customerID) {
        assert propertyID != null : "propertyID should not be null";
        this.propertyID = propertyID;
        assert customerID != null : "customerID should not be null";
        this.customerID = customerID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getCustomerID() {
        return customerID;
    }

}
