/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 */
package com.propertyvista.interfaces.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.crm.rpc.dto.ImportBuildingDataParametersDTO;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;

public class ImportBuildingDataProcessor {

    private final static Logger log = LoggerFactory.getLogger(ImportBuildingDataProcessor.class);

    private final ImportBuildingDataParametersDTO uploadInitiationData;

    public ImportBuildingDataProcessor(ImportBuildingDataParametersDTO uploadInitiationData) {
        this.uploadInitiationData = uploadInitiationData;
    }

    private Building retrive(BuildingIO buildingIO) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        if (buildingIO.propertyCode().isNull()) {
            criteria.eq(criteria.proto().externalId(), buildingIO.externalId());
        } else {
            criteria.eq(criteria.proto().propertyCode(), buildingIO.propertyCode());
        }
        return Persistence.secureRetrieve(criteria);
    }

    public void importModel(BuildingIO buildingIO, RunningProcess progress, ExecutionMonitor monitor) {
        ImportProcessorContext context = new ImportProcessorContext(monitor, buildingIO);
        if (uploadInitiationData.buildingId().isNull()) {
            context.building = retrive(buildingIO);
            if (context.building == null) {
                monitor.addErredEvent("Building", "Building " + buildingIO.propertyCode().getStringView() + " not found");
                return;
            }
            log.info("importing building {} {}", context.building.id(), context.building.propertyCode());
        } else {
            context.building = Persistence.secureRetrieve(EntityCriteriaByPK.create(uploadInitiationData.buildingId()));
            if (context.building == null) {
                monitor.addErredEvent("Building", "Building not found");
                return;
            }
            Building renamedBuilding = retrive(buildingIO);
            if (renamedBuilding == null) {
                context.ignoreEntityId = true;
                log.info("importing building {} {} without IDs", context.building.id(), context.building.propertyCode());
            } else if (!context.building.equals(renamedBuilding)) {
                if (!renamedBuilding.suspended().getValue()) {
                    monitor.addErredEvent("Building", "Old Building " + buildingIO.propertyCode().getStringView() + " Should be suspended");
                    return;
                }
                // Building propertyCode and other Id in XML can be ignored if Building id renaming building
                context.ignoreEntityId = true;
                context.renamedBuilding = renamedBuilding;
                log.info("importing building {} to building {} {}", context.building.id(), renamedBuilding.propertyCode(), context.building.propertyCode());
            } else {
                log.info("importing building {} with IDs", context.building.propertyCode());
            }
        }

        ServerSideFactory.create(AuditFacade.class).updated(context.building, "xml data import building " + context.building.propertyCode().getStringView());

        // TODO Update building

        // For now process only units
        for (AptUnitIO aptUnitIO : buildingIO.units()) {
            new ImportUnitDataProcessor().importModel(context, aptUnitIO);
            progress.progress.addAndGet(1);
        }

        monitor.addProcessedEvent("Building", "Building " + context.building.propertyCode().getStringView() + " imported");

    }
}
