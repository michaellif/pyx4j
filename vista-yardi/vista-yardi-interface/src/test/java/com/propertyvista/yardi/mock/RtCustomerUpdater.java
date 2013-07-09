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
package com.propertyvista.yardi.mock;

import com.yardi.entity.resident.RTCustomer;

public class RtCustomerUpdater extends Updater<RTCustomer, RtCustomerUpdater> {

    public enum YCUSTOMER implements com.propertyvista.yardi.mock.Name {
        Type, CustomerID, PaymentAccepted;
    }

    public enum YCUSTOMERNAME implements com.propertyvista.yardi.mock.Name {
        FirstName, LastName;
    }

    public enum YLEASE implements com.propertyvista.yardi.mock.Name {
        CurrentRent, LeaseFromDate, LeaseToDate, ResponsibleForLease;
    }

    public enum UNITINFO implements com.propertyvista.yardi.mock.Name {
        UnitType, UnitBedrooms, UnitBathrooms, UnitRent, FloorPlanID, FloorplanName;
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
