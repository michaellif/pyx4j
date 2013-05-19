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

import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Name;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiCustomers;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTUnit;

public class RtCustomerUpdater extends Updater<RTCustomer, RtCustomerUpdater> {

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

    public RtCustomerUpdater(String propertyID, String customerID) {
        assert propertyID != null : "Property with id " + propertyID + " is not found.";
        this.propertyID = propertyID;
        assert customerID != null : "Customer with id " + customerID + " is not found.";
        this.customerID = customerID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getCustomerID() {
        return customerID;
    }

    @Override
    public void update(RTCustomer model) {
        for (com.propertyvista.yardi.mock.Name name : map.keySet()) {
            Property<?> property = map.get(name);
            if (property.getName() instanceof YCUSTOMER) {

                if (model.getCustomers() == null) {
                    model.setCustomers(new YardiCustomers());
                }

                if (model.getCustomers().getCustomer().size() == 0) {
                    model.getCustomers().getCustomer().add(new YardiCustomer());
                }

                updateProperty(model.getCustomers().getCustomer().get(0), property);

            } else if (property.getName() instanceof YCUSTOMERNAME) {

                if (model.getCustomers().getCustomer().get(0).getName() == null) {
                    Name custName = new Name();
                    model.getCustomers().getCustomer().get(0).setName(custName);
                }

                updateProperty(model.getCustomers().getCustomer().get(0).getName(), property);

            } else if (property.getName() instanceof YLEASE) {

                if (model.getCustomers().getCustomer().get(0).getLease() == null) {
                    YardiLease lease = new YardiLease();
                    model.getCustomers().getCustomer().get(0).setLease(lease);
                }

                updateProperty(model.getCustomers().getCustomer().get(0).getLease(), property);

            } else if (property.getName() instanceof UNITINFO) {

                if (model.getRTUnit() == null) {
                    RTUnit rtunit = new RTUnit();
                    rtunit.setUnitID(customerID.substring(3));

                    Unit unit = new Unit();
                    Information info = new Information();
                    info.setUnitID(rtunit.getUnitID());
                    unit.getInformation().add(info);

                    rtunit.setUnit(unit);
                    model.setRTUnit(rtunit);
                }

                updateProperty(model.getRTUnit().getUnit().getInformation().get(0), property);

            }
        }
    }

}
