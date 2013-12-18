/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.interfaces.importer.converter.BuildingAmenityConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.converter.ParkingConverter;
import com.propertyvista.interfaces.importer.model.BuildingAmenityIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.interfaces.importer.model.ParkingIO;
import com.propertyvista.interfaces.importer.model.PropertyPhoneIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

class ImportPersister {

    private static final I18n i18n = I18n.get(ImportPersister.class);

    private final static Logger log = LoggerFactory.getLogger(ImportPersister.class);

    // Set defaults
    protected void setBuildingDfaults(Building building) {
        if (building.info().type().isNull()) {
            building.info().type().setValue(BuildingInfo.Type.residential);
        }
        if (building.marketing().visibility().isNull()) {
            building.marketing().visibility().setValue(PublicVisibilityType.global);
        }
    }

    // Save building
    protected Building createBuilding(BuildingIO buildingIO, MediaConfig mediaConfig) {
        Building building = new BuildingConverter().createBO(buildingIO);

        // Set defaults
        setBuildingDfaults(building);

        if (!building.complex().isNull()) {
            EntityQueryCriteria<Complex> criteria = EntityQueryCriteria.create(Complex.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().name(), building.complex().name()));
            Complex complex = Persistence.service().retrieve(criteria);
            if (complex != null) {
                building.complex().set(complex);
            } else {
                Persistence.service().persist(building.complex());
            }
        }

        // Save Employee or find existing one
        for (OrganizationContact organisationContact : building.contacts().organizationContacts()) {
            if (!organisationContact.person().isNull()) {
                // Find existing Employee
                EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().name().firstName(), organisationContact.person().name().firstName().getValue()));
                criteria.add(PropertyCriterion.eq(criteria.proto().name().lastName(), organisationContact.person().name().lastName().getValue()));
                Employee employeeExists = Persistence.service().retrieve(criteria);
                if (employeeExists != null) {
                    organisationContact.person().set(employeeExists);
                } else {
                    Persistence.service().persist(organisationContact.person());
                }
            }
        }

        if (building.info().location().isNull()) {
            SharedGeoLocator.populateGeo(building);
        }

        if (!buildingIO.email().isNull()) {
            // Adding email to existing contact
            PropertyContact contact;
            if (!building.contacts().propertyContacts().isEmpty()) {
                contact = building.contacts().propertyContacts().get(0);
            } else {
                contact = EntityFactory.create(PropertyContact.class);
                building.contacts().propertyContacts().add(contact);
            }

            contact.email().setValue(buildingIO.email().getValue());
        }

        for (int i = 0; i < buildingIO.phones().size(); i++) {
            // building phones are added to PropertyContacts, so there must be at least as many PropertyContacts as phones
            PropertyPhoneIO phone = buildingIO.phones().get(i);
            PropertyContact contact = building.contacts().propertyContacts().get(i);
            if (contact.type().isNull() && (phone.designation().isNull() || phone.designation().getValue().equals("office"))) {
                contact.type().setValue(PropertyContactType.mainOffice);
            }
            if (contact.visibility().isNull()) {
                contact.visibility().setValue(PublicVisibilityType.global);
            }
        }

        // Media
        {
            for (MediaIO iIO : buildingIO.medias()) {
                try {
                    building.media().add(new MediaConverter(mediaConfig, ImageTarget.Building).createBO(iIO));
                } catch (Throwable e) {
                    log.error("Building '" + buildingIO.propertyCode().getValue() + "' media error", e);
                    throw new UserRuntimeException(i18n.tr("Building ''{0}'' media error {1}", buildingIO.propertyCode().getValue(), e.getMessage()));
                }
            }
        }

        ServerSideFactory.create(BuildingFacade.class).persist(building);

        //BuildingAmenity
        {
            List<BuildingAmenity> items = new Vector<BuildingAmenity>();
            for (BuildingAmenityIO iIO : buildingIO.amenities()) {
                BuildingAmenity i = new BuildingAmenityConverter().createBO(iIO);
                if (i.type().isNull()) {
                    i.type().setValue(BuildingAmenity.Type.other);
                }
                i.building().set(building);
                i.orderInBuilding().setValue(items.size());
                items.add(i);
            }
            Persistence.service().persist(items);
        }

        //Parking
        {
            List<Parking> items = new Vector<Parking>();
            for (ParkingIO iIO : buildingIO.parkings()) {
                Parking i = new ParkingConverter().createBO(iIO);
                i.building().set(building);
                items.add(i);
            }
            Persistence.service().persist(items);
        }

        return building;
    }

    protected Floorplan createFloorplan(FloorplanIO floorplanIO, Building building, MediaConfig mediaConfig) {
        Floorplan floorplan = new FloorplanConverter().createBO(floorplanIO);
        floorplan.building().set(building);

        if (floorplan.name().isNull()) {
            throw new UserRuntimeException("Floorplan name in  building '" + building.propertyCode().getValue() + "' can't be empty");
        }
        {
            EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            criteria.add(PropertyCriterion.eq(criteria.proto().name(), floorplanIO.name().getValue()));
            List<Floorplan> floorplans = Persistence.service().query(criteria);
            if (floorplans.size() != 0) {
                throw new UserRuntimeException("Floorplan '" + floorplanIO.name().getValue() + "' in  building '" + building.propertyCode().getValue()
                        + "' already exists. Have Floorplan: " + floorplans.get(0).getStringView());
            }
        }

        // Media
        {
            for (MediaIO iIO : floorplanIO.medias()) {
                try {
                    floorplan.media().add(new MediaConverter(mediaConfig, ImageTarget.Floorplan).createBO(iIO));
                } catch (Throwable e) {
                    log.error("Building '" + building.propertyCode().getValue() + "' floorplan '" + floorplanIO.name().getValue() + "' media error", e);
                    throw new UserRuntimeException(i18n.tr("Building ''{0}'' floorplan ''{1}'' media error {2}", building.propertyCode().getValue(),
                            floorplanIO.name().getValue(), e.getMessage()));
                }
            }
        }

        Persistence.service().persist(floorplan);
        return floorplan;
    }

}
