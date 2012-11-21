/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.model.BuildingInfoIO;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.ContactIO;

public class BuildingMarshaller implements Marshaller<Building, BuildingIO> {

    @Override
    public BuildingIO unmarshal(Building building) {
        BuildingIO buildingRS = initBuildingRS();
        buildingRS.propertyCode = building.propertyCode().getValue();
        buildingRS.info.address.country = building.info().address().country().name().getValue();
        buildingRS.info.address.province = building.info().address().province().name().getValue();
        buildingRS.info.address.city = building.info().address().city().getValue();
        buildingRS.info.address.postalCode = building.info().address().postalCode().getValue();
        buildingRS.info.address.streetNumber = building.info().address().streetNumber().getValue();
        buildingRS.info.address.streetName = building.info().address().streetName().getValue();
        buildingRS.info.address.streetType = building.info().address().streetType().getValue().toString();
        buildingRS.contacts.addAll(getContacts(building));

        return buildingRS;
    }

    private BuildingIO initBuildingRS() {
        BuildingIO buildingRS = new BuildingIO();
        BuildingInfoIO buildingInfoRS = new BuildingInfoIO();
        AddressIO addressRS = new AddressIO();
        buildingInfoRS.address = addressRS;
        buildingRS.info = buildingInfoRS;

        return buildingRS;
    }

    private List<ContactIO> getContacts(Building building) {
        List<ContactIO> contacts = new ArrayList<ContactIO>();
        Persistence.service().retrieve(building.contacts().propertyContacts());
        for (PropertyContact contact : building.contacts().propertyContacts()) {
            if (EnumSet.of(PropertyContactType.mainOffice, PropertyContactType.pointOfSale).contains(contact.type().getValue())) {
                ContactIO contactRS = new ContactIO();
                contactRS.email = contact.email().getValue();
                contactRS.name = contact.name().getValue();
                contactRS.phone = contact.phone().getValue();
                contacts.add(contactRS);
            }
        }
        return contacts;
    }

    @Override
    public Building marshal(BuildingIO buildingRs) {
        return null;
    }

}
