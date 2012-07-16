/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.processor;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class ImportProcessorFlatFloorplanAndUnits implements ImportProcessor {

    private static final I18n i18n = I18n.get(ImportProcessorFlatFloorplanAndUnits.class);

    private final static Logger log = LoggerFactory.getLogger(ImportProcessorFlatFloorplanAndUnits.class);

    @Override
    public boolean validate(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo,
            UploadResponse<ImportUploadResponseDTO> response) {
        status.setProgressMaximum(data.buildings().size() * 2);
        int count = 0;
        boolean result = true;
        for (BuildingIO buildingIO : data.buildings()) {
            if (buildingIO.type().isNull()) {
                buildingIO.type().setValue(BuildingInfo.Type.residential);
            }

            if (buildingIO.propertyCode().isNull()) {
                buildingIO._import().invalid().setValue(true);
                buildingIO._import().message().setValue(i18n.tr("Building Property Code is null."));
                result = false;
            }
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                if (floorplanIO.name().isNull()) {
                    floorplanIO._import().invalid().setValue(true);
                    floorplanIO._import().message().setValue(i18n.tr("Floorplan Name is null."));
                    result = false;
                }
                for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                    if (aptUnitIO.number().isNull()) {
                        aptUnitIO._import().invalid().setValue(true);
                        aptUnitIO._import().message().setValue(i18n.tr("Unit Number is null."));
                        result = false;
                    }
                }
            }

            // Check if building is already created
            EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(buildingCriteria);
            if (buildings.size() == 0) {
                buildingIO._import().message().setValue(i18n.tr("Building ''{0}'' is not found in the database.", buildingIO.propertyCode().getValue()));
                result = false;
            }

            count++;
            status.setProgress(count);
            if (status.isCanceled()) {
                break;
            }
        }
        status.setProgress(0);
        return result;
    }

    @Override
    public void persist(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo,
            UploadResponse<ImportUploadResponseDTO> response) {
        SharedGeoLocator.setMode(Mode.updateCache);
        status.setProgressMaximum(data.buildings().size() * 2);
        ImportCounters counters = new ImportCounters();
        MediaConfig mediaConfig = MediaConfig.create(uploadRequestInfo);
        int count = 0;
        try {
            for (BuildingIO building : data.buildings()) {
                log.debug("processing building {} {}", count + "/" + data.buildings().size(), building.getStringView());
                counters.add(persist(building, mediaConfig));
                count++;
                status.setProgress(data.buildings().size() + count);
                log.info("building {} updated", building.getStringView());
                if (status.isCanceled()) {
                    break;
                }
            }
            response.message = SimpleMessageFormat
                    .format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", count, counters.floorplans, counters.units);
        } finally {
            SharedGeoLocator.save();
        }
    }

    private ImportCounters persist(BuildingIO buildingIO, MediaConfig mediaConfig) {

        ImportCounters counters = new ImportCounters();
        counters.buildings++;
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            building = buildings.get(0);
        }

        //Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                Floorplan floorplan = EntityFactory.create(Floorplan.class);
                {
                    EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                    criteria.add(PropertyCriterion.eq(criteria.proto().name(), floorplanIO.name().getValue()));
                    List<Floorplan> floorplans = Persistence.service().query(criteria);
                    if (floorplans.size() == 1) {
                        floorplan = (floorplans.get(0));
                        Persistence.service().persist(floorplan);
                    }

                }

                counters.floorplans += 1;

                //Units
                {
                    List<AptUnit> items = new Vector<AptUnit>();
                    for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                        if (aptUnitIO.number().isNull()) {
                            throw new UserRuntimeException("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '"
                                    + buildingIO.propertyCode().getValue() + "' can't be empty");
                        }
                        aptUnitIO.number().setValue(AptUnitConverter.trimUnitNumber(aptUnitIO.number().getValue()));
                        {
                            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                            criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                            List<AptUnit> units = Persistence.service().query(criteria);
                            if (units.size() == 1) {
                                continue;
                            }
                        }

                        AptUnit i = new AptUnitConverter().createDBO(aptUnitIO);
                        i.building().set(building);
                        i.floorplan().set(floorplan);
                        i.info()._bathrooms().set(floorplan.bathrooms());
                        i.info()._bedrooms().set(floorplan.bedrooms());
                        i.info().floor().set(aptUnitIO.floor());
                        items.add(i);

                    }

                    Persistence.service().merge(items);
                    counters.units += items.size();

                }
            }
        }

        return counters;
    }

}