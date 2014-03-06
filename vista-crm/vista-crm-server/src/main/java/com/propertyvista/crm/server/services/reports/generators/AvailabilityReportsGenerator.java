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
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.services.reports.ReportCriteriaBuilder;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Scoping;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportsGenerator implements ReportGenerator {

    private volatile boolean aborted;

    public AvailabilityReportsGenerator() {
        this.aborted = false;
    }

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        AvailabilityReportMetadata meta = (AvailabilityReportMetadata) reportMetadata;
        if (meta.asOf().isNull()) {
            meta.asOf().setValue(new LogicalDate());
        }

        List<UnitAvailabilityStatus> statuses = Persistence.secureQuery(createAvailabilityCriteria(meta));
        clearUnrequiredData(statuses);

        AvailabilityReportDataDTO reportData = new AvailabilityReportDataDTO();
        reportData.unitStatuses = new Vector<UnitAvailabilityStatus>(statuses);
        reportData.asOf = meta.asOf().getValue();

        if (false) {
            fillMockupAvailabilityReportData(reportData);
        }
        return reportData;
    }

    @Override
    public void abort() {
        this.aborted = true;
    }

    @Override
    public ReportProgressStatus getProgressStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    private void clearUnrequiredData(List<UnitAvailabilityStatus> statuses) {
        for (UnitAvailabilityStatus status : statuses) {
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

    private EntityQueryCriteria<UnitAvailabilityStatus> createAvailabilityCriteria(AvailabilityReportMetadata metadata) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = null;
        if (metadata.isInAdvancedMode().getValue(true)) {
            criteria = ReportCriteriaBuilder.build(UnitAvailabilityStatus.class, metadata.availbilityTableCriteria());
        } else {
            criteria = EntityQueryCriteria.create(UnitAvailabilityStatus.class);
            if (!metadata.vacancyStatus().isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().vacancyStatus(), metadata.vacancyStatus()));
            } else {
                criteria.isNull(criteria.proto().vacancyStatus());
            }
            if (!metadata.rentedStatus().isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().rentedStatus(), metadata.rentedStatus()));
            } else {
                criteria.isNull(criteria.proto().rentedStatus());
            }
            if (!metadata.rentReadinessStatus().isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().rentReadinessStatus(), metadata.rentReadinessStatus()));
            } else {
                criteria.isNull(criteria.proto().rentReadinessStatus());
            }

        }

        criteria.le(criteria.proto().statusFrom(), metadata.asOf());
        criteria.ge(criteria.proto().statusUntil(), metadata.asOf());
        criteria.isNotNull(criteria.proto().vacancyStatus());

        return criteria;
    }

    private void fillMockupAvailabilityReportData(AvailabilityReportDataDTO reportData) {
        for (int i = 0; i < 2000; ++i) {

            UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
            status.building().propertyCode().setValue("mockup1234");
            status.building().externalId().setValue("bb");
            status.building().info().name().setValue("bla-bla");
            status.building().info().address().streetName().setValue("haShalom");
            status.building().info().address().streetNumber().setValue("1");
            status.building().info().address().country().name().setValue("Canada");
            status.building().info().address().province().name().setValue("Ontario");
            status.building().info().address().province().code().setValue("ON");
            status.building().info().address().postalCode().setValue("TBD G2G");
            status.unit().info().number().setValue("unit #" + i);
            status.floorplan().name().setValue("fplan");
            status.floorplan().name().setValue("r2d2");
            status.vacancyStatus().setValue(Vacancy.Vacant);
            status.rentedStatus().setValue(RentedStatus.Rented);
            status.scoping().setValue(Scoping.Scoped);
            status.rentReadinessStatus().setValue(RentReadiness.RentReady);
            status.unitRent().setValue(new BigDecimal("1999.99"));
            status.marketRent().setValue(new BigDecimal("2055.00"));
            status.rentDeltaAbsolute().setValue(new BigDecimal("55.00"));
            status.rentDeltaRelative().setValue(new BigDecimal("0.02"));
            status.rentEndDay().setValue(new LogicalDate());
            status.rentedFromDay().setValue(new LogicalDate());
            status.moveInDay().setValue(new LogicalDate());
            reportData.unitStatuses.add(status);
        }
    }
}
