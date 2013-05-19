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

import com.yardi.entity.mits.Name;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiLease;

public class CoTenantUpdater extends Updater<YardiCustomer, CoTenantUpdater> {

    public enum YCUSTOMER implements com.propertyvista.yardi.mock.Name {
        Type, CustomerID;
    }

    public enum YCUSTOMERNAME implements com.propertyvista.yardi.mock.Name {
        FirstName, LastName;
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

    @Override
    public void update(YardiCustomer model) {
        for (com.propertyvista.yardi.mock.Name name : map.keySet()) {
            Property<?> property = map.get(name);
            if (property.getName() instanceof YCUSTOMER) {

                updateProperty(model, property);

            } else if (property.getName() instanceof YCUSTOMERNAME) {

                if (model.getName() == null) {
                    Name custName = new Name();
                    model.setName(custName);
                }

                updateProperty(model.getName(), property);

            } else if (property.getName() instanceof YLEASE) {

                if (model.getLease() == null) {
                    YardiLease lease = new YardiLease();
                    model.setLease(lease);
                }

                updateProperty(model.getLease(), property);

            }
        }
    }

}
