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

import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Name;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiCustomers;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTUnit;

public class RtCustomerUpdater extends MultiUpdater<RTCustomer, RtCustomerUpdater> {

    public enum YCUSTOMER implements com.propertyvista.yardi.mock.Name {
        Type, CustomerID;
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

    YardiCustomer customer;

    RTUnit rtunit;

    public RtCustomerUpdater(String propertyID, String customerID) {
        assert propertyID != null : "Property with id " + propertyID + " is not found.";
        this.propertyID = propertyID;
        assert customerID != null : "Customer with id " + customerID + " is not found.";
        this.customerID = customerID;

        //=========== <Customer> ===========
        {
            customer = new YardiCustomer();
            addForUpdate(YCUSTOMER.class, customer);

            com.yardi.entity.mits.Name name = new com.yardi.entity.mits.Name();
            customer.setName(name);
            addForUpdate(YCUSTOMERNAME.class, name);

            YardiLease lease = new YardiLease();
            addForUpdate(YLEASE.class, lease);

            customer.setLease(lease);
        }

        //=========== <RT_Unit> ===========
        {
            rtunit = new RTUnit();
            rtunit.setUnitID(customerID.substring(3));

            Unit unit = new Unit();
            Information info = new Information();
            info.setUnitID(rtunit.getUnitID());
            unit.getInformation().add(info);
            addForUpdate(UNITINFO.class, info);

            rtunit.setUnit(unit);
        }
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getCustomerID() {
        return customerID;
    }

    @Override
    public RTCustomer update(RTCustomer rtCustomer) {

        YardiCustomers customers = new YardiCustomers();
        rtCustomer.setCustomers(customers);
        rtCustomer.setPaymentAccepted("0");

        customers.getCustomer().add(customer);

        {
            YardiCustomer customer = new YardiCustomer();
            customer.setType(Customerinfo.CUSTOMER);
            customer.setCustomerID("r" + customerID.substring(1));
            Name name = new Name();
            name.setFirstName("Jane");
            name.setLastName("Smith");
            customer.setName(name);

            YardiLease lease = new YardiLease();
            lease.setResponsibleForLease(true);
            customer.setLease(lease);

            customers.getCustomer().add(customer);
        }

        rtCustomer.setRTUnit(rtunit);

        //run update
        update();

        return rtCustomer;
    }

}
