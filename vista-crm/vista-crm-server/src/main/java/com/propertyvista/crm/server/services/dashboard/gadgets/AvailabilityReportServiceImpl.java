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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
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
import com.propertyvista.crm.server.util.SortingFactory;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO.AvailabilityCategory;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMeta.FilterPreset;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {

    private static SortingFactory<UnitAvailabilityStatus> SORTING_FACTORY = new SortingFactory<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>> callback, Vector<Key> buildings,
            UnitAvailabilityGadgetMeta.FilterPreset filterPreset, LogicalDate on, Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {

        EntityListCriteria<UnitAvailabilityStatus> criteria = new EntityListCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);

        ArrayList<UnitAvailabilityStatus> allUnitStatuses = new ArrayList<UnitAvailabilityStatus>();

        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }
        if (on == null) {
            throw new IllegalArgumentException("the report date cannot be null");
        }
        criteria.add(PropertyCriterion.le(criteria.proto().statusDate(), on));

        // use descending order of the status date in order to select the most recent statuses first
        // use unit pk sorting in order to make tacking of already added unit statuses
        criteria.setSorts(Arrays.asList(new Sort(criteria.proto().unit().getPath().toString(), true), new Sort(criteria.proto().statusDate().getPath()
                .toString(), true), new Sort(criteria.proto().id().getPath().toString(), true)));
        List<UnitAvailabilityStatus> unfiltered = Persistence.service().query(criteria);

        Key pervUnitPK = null;
        for (UnitAvailabilityStatus unitStatus : unfiltered) {
            Key thisUnitPK = unitStatus.unit().getPrimaryKey();
            if (!thisUnitPK.equals(pervUnitPK)) {
                allUnitStatuses.add(computeTransientFields(unitStatus, on));
                pervUnitPK = thisUnitPK;
            }
        }

        if (!sortingCriteria.isEmpty()) {
            Collections.sort(allUnitStatuses, SORTING_FACTORY.createDtoComparator(sortingCriteria));
        }

        int currentPage = 0;
        int currentPagePosition = 0;
        int totalRows = 0;
        boolean hasMoreRows = false;
        Vector<UnitAvailabilityStatus> unitsStatusPage = new Vector<UnitAvailabilityStatus>();

        Iterator<UnitAvailabilityStatus> i = allUnitStatuses.iterator();
        StatusFilter filter = filterFor(filterPreset);

        while (i.hasNext()) {
            UnitAvailabilityStatus unitStatus = i.next();
            if (filter.isAcceptable(unitStatus)) {
                ++currentPagePosition;
                ++totalRows;
                if (currentPagePosition > pageSize) {
                    ++currentPage;
                    currentPagePosition = 1;
                }
                if (currentPage < pageNumber) {
                    continue;
                } else if (currentPage == pageNumber) {
                    unitsStatusPage.add(unitStatus);
                } else {
                    hasMoreRows = true;
                    break;
                }
            }
        }
        while (i.hasNext()) {
            if (filter.isAcceptable(i.next())) {
                ++totalRows;
            }
        }

        clearUnrequiredData(unitsStatusPage);

        EntitySearchResult<UnitAvailabilityStatus> result = new EntitySearchResult<UnitAvailabilityStatus>();

        result.setData(unitsStatusPage);
        result.setTotalRows(totalRows);
        result.hasMoreData(hasMoreRows);

        callback.onSuccess(result);
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

    private StatusFilter filterFor(FilterPreset filterPreset) {
        switch (filterPreset) {
        case NetExposure:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return !status.vacancyStatus().isNull() & status.rentedStatus().getValue() != RentedStatus.Rented;
                }
            };
        case Notice:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return !status.vacancyStatus().isNull() & status.vacancyStatus().getValue() == Vacancy.Notice;
                }
            };
        case Rented:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return !status.vacancyStatus().isNull() & status.rentedStatus().getValue() == RentedStatus.Rented;
                }
            };
        case Vacant:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return !status.vacancyStatus().isNull() & status.vacancyStatus().getValue() == Vacancy.Vacant;
                }
            };
        case VacantAndNotice:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.vacancyStatus().getValue() != null;
                }
            };
        default:
            throw new IllegalStateException("unknown filter preset: " + filterPreset);
        }
    }

    @Override
    public void summary(AsyncCallback<UnitAvailabilityReportSummaryDTO> callback, Vector<Key> buildings, LogicalDate toDate) {
        if (buildings == null) {
            callback.onFailure(new Error("the set of buildings was not provided."));
            return;
        }

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }
        if (toDate == null) {
            toDate = new LogicalDate();
        }
        criteria.add(PropertyCriterion.le(criteria.proto().statusDate(), toDate));
        // use descending order of the status date in order to select the most recent statuses first
        criteria.setSorts(Arrays.asList(new Sort(criteria.proto().unit().getPath().toString(), true), new Sort(criteria.proto().statusDate().getPath()
                .toString(), true), new Sort(criteria.proto().id().getPath().toString(), true)));

        List<UnitAvailabilityStatus> unitStatuses = Persistence.service().query(criteria);

        UnitAvailabilityReportSummaryDTO summary = EntityFactory.create(UnitAvailabilityReportSummaryDTO.class);

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        int netExposure = 0;

        Key pervUnitPK = null;
        for (UnitAvailabilityStatus unitStatus : unitStatuses) {
            Key thisUnitPK = unitStatus.unit().getPrimaryKey();
            if (!thisUnitPK.equals(pervUnitPK)) {
                pervUnitPK = thisUnitPK;
                ++total;

                // check that we have vacancy status, and don't waste the cpu cycles if we don't have it
                Vacancy vacancyStatus = unitStatus.vacancyStatus().getValue();
                if (vacancyStatus == null) {
                    continue;

                } else if (vacancyStatus == Vacancy.Vacant) {
                    ++vacant;
                    if (unitStatus.rentedStatus().getValue() == RentedStatus.Rented) {
                        ++vacantRented;
                    }
                } else if (Vacancy.Notice.equals(vacancyStatus)) {
                    ++notice;
                    if (unitStatus.rentedStatus().getValue() == RentedStatus.Rented) {
                        ++noticeRented;
                    }
                }
            }
        }

        summary.total().setValue(total);

        summary.vacancyAbsolute().setValue(vacant);
        summary.vacancyRelative().setValue(vacant / ((double) total) * 100d);
        summary.vacantRented().setValue(vacantRented);

        occupied = total - vacant;
        summary.occupancyAbsolute().setValue(occupied);
        summary.occupancyRelative().setValue(occupied / ((double) total) * 100d);

        summary.noticeAbsolute().setValue(notice);
        summary.noticeRelative().setValue(notice / ((double) total) * 100d);
        summary.noticeRented().setValue(noticeRented);

        netExposure = vacant + notice - (vacantRented + noticeRented);
        summary.netExposureAbsolute().setValue(netExposure);
        summary.netExposureRelative().setValue(netExposure / ((double) total) * 100d);
        callback.onSuccess(summary);
    }

    @Override
    public void unitStatusSummary(AsyncCallback<Vector<UnitAvailabilityStatusSummaryLineDTO>> callback, Vector<Building> buildings, LogicalDate asOf) {
        Vector<UnitAvailabilityStatusSummaryLineDTO> summary = new Vector<UnitAvailabilityStatusSummaryLineDTO>();

        if (buildings == null) {
            callback.onFailure(new Error("the set of buildings was not provided."));
            return;
        }

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }
        criteria.add(PropertyCriterion.le(criteria.proto().statusDate(), asOf));
        // use descending order of the status date in order to select the most recent statuses first
        criteria.setSorts(Arrays.asList(new Sort(criteria.proto().unit().getPath().toString(), true), new Sort(criteria.proto().statusDate().getPath()
                .toString(), true), new Sort(criteria.proto().id().getPath().toString(), true)));

        List<UnitAvailabilityStatus> unitStatuses = Persistence.service().query(criteria);

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        int netExposure = 0;

        Key pervUnitPK = null;
        for (UnitAvailabilityStatus unitStatus : unitStatuses) {
            Key thisUnitPK = unitStatus.unit().getPrimaryKey();
            if (!thisUnitPK.equals(pervUnitPK)) {
                pervUnitPK = thisUnitPK;
                ++total;

                // check that we have vacancy status, and don't waste the cpu cycles if we don't have it
                Vacancy vacancyStatus = unitStatus.vacancyStatus().getValue();
                if (vacancyStatus == null) {
                    continue;

                } else if (vacancyStatus == Vacancy.Vacant) {
                    ++vacant;
                    if (unitStatus.rentedStatus().getValue() == RentedStatus.Rented) {
                        ++vacantRented;
                    }
                } else if (Vacancy.Notice.equals(vacancyStatus)) {
                    ++notice;
                    if (unitStatus.rentedStatus().getValue() == RentedStatus.Rented) {
                        ++noticeRented;
                    }
                }
            }
        }
        occupied = total - vacant;
        netExposure = vacant + notice - (vacantRented + noticeRented);

        summary.add(makeSummaryRecord(AvailabilityCategory.total, total, 1d));
        summary.add(makeSummaryRecord(AvailabilityCategory.occupied, occupied, percentile(occupied, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.vacant, vacant, percentile(vacant, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.vacantRented, vacantRented, percentile(vacantRented, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.notice, notice, percentile(notice, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.noticeRented, noticeRented, percentile(noticeRented, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.netExposure, netExposure, percentile(netExposure, total)));

        callback.onSuccess(summary);
    }

    private static UnitAvailabilityStatusSummaryLineDTO makeSummaryRecord(UnitAvailabilityStatusSummaryLineDTO.AvailabilityCategory category, int units,
            double percentile) {
        UnitAvailabilityStatusSummaryLineDTO summaryRecord = EntityFactory.create(UnitAvailabilityStatusSummaryLineDTO.class);
        summaryRecord.category().setValue(category);
        summaryRecord.units().setValue(units);
        summaryRecord.percentile().setValue(percentile);
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

        MathContext ctx = new MathContext(2, RoundingMode.UP);
        if (marketRent != null && marketRent.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal revenueLost = marketRent.multiply(new BigDecimal(daysVacant).divide(new BigDecimal(30), ctx), ctx);
            unit.revenueLost().setValue(revenueLost);
        }
    }

    private static interface StatusFilter {

        boolean isAcceptable(UnitAvailabilityStatus status);

    }

    @Override
    public void turnoverAnalysis(AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>> callback, Vector<Key> buidlings, LogicalDate reportDate) {
        if (buidlings.isEmpty()) {
            List<Building> bb = Persistence.secureQuery(EntityQueryCriteria.create(Building.class));
            for (Building building : bb) {
                buidlings.add(building.getPrimaryKey());
            }
        }
        Vector<UnitTurnoversPerIntervalDTO> result = new Vector<UnitTurnoversPerIntervalDTO>(12);
        if (buidlings.isEmpty()) {
            // TODO maybe error??
            callback.onSuccess(result);
            return;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(reportDate);
        cal.add(Calendar.MONTH, -12);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        LogicalDate tweleveMonthsAgo = new LogicalDate(cal.getTime());
        LogicalDate endOfTheMonth = thisMonthEnd(tweleveMonthsAgo);
        UnitTurnoverAnalysisFacade manager = ServerSideFactory.create(UnitTurnoverAnalysisFacade.class);

        Key[] buildingsArray = buidlings.toArray(new Key[buidlings.size()]);
        int totalTurnovers = 0;

        while (endOfTheMonth.before(reportDate)) {
            UnitTurnoversPerIntervalDTO intervalStats = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
            intervalStats.intervalSize().setValue(AnalysisResolution.Month);
            intervalStats.intervalValue().setValue(new LogicalDate(endOfTheMonth));
            int turnovers = manager.turnoversSinceBeginningOfTheMonth(endOfTheMonth, buildingsArray);
            intervalStats.unitsTurnedOverAbs().setValue(turnovers);
            totalTurnovers += turnovers;
            result.add(intervalStats);
            endOfTheMonth = nextMonthEnd(endOfTheMonth);
        }
        UnitTurnoversPerIntervalDTO intervalStats = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
        intervalStats.intervalSize().setValue(AnalysisResolution.Month);
        intervalStats.intervalValue().setValue(new LogicalDate(endOfTheMonth));
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

    private LogicalDate thisMonthEnd(LogicalDate date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate nextMonthEnd(LogicalDate endOfTheMonth) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(endOfTheMonth);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

}
