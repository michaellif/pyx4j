/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.OrganisationContact;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.BuildingAmenityConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanAmenityConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.converter.ParkingConverter;
import com.propertyvista.interfaces.importer.model.AmenityIO;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.interfaces.importer.model.ParkingIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class BuildingImporter {

    private static I18n i18n = I18nFactory.getI18n();

    private final static Logger log = LoggerFactory.getLogger(BuildingImporter.class);

    public List<String> verify(BuildingIO buildingIO, String imagesBaseFolder) {
        List<String> messages = new Vector<String>();
        // Set defaults
        if (buildingIO.type().isNull()) {
            buildingIO.type().setValue(BuildingInfo.Type.residential);
        }

        if (buildingIO.propertyCode().isNull()) {
            messages.add("propertyCode can't be empty");
        }

        // Media
        {
            for (MediaIO iIO : buildingIO.medias()) {
                String m = new MediaConverter(imagesBaseFolder).verify(iIO);
                if (m != null) {
                    messages.add("Building '" + buildingIO.propertyCode().getValue() + "' " + m);
                }
            }
        }

        //    Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                if (floorplanIO.name().isNull()) {
                    messages.add("Floorplan name in building '" + buildingIO.propertyCode().getValue() + "' can't be empty");
                }

                // Media
                {
                    for (MediaIO iIO : floorplanIO.medias()) {
                        String m = new MediaConverter(imagesBaseFolder).verify(iIO);
                        if (m != null) {
                            messages.add("Floorplan '" + floorplanIO.name().getValue() + "' in building '" + buildingIO.propertyCode().getValue() + "' " + m);
                        }
                    }
                }
                //Units
                {
                    List<AptUnit> items = new Vector<AptUnit>();
                    for (AptUnitIO iIO : floorplanIO.units()) {
                        if (iIO.number().isNull()) {
                            messages.add("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '" + buildingIO.propertyCode().getValue()
                                    + "' can't be empty");
                        }
                    }
                }

            }
        }
        return messages;
    }

    public ImportCounters persist(BuildingIO buildingIO, String imagesBaseFolder, boolean ignoreMissingMedia) {

        ImportCounters counters = new ImportCounters();

        // Set defaults
        if (buildingIO.type().isNull()) {
            buildingIO.type().setValue(BuildingInfo.Type.residential);
        }

        if (buildingIO.propertyCode().isNull()) {
            throw new UserRuntimeException("propertyCode can't be empty");
        }

        // Save building
        Building building = new BuildingConverter().createDBO(buildingIO);
        // Save Employee or find existing one
        for (OrganisationContact organisationContact : building.contacts().contacts()) {
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

        if (building.info().address().location().isNull()) {
            SharedGeoLocator.populateGeo(building.info().address());
        }

        // Media
        {
            for (MediaIO iIO : buildingIO.medias()) {
                try {
                    building.media().add(new MediaConverter(imagesBaseFolder, ignoreMissingMedia, ImageTarget.Building).createDBO(iIO));
                } catch (Throwable e) {
                    log.error("Building '" + buildingIO.propertyCode().getValue() + "' media error", e);
                    throw new UserRuntimeException(i18n.tr("Building ''{}'' media error {}", buildingIO.propertyCode().getValue(), e.getMessage()));
                }
            }
            Persistence.service().persist(building.media());
        }

        Persistence.service().persist(building);
        PublicDataUpdater.updateIndexData(building);

        //BuildingAmenity
        {
            List<BuildingAmenity> items = new Vector<BuildingAmenity>();
            for (AmenityIO iIO : buildingIO.amenities()) {
                BuildingAmenity i = new BuildingAmenityConverter().createDBO(iIO);
                i.belongsTo().set(building);
                items.add(i);
            }
            Persistence.service().persist(items);
        }

        //Parking
        {
            List<Parking> items = new Vector<Parking>();
            for (ParkingIO iIO : buildingIO.parkings()) {
                Parking i = new ParkingConverter().createDBO(iIO);
                i.belongsTo().set(building);
                items.add(i);
            }
            Persistence.service().persist(items);
        }

        //Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                Floorplan floorplan = new FloorplanConverter().createDBO(floorplanIO);
                floorplan.building().set(building);

                if (floorplan.name().isNull()) {
                    throw new UserRuntimeException("Floorplan name in  building '" + buildingIO.propertyCode().getValue() + "' can't be empty");
                }

                // Media
                {
                    for (MediaIO iIO : floorplanIO.medias()) {
                        try {
                            floorplan.media().add(new MediaConverter(imagesBaseFolder, ignoreMissingMedia, ImageTarget.Floorplan).createDBO(iIO));
                        } catch (Throwable e) {
                            log.error("Building '" + buildingIO.propertyCode().getValue() + "' floorplan '" + floorplanIO.name().getValue() + "' media error",
                                    e);
                            throw new UserRuntimeException(i18n.tr("Building ''{}'' floorplan ''{}'' media error {}", buildingIO.propertyCode().getValue(),
                                    floorplanIO.name().getValue(), e.getMessage()));
                        }
                    }
                    Persistence.service().persist(floorplan.media());
                }

                Persistence.service().persist(floorplan);
                counters.floorplans += 1;

                //FloorplanAmenity
                {
                    List<FloorplanAmenity> items = new Vector<FloorplanAmenity>();
                    for (AmenityIO iIO : buildingIO.amenities()) {
                        FloorplanAmenity i = new FloorplanAmenityConverter().createDBO(iIO);
                        i.belongsTo().set(floorplan);
                        items.add(i);
                    }
                    Persistence.service().persist(items);
                }

                //Units
                {
                    List<AptUnit> items = new Vector<AptUnit>();
                    for (AptUnitIO iIO : floorplanIO.units()) {
                        if (iIO.number().isNull()) {
                            throw new UserRuntimeException("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '"
                                    + buildingIO.propertyCode().getValue() + "' can't be empty");
                        }

                        AptUnit i = new AptUnitConverter().createDBO(iIO);
                        i.belongsTo().set(building);
                        i.floorplan().set(floorplan);
                        items.add(i);
                    }
                    Persistence.service().persist(items);
                    counters.units += items.size();
                }

            }
        }
        return counters;
    }
}
