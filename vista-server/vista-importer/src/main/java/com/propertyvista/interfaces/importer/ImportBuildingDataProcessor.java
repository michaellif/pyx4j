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
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;

public class ImportBuildingDataProcessor {

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
        Building building = retrive(buildingIO);
        if (building == null) {
            monitor.addErredEvent("Building", "Building " + buildingIO.propertyCode().getStringView() + " not found");
            return;
        }

        // TODO Update building

        // For now process only units
        for (AptUnitIO aptUnitIO : buildingIO.units()) {
            new ImportUnitDataProcessor().importModel(building, aptUnitIO, monitor);
            progress.progress.addAndGet(1);
        }

        monitor.addProcessedEvent("Building", "Building " + buildingIO.propertyCode().getStringView() + " imported");

    }
}
