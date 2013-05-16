/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

import org.apache.commons.lang.SerializationUtils;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.yardi.bean.Property;

public class PropertyManager {

    private final String propertyId;

    private final Property property;

    private final ResidentTransactions transactions;

    public PropertyManager(String propertyId) {
        this.propertyId = propertyId;
        property = new Property();
        transactions = new ResidentTransactions();

        com.yardi.entity.resident.Property rtProperty = new com.yardi.entity.resident.Property();
        PropertyID propertyID = new PropertyID();
        Identification identification = new Identification();
        identification.setPrimaryID(propertyId);
        identification.setMarketingName("Marketing" + propertyId);

        Address address = new Address();
        address.setAddress1("11 " + propertyId + " str");
        address.setCountry("Canada");

        propertyID.getAddress().add(address);

        propertyID.setIdentification(identification);
        rtProperty.getPropertyID().add(propertyID);
        transactions.getProperty().add(rtProperty);
    }

    public ResidentTransactions getAllResidentTransactions() {
        try {
            return (ResidentTransactions) SerializationUtils.clone(transactions);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ResidentTransactions getResidentTransactionsForTenant(String tenantId) {
        return transactions;
    }
}
