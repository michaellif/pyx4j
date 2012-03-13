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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.util.SortingFactory;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoverStats;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability.FilterPreset;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {

    private static SortingFactory<UnitAvailabilityStatus> SORTING_FACTORY = new SortingFactory<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>> callback, Vector<Key> buildings,
            UnitAvailability.FilterPreset filterPreset, LogicalDate on, Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {

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
            if (!unitStatus.vacancyStatus().isNull() & filter.isAcceptable(unitStatus)) {
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
                    return status.rentedStatus().getValue() != RentedStatus.Rented;
                }
            };
        case Notice:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.vacancyStatus().getValue() == Vacancy.Notice;
                }
            };
        case Rented:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.rentedStatus().getValue() == RentedStatus.Rented;
                }
            };
        case Vacant:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.vacancyStatus().getValue() == Vacancy.Vacant;
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
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<Key> buildings, LogicalDate toDate) {
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
        criteria.desc(criteria.proto().statusDate().getPath().toString());

        List<UnitAvailabilityStatus> unitStatuses = Persistence.service().query(criteria);

        UnitVacancyReportSummaryDTO summary = EntityFactory.create(UnitVacancyReportSummaryDTO.class);

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        int netExposure = 0;

        // use hash to mark units, since we need only the last date
        // TODO use proper sorting by unit PK to avoid the usage of hash 
        HashSet<Key> checkedUnits = new HashSet<Key>(unitStatuses.size());

        for (UnitAvailabilityStatus unitStatus : unitStatuses) {
            if (!checkedUnits.add(unitStatus.unit().getPrimaryKey())) {
                continue;
            } else {
                ++total;

                // check that we have vacancy status, and don't waste the cpu cycles if we don't have it
                Vacancy vacancyStatus = unitStatus.vacancyStatus().getValue();
                if (vacancyStatus == null) {
                    continue;

                } else if (Vacancy.Vacant.equals(vacancyStatus)) {
                    ++vacant;
                    if (RentedStatus.Rented.equals(unitStatus.rentedStatus().getValue())) {
                        ++vacantRented;
                    }
                } else if (Vacancy.Notice.equals(vacancyStatus)) {
                    ++notice;
                    if (RentedStatus.Rented.equals(unitStatus.rentedStatus().getValue())) {
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

        LogicalDate tweleveMonthsAgo = new LogicalDate(reportDate.getYear() - 1, reportDate.getMonth(), 1);

        EntityQueryCriteria<UnitTurnoverStats> criteria = EntityQueryCriteria.create(UnitTurnoverStats.class);
        if (!buidlings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().belongsTo(), buidlings));
        }
        criteria.asc(criteria.proto().updatedOn());
        criteria.add(PropertyCriterion.ge(criteria.proto().updatedOn(), tweleveMonthsAgo));
        List<UnitTurnoverStats> summaries = Persistence.secureQuery(criteria);

        Vector<UnitTurnoversPerIntervalDTO> result = new Vector<UnitTurnoversPerIntervalDTO>(12);
        UnitTurnoversPerIntervalDTO accumulation = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
        accumulation.unitsTurnedOverAbs().setValue(0);

        // initialize        
        LogicalDate month = new LogicalDate(tweleveMonthsAgo);
        for (int i = 0; i < 13; ++i) {
            UnitTurnoversPerIntervalDTO turnoversPerInterval = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
            turnoversPerInterval.unitsTurnedOverAbs().setValue(0);
            turnoversPerInterval.intervalSize().setValue(AnalysisResolution.Month);
            turnoversPerInterval.intervalValue().setValue(new LogicalDate(month));
            result.add(turnoversPerInterval);
            int newMonth = (month.getMonth() + 1) % 12;
            month.setMonth(newMonth);
            if (newMonth == 0) {
                month.setYear(month.getYear() + 1);
            }
        }

        // fill with data
        Iterator<UnitTurnoverStats> si = summaries.iterator();
        int i = -1;
        int prevMonth = -1;
        double total = 0.;
        while (si.hasNext()) {
            UnitTurnoverStats summary = si.next();
            if (summary.updatedOn().getValue().getMonth() != prevMonth) {
                prevMonth = summary.updatedOn().getValue().getMonth();
                ++i;
            }
            int turnovers = summary.turnovers().getValue();
            total += turnovers;
            result.get(i).unitsTurnedOverAbs().setValue(result.get(i).unitsTurnedOverAbs().getValue() + turnovers);
        }
        // calculate percentage
        for (UnitTurnoversPerIntervalDTO turnoversPerInterval : result) {
            if (total != 0.) {
                turnoversPerInterval.unitsTurnedOverPct().setValue(turnoversPerInterval.unitsTurnedOverAbs().getValue() * 100 / total);
            } else {
                turnoversPerInterval.unitsTurnedOverPct().setValue(0.);
            }
        }
        callback.onSuccess(result);

    }

}
