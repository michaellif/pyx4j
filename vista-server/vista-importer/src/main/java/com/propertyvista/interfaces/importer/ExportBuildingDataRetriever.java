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

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;

public class ExportBuildingDataRetriever {

    public BuildingIO getModel(Building building, RunningProcess progress) {
        BuildingIO buildingIO = new BuildingConverter().createTO(building);
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.eq(criteria.proto().building(), building);
            progress.progressMaximum.addAndGet(Persistence.service().count(criteria));

            criteria.asc(criteria.proto().info().number());
            for (AptUnit unit : Persistence.service().query(criteria)) {
                AptUnitIO aptUnitIO = new AptUnitConverter().createTO(unit);
                buildingIO.units().add(aptUnitIO);

                aptUnitIO.lease().set(new ExportLeaseDataRetriever().getModel(unit));

                progress.progress.addAndGet(1);
            }
        }
        return buildingIO;
    }

}
