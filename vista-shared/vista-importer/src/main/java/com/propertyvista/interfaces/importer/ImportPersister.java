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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.interfaces.importer.converter.BuildingAmenityConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.converter.ParkingConverter;
import com.propertyvista.interfaces.importer.model.AmenityIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.interfaces.importer.model.ParkingIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

class ImportPersister {

    private static I18n i18n = I18n.get(ImportPersister.class);

    private final static Logger log = LoggerFactory.getLogger(ImportPersister.class);

    protected Building createBuilding(BuildingIO buildingIO, MediaConfig mediaConfig) {
        // Save building
        Building building = new BuildingConverter().createDBO(buildingIO);
        // Save Employee or find existing one
        for (OrganizationContact organisationContact : building.contacts().contacts()) {
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
                    building.media().add(new MediaConverter(mediaConfig, ImageTarget.Building).createDBO(iIO));
                } catch (Throwable e) {
                    log.error("Building '" + buildingIO.propertyCode().getValue() + "' media error", e);
                    throw new UserRuntimeException(i18n.tr("Building ''{0}'' media error {1}", buildingIO.propertyCode().getValue(), e.getMessage()));
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

        return building;
    }

    protected Floorplan createFloorplan(FloorplanIO floorplanIO, Building building, MediaConfig mediaConfig) {
        Floorplan floorplan = new FloorplanConverter().createDBO(floorplanIO);
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
                    floorplan.media().add(new MediaConverter(mediaConfig, ImageTarget.Floorplan).createDBO(iIO));
                } catch (Throwable e) {
                    log.error("Building '" + building.propertyCode().getValue() + "' floorplan '" + floorplanIO.name().getValue() + "' media error", e);
                    throw new UserRuntimeException(i18n.tr("Building ''{0}'' floorplan ''{1}'' media error {2}", building.propertyCode().getValue(),
                            floorplanIO.name().getValue(), e.getMessage()));
                }
            }
            Persistence.service().persist(floorplan.media());
        }

        Persistence.service().persist(floorplan);
        return floorplan;
    }
}
