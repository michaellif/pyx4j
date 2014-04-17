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

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.model.AptUnitIO;

public class ImportUnitDataProcessor {

    private AptUnit retrive(Building building, AptUnitIO aptUnitIO) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building(), building);
        criteria.eq(criteria.proto().info().number(), aptUnitIO.number().getValue());
        return Persistence.service().retrieve(criteria);
    }

    public void importModel(Building building, AptUnitIO aptUnitIO, ExecutionMonitor monitor) {
        AptUnit unit = retrive(building, aptUnitIO);
        if (unit == null) {
            monitor.addErredEvent("Unit", "Unit " + aptUnitIO.number().getStringView() + " not found");
            return;
        }

        if (!aptUnitIO.lease().isNull()) {
            new ImportLeaseDataProcessor().importModel(building, unit, aptUnitIO.lease(), monitor);
        }

        monitor.addProcessedEvent("Unit");
    }

}
