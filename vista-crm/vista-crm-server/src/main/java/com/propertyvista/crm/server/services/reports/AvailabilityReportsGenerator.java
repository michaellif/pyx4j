/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.site.server.services.reports.ReportGenerator;

import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportsGenerator implements ReportGenerator {

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        AvailabilityReportMetadata meta = (AvailabilityReportMetadata) reportMetadata;
        if (meta.asOf().isNull()) {
            meta.asOf().setValue(new LogicalDate());
        }

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = EntityQueryCriteria.create(UnitAvailabilityStatus.class);
        criteria.add(PropertyCriterion.le(criteria.proto().statusFrom(), meta.asOf()));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusUntil(), meta.asOf()));
        criteria.add(PropertyCriterion.ne(criteria.proto().vacancyStatus(), null));

        List<UnitAvailabilityStatus> statuses = Persistence.secureQuery(criteria);

        AvailabilityReportDataDTO reportData = new AvailabilityReportDataDTO();
        reportData.unitStatuses = new Vector<UnitAvailabilityStatus>(statuses);
        clearUnrequiredData(reportData.unitStatuses);
        return reportData;
    }

    private void clearUnrequiredData(Vector<UnitAvailabilityStatus> unitsStatusPage) {
        for (UnitAvailabilityStatus status : unitsStatusPage) {
            Building building = EntityFactory.create(Building.class);
            building.id().setValue(status.building().id().getValue());
            building.propertyCode().setValue(status.building().propertyCode().getValue());
            building.externalId().setValue(status.building().externalId().getValue());
            building.info().name().setValue(status.building().info().name().getValue());
            building.info().address().setValue(status.building().info().address().getValue());
            building.propertyManager().name().setValue(status.building().propertyManager().name().getValue());
            building.complex().name().setValue(status.building().complex().name().getValue());
            status.building().set(building);

            AptUnit unit = EntityFactory.create(AptUnit.class);
            unit.id().setValue(status.unit().id().getValue());
            unit.info().number().setValue(status.unit().info().number().getValue());
            status.unit().set(unit);

            Floorplan floorplan = EntityFactory.create(Floorplan.class);
            floorplan.id().setValue(status.floorplan().id().getValue());
            floorplan.name().setValue(status.floorplan().name().getValue());
            floorplan.marketingName().setValue(status.floorplan().marketingName().getValue());
            status.floorplan().set(floorplan);
        }
    }

}
