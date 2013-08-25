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

import com.yardi.entity.mits.YardiCustomer;

public class CoTenantUpdater extends Updater<YardiCustomer, CoTenantUpdater> {

    public enum YCUSTOMER implements com.propertyvista.yardi.mock.Name {
        Type, CustomerID;
    }

    public enum YCUSTOMERNAME implements com.propertyvista.yardi.mock.Name {
        FirstName, LastName;
    }

    public enum YCUSTOMERADDRESS implements com.propertyvista.yardi.mock.Name {
        Email;
    }

    public enum YLEASE implements com.propertyvista.yardi.mock.Name {
        ActualMoveIn, ResponsibleForLease;
    }

    private final String propertyID;

    private final String customerID;

    private final String coTenantCustomerID;

    public CoTenantUpdater(String propertyID, String customerID, String coTenantCustomerID) {
        assert propertyID != null : "propertyID should not be null";
        this.propertyID = propertyID;
        assert customerID != null : "customerID should not be null";
        this.customerID = customerID;
        assert coTenantCustomerID != null : "co-tenant customerID should not be null";
        this.coTenantCustomerID = coTenantCustomerID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCoTenantCustomerID() {
        return coTenantCustomerID;
    }

}
