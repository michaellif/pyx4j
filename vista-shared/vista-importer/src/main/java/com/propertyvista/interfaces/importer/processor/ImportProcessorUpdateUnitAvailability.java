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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class ImportProcessorUpdateUnitAvailability implements ImportProcessor {

    private static final I18n i18n = I18n.get(ImportProcessorUpdateUnitAvailability.class);

    private final static Logger log = LoggerFactory.getLogger(ImportProcessorUpdateUnitAvailability.class);

    @Override
    public boolean validate(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo,
            UploadResponse<ImportUploadResponseDTO> response) {
        status.setProgressMaximum(data.buildings().size() * 2);
        int count = 0;
        boolean result = true;
        for (BuildingIO buildingIO : data.buildings()) {

            if (buildingIO.propertyCode().isNull()) {
                buildingIO._import().invalid().setValue(true);
                buildingIO
                        ._import()
                        .message()
                        .setValue(
                                i18n.tr("Building Property Code is null at row {0} on sheet {1}.", buildingIO._import().row().getStringView(), buildingIO
                                        ._import().sheet().getStringView()));
                result = false;
            }

            // Check if building exists
            {
                EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
                buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
                List<Building> buildings = Persistence.service().query(buildingCriteria);
                if (buildings.size() == 0) {
                    buildingIO._import().message().setValue(i18n.tr("Building {0} not found in the database.", buildingIO.propertyCode().getStringView()));
                    result = false;
                }
            }

            for (AptUnitIO aptUnitIO : buildingIO.units()) {
                if (aptUnitIO.number().isNull()) {
                    aptUnitIO._import().invalid().setValue(true);
                    aptUnitIO
                            ._import()
                            .message()
                            .setValue(
                                    i18n.tr("Unit Number is null at row {0} on sheet {1}.", aptUnitIO._import().row().getStringView(), aptUnitIO._import()
                                            .sheet().getStringView()));
                    result = false;
                }

                // Check if unit exists
                {
                    EntityQueryCriteria<AptUnit> unitCriteria = EntityQueryCriteria.create(AptUnit.class);
                    unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().info().number(), aptUnitIO.number().getValue()));
                    List<AptUnit> units = Persistence.service().query(unitCriteria);
                    if (units.size() == 0) {
                        aptUnitIO._import().message().setValue(i18n.tr("Unit {0} not found in the database.", aptUnitIO.number().getStringView()));
                        result = false;
                    }
                }

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
        int count = 0;
        try {
            for (BuildingIO building : data.buildings()) {
                if (building.type().isNull()) {
                    building.type().setValue(BuildingInfo.Type.residential);
                }
                log.debug("processing building {} {}", count + "/" + data.buildings().size(), building.getStringView());
                counters.add(saveInDB(building));
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

    private ImportCounters saveInDB(BuildingIO buildingIO) {

        ImportCounters counters = new ImportCounters();
        counters.buildings++;
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() == 1) {
                building = buildings.get(0);
            } else {
                throw new UserRuntimeException(i18n.tr("Building {0} not found in the database.", buildingIO.propertyCode()));
            }
        }

        //Units
        {
            List<AptUnit> items = new Vector<AptUnit>();
            for (AptUnitIO aptUnitIO : buildingIO.units()) {
                aptUnitIO.number().setValue(AptUnitConverter.trimUnitNumber(aptUnitIO.number().getValue()));
                {
                    EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                    criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                    List<AptUnit> units = Persistence.service().query(criteria);
                    if (units.size() == 1) {
                        AptUnit unit = (units.get(0));
                        unit.financial()._marketRent().setValue(aptUnitIO.marketRent().getValue());
                        unit._availableForRent().setValue(aptUnitIO.availableForRent().getValue());
                        items.add(unit);
                    } else {
                        throw new UserRuntimeException(i18n.tr("Unit ''{0}'' not found.", aptUnitIO.number()));
                    }
                }
            }
            Persistence.service().merge(items);
            counters.units += items.size();

        }

        return counters;
    }
}
