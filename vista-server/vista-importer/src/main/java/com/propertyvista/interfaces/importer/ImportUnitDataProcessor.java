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

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.model.AptUnitIO;

public class ImportUnitDataProcessor {

    private final static Logger log = LoggerFactory.getLogger(ImportUnitDataProcessor.class);

    public ImportUnitDataProcessor() {
    }

    private AptUnit retrive(Building building, AptUnitIO aptUnitIO) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building(), building);
        criteria.eq(criteria.proto().info().number(), aptUnitIO.number().getValue());
        return Persistence.service().retrieve(criteria);
    }

    public void importModel(ImportProcessorContext context, AptUnitIO aptUnitIO) {
        AptUnit unit = retrive(context.building, aptUnitIO);
        if (unit == null) {
            if (!aptUnitIO.lease().isNull()) {
                context.monitor.addErredEvent("Unit", "Unit " + aptUnitIO.number().getStringView() + " not found");
            } else {
                context.monitor.addFailedEvent("Unit", "Unit " + aptUnitIO.number().getStringView() + " not found");
            }
            return;
        }
        log.debug("importing unit {} {}", unit.id(), unit.info().number());

        if (!aptUnitIO.lease().isNull()) {
            new ImportLeaseDataProcessor().importModel(context, unit, aptUnitIO.lease());
        }

        context.monitor.addProcessedEvent("Unit", aptUnitIO.number().getStringView());
    }

}
