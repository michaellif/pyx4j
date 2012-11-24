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
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.ContactIO;

public class BuildingMarshaller implements Marshaller<Building, BuildingIO> {

    @Override
    public BuildingIO unmarshal(Building building) {
        BuildingIO buildingIO = new BuildingIO();
        buildingIO.info = new BuildingInfoMarshaller().unmarshal(building.info());
        buildingIO.propertyCode = building.propertyCode().getValue();

        List<ContactIO> contacts = new ArrayList<ContactIO>();
        Persistence.service().retrieve(building.contacts().propertyContacts());
        for (PropertyContact contact : building.contacts().propertyContacts()) {
            if (EnumSet.of(PropertyContactType.mainOffice, PropertyContactType.pointOfSale).contains(contact.type().getValue())) {
                contacts.add(new ContactMarshaller().unmarshal(contact));
            }
        }
        buildingIO.contacts.addAll(contacts);

        return buildingIO;
    }

    @Override
    public Building marshal(BuildingIO buildingRs) {
        return null;
    }

}
