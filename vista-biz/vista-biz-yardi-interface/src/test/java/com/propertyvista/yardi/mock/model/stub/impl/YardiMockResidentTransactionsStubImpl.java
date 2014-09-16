/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.stub.impl;

import java.rmi.RemoteException;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.Propertyidinfo;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiAddress;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiMockResidentTransactionsStubImpl extends YardiMockStubBase implements YardiResidentTransactionsStub {

    @Override
    public String ping(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        Properties properties = new Properties();
        for (YardiBuilding building : YardiMock.server().getModel().getBuildings()) {
            com.propertyvista.yardi.beans.Property property = new com.propertyvista.yardi.beans.Property();
            property.setCode(building.propertyID().getValue());
            properties.getProperties().add(property);
        }
        return properties;
    }

    @Override
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyListCode) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException,
            RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyListCode, LogicalDate date) throws YardiServiceException,
            RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    private Property getProperty(YardiBuilding building) {
        Property property = new Property();
        // set identifier
        PropertyID propertyID = new PropertyID();
        Identification identification = new Identification();
        identification.setType(Propertyidinfo.OTHER);
        identification.setPrimaryID(building.propertyID().getValue());
        identification.setMarketingName("Residential Property " + building.propertyID().getValue());
        propertyID.setIdentification(identification);
        propertyID.getAddress().add(getAddress(building.address()));
        property.getPropertyID().add(propertyID);

        return property;
    }

    // Street, City, Province, Country, Code
    private Address getAddress(YardiAddress ya) {
        Address address = new Address();
        address.setAddress1(ya.street().getValue());
        address.setCity(ya.city().getValue());
        address.setProvince(ya.province().getValue());
        address.setCountry(ya.country().getValue());
        address.setPostalCode(ya.postalCode().getValue());
        return address;
    }
}
