/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO.AvailabilityCategory;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>> callback, Vector<Building> buildingsFilter,
            UnitAvailabilityGadgetMetadata.FilterPreset filterPreset, LogicalDate asOf, Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        EntityListCriteria<UnitAvailabilityStatus> criteria = new EntityListCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(pageSize);
        criteria.setSorts(sortingCriteria);

        criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildingsFilter));
        criteria.add(PropertyCriterion.ne(criteria.proto().vacancyStatus(), null));
        criteria.add(PropertyCriterion.le(criteria.proto().statusFrom(), asOf));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusUntil(), asOf));

        switch (filterPreset) {
        case Vacant:
            criteria.add(PropertyCriterion.in(criteria.proto().vacancyStatus(), Vacancy.Vacant));
            break;
        case Notice:
            criteria.add(PropertyCriterion.in(criteria.proto().vacancyStatus(), Vacancy.Notice));
            break;
        case NetExposure:
            criteria.add(PropertyCriterion.in(criteria.proto().vacancyStatus(), Vacancy.Notice, Vacancy.Vacant));
            criteria.add(PropertyCriterion.ne(criteria.proto().rentedStatus(), RentedStatus.Rented));
            break;
        case VacantAndNotice:
        default:
            // nothing special here
        }
        EntitySearchResult<UnitAvailabilityStatus> result = Persistence.secureQuery(criteria);
        for (UnitAvailabilityStatus avaialabilityStatus : result.getData()) {
            computeTransientFields(avaialabilityStatus, asOf);
            clearUnrequiredData(avaialabilityStatus);
        }
        callback.onSuccess(result);
    }

    @Override
    public void unitStatusSummary(AsyncCallback<Vector<UnitAvailabilityStatusSummaryLineDTO>> callback, Vector<Building> buildingsFilter, LogicalDate asOf) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);
        Vector<UnitAvailabilityStatusSummaryLineDTO> summary = new Vector<UnitAvailabilityStatusSummaryLineDTO>();

        EntityQueryCriteria<UnitAvailabilityStatus> totalCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        int total = Persistence.service().count(totalCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> occupiedCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        occupiedCriteria.add(PropertyCriterion.eq(occupiedCriteria.proto().vacancyStatus(), (Vacancy) null));
        int occupied = Persistence.service().count(occupiedCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> vacantCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        vacantCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Vacant));
        int vacant = Persistence.service().count(vacantCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> vacantRentedCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        vacantRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Vacant));
        vacantRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().rentedStatus(), RentedStatus.Rented));
        int vacantRented = Persistence.service().count(vacantRentedCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> noticeCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        noticeCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Notice));
        int notice = Persistence.service().count(noticeCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> noticeRentedCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        noticeRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Notice));
        noticeRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().rentedStatus(), RentedStatus.Rented));
        int noticeRented = Persistence.service().count(noticeRentedCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> netExposureCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        netExposureCriteria.or(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Vacant),
                PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Notice));
        netExposureCriteria.add(PropertyCriterion.ne(vacantCriteria.proto().rentedStatus(), RentedStatus.Rented));
        int netExposure = Persistence.service().count(netExposureCriteria);

        summary.add(makeSummaryRecord(AvailabilityCategory.total, total, 1d));
        summary.add(makeSummaryRecord(AvailabilityCategory.occupied, occupied, percentile(occupied, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.vacant, vacant, percentile(vacant, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.vacantRented, vacantRented, percentile(vacantRented, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.notice, notice, percentile(notice, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.noticeRented, noticeRented, percentile(noticeRented, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.netExposure, netExposure, percentile(netExposure, total)));

        callback.onSuccess(summary);
    }

    @Override
    public void turnoverAnalysis(AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>> callback, Vector<Building> buildingsFilter, LogicalDate reportDate) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        Vector<UnitTurnoversPerIntervalDTO> result = new Vector<UnitTurnoversPerIntervalDTO>(12);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(reportDate);
        cal.add(Calendar.MONTH, -12);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        LogicalDate tweleveMonthsAgo = new LogicalDate(cal.getTime());

        LogicalDate endOfMonth = Util.endOfMonth(tweleveMonthsAgo);

        UnitTurnoverAnalysisFacade manager = ServerSideFactory.create(UnitTurnoverAnalysisFacade.class);

        Key[] buildingsArray = new Key[buildingsFilter.size()];
        int i = 0;
        for (Building b : buildingsFilter) {
            buildingsArray[i++] = b.getPrimaryKey();
        }

        int totalTurnovers = 0;

        while (endOfMonth.before(reportDate)) {
            UnitTurnoversPerIntervalDTO intervalStats = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
            intervalStats.intervalSize().setValue(AnalysisResolution.Month);
            intervalStats.intervalValue().setValue(new LogicalDate(endOfMonth));

            int turnovers = manager.turnoversSinceBeginningOfTheMonth(endOfMonth, buildingsArray);
            intervalStats.unitsTurnedOverAbs().setValue(turnovers);
            totalTurnovers += turnovers;
            result.add(intervalStats);

            endOfMonth = Util.endOfMonth(Util.beginningOfNextMonth(endOfMonth));
        }
        UnitTurnoversPerIntervalDTO intervalStats = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
        intervalStats.intervalSize().setValue(AnalysisResolution.Month);
        intervalStats.intervalValue().setValue(new LogicalDate(endOfMonth));
        intervalStats.unitsTurnedOverAbs().setValue(manager.turnoversSinceBeginningOfTheMonth(reportDate, buildingsArray));
        result.add(intervalStats);

        if (totalTurnovers != 0) {
            for (UnitTurnoversPerIntervalDTO stats : result) {
                stats.unitsTurnedOverPct().setValue((double) (stats.unitsTurnedOverAbs().getValue()) / totalTurnovers);
            }
        } else {
            for (UnitTurnoversPerIntervalDTO stats : result) {
                stats.unitsTurnedOverPct().setValue(0d);
            }
        }
        callback.onSuccess(result);
    }

    private static UnitAvailabilityStatusSummaryLineDTO makeSummaryRecord(UnitAvailabilityStatusSummaryLineDTO.AvailabilityCategory category, int units,
            double percentile) {
        UnitAvailabilityStatusSummaryLineDTO summaryRecord = EntityFactory.create(UnitAvailabilityStatusSummaryLineDTO.class);
        summaryRecord.category().setValue(category);
        summaryRecord.units().setValue(units);
        summaryRecord.percentage().setValue(percentile);
        return summaryRecord;
    }

    private static double percentile(int part, int total) {
        return total != 0 ? ((double) part) / ((double) total) : 0d;
    }

    private static UnitAvailabilityStatus computeTransientFields(final UnitAvailabilityStatus unitStatus, final LogicalDate now) {
        if (isRevenueLost(unitStatus)) {
            computeDaysVacantAndRevenueLost(unitStatus, now.getTime());
        }
        return unitStatus;
    }

    private static boolean isRevenueLost(final UnitAvailabilityStatus unit) {
        return unit.vacancyStatus().getValue() == Vacancy.Vacant & unit.vacantSince().getValue() != null;
    }

    /**
     * Due to performance considerations this method does the both things together to avoid unnecessary invocation of
     * {@link UnitAvailabilityStatusDTO#daysVacant()}.
     */
    private static void computeDaysVacantAndRevenueLost(final UnitAvailabilityStatus unit, final long reportTime) {

        final long MILLIS_IN_DAY = 1000L * 60L * 60L * 24L;
        final long avaialbleFrom = unit.vacantSince().getValue().getTime();
        final long millisecondsVacant = reportTime - avaialbleFrom;

        int daysVacant = (int) (millisecondsVacant / MILLIS_IN_DAY) + 1;
        unit.daysVacant().setValue(daysVacant);

        BigDecimal marketRent = unit.marketRent().getValue();

        MathContext ctx = MathContext.DECIMAL128;
        if (marketRent != null && marketRent.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal revenueLost = marketRent.multiply(new BigDecimal(daysVacant).divide(new BigDecimal(30), ctx), ctx);
            unit.revenueLost().setValue(revenueLost);
        }
    }

    private static void clearUnrequiredData(UnitAvailabilityStatus status) {

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

    private static EntityQueryCriteria<UnitAvailabilityStatus> makeAvaiabilityStatusCriteria(Vector<Building> buildingsFilter, LogicalDate asOf) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteria.add(PropertyCriterion.in(criteria.proto().building(), buildingsFilter));
        criteria.add(PropertyCriterion.le(criteria.proto().statusFrom(), asOf));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusUntil(), asOf));
        return criteria;
    }

}
