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
import com.propertyvista.oapi.model.AddressRS;
import com.propertyvista.oapi.model.BuildingInfoRS;
import com.propertyvista.oapi.model.BuildingRS;
import com.propertyvista.oapi.model.ContactRS;

public class BuildingMarshaller implements Marshaller<Building, BuildingRS> {

    @Override
    public BuildingRS unmarshal(Building building) {
        BuildingRS buildingRS = initBuildingRS();
        buildingRS.propertyCode = building.propertyCode().getValue();
        buildingRS.info.address.country = building.info().address().country().name().getValue();
        buildingRS.info.address.province = building.info().address().province().name().getValue();
        buildingRS.info.address.city = building.info().address().city().getValue();
        buildingRS.info.address.postalCode = building.info().address().postalCode().getValue();
        buildingRS.info.address.streetNumber = building.info().address().streetNumber().getValue();
        buildingRS.info.address.streetName = building.info().address().streetName().getValue();
        buildingRS.info.address.streetType = building.info().address().streetType().getValue().toString();
        // detached entity, fix later

        buildingRS.contacts.addAll(getContacts(building));

        return buildingRS;
    }

    private BuildingRS initBuildingRS() {
        BuildingRS buildingRS = new BuildingRS();
        BuildingInfoRS buildingInfoRS = new BuildingInfoRS();
        AddressRS addressRS = new AddressRS();
        buildingInfoRS.address = addressRS;
        buildingRS.info = buildingInfoRS;

        return buildingRS;
    }

    private List<ContactRS> getContacts(Building building) {
        List<ContactRS> contacts = new ArrayList<ContactRS>();
        Persistence.service().retrieve(building.contacts().propertyContacts());
        for (PropertyContact contact : building.contacts().propertyContacts()) {
            if (EnumSet.of(PropertyContactType.mainOffice, PropertyContactType.pointOfSale).contains(contact.type().getValue())) {
                ContactRS contactRS = new ContactRS();
                contactRS.email = contact.email().getValue();
                contactRS.name = contact.name().getValue();
                contactRS.phone = contact.phone().getValue();
                contacts.add(contactRS);
            }
        }
        return contacts;
    }

    @Override
    public Building marshal(BuildingRS buildingRs) {
        return null;
    }

}
