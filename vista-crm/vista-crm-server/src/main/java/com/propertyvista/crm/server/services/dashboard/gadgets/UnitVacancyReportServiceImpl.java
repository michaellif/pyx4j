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

import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.RentReady;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportEvent;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;

public class UnitVacancyReportServiceImpl implements UnitVacancyReportService {

    @Override
    public void list(AsyncCallback<EntitySearchResult<UnitVacancyReport>> callback, EntityListCriteria<UnitVacancyReport> criteria) {

        EntitySearchResult<UnitVacancyReport> unitResult = EntityLister.secureQuery(criteria);
        long now = (new Date()).getTime();

        for (UnitVacancyReport unit : unitResult.getData()) {
            calculateRentDelta(unit);
            if (isRevenueLost(unit)) {
                calclulateDaysVacantAndRevenueLost(unit, now);
            }
        }

        callback.onSuccess(unitResult);
    }

    @Override
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, EntityQueryCriteria<UnitVacancyReport> criteria) {
        // TODO: think about using another type of query that returns something better than just a list
        List<UnitVacancyReport> units = Persistence.service().query(criteria);

        UnitVacancyReportSummaryDTO summary = EntityFactory.create(UnitVacancyReportSummaryDTO.class);

        long now = (new Date()).getTime();

        int total = units.size(); // TODO: I hope that units is an ArrayList otherwise it might be better to count this in the following loop

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        double netExposure = 0.0;

        for (UnitVacancyReport unit : units) {
            VacancyStatus vacancyStatus = unit.vacancyStatus().getValue();

            // check that we can get vacancy status, and don't waste the cpu cycles if we can't            
            if (vacancyStatus == null)
                continue;

            if (VacancyStatus.Vacant.equals(vacancyStatus)) {
                ++vacant;
                if (isRevenueLost(unit)) {
                    calclulateDaysVacantAndRevenueLost(unit, now);
                    netExposure += unit.revenueLost().getValue();
                }
                if (RentedStatus.Rented.equals(unit.rentedStatus().getValue())) {
                    ++vacantRented;
                }
            } else if (VacancyStatus.Notice.equals(vacancyStatus)) {
                ++notice;
                if (RentedStatus.Rented.equals(unit.rentedStatus().getValue())) {
                    ++noticeRented;
                }
            }
        }

        summary.total().setValue(total);

        summary.vacancyAbsolute().setValue(vacant);
        summary.vacancyRelative().setValue(vacant / ((double) total) * 100);
        summary.vacantRented().setValue(vacantRented);

        occupied = total - vacant;
        summary.occupancyAbsolute().setValue(occupied);
        summary.occupancyRelative().setValue(occupied / ((double) total) * 100);

        summary.noticeAbsolute().setValue(notice);
        summary.noticeRelative().setValue(notice / ((double) total) * 100);
        summary.noticeRented().setValue(noticeRented);

        summary.netExposure().setValue(netExposure);
        callback.onSuccess(summary);
    }

    /**
     * Set unit state as it should be on specified date deducting it from the event table (creepy demo mode implementation)
     * 
     * @param unit
     * @param when
     */
    private static void updateState(UnitVacancyReport unit, LogicalDate when) {
        String unitNumber = unit.unit().getValue();
        String propertyCode = unit.propertyCode().getValue();

        // I miss SQL :`(
        // TODO:  this procedure is real waste of CPU cycles!!! think about better implementation!
        EntityQueryCriteria<UnitVacancyReportEvent> criteria = new EntityQueryCriteria<UnitVacancyReportEvent>(UnitVacancyReportEvent.class);

        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.LESS_THAN_OR_EQUAL, when));
        criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.EQUAL, propertyCode));
        criteria.add(new PropertyCriterion(criteria.proto().unit(), Restriction.EQUAL, unitNumber));
        criteria.sort(new Sort(criteria.proto().eventDate().getPath().toString(), false));

        List<UnitVacancyReportEvent> events = Persistence.service().query(criteria);

        // TODO: not sure how to treat scoping events:
        // TODO: is 'reno in progress' automatically renders "rented" as 'off market'?

        // I have trouble thinking about events in the reverse way, hence i'm getting them back into the normal order
        // this algorithm assumes that "rented" events can come only after "notice";
        // TODO: consider using priority queue for ordering events that might happen on the same day
        Stack<UnitVacancyReportEvent> eventStack = new Stack<UnitVacancyReportEvent>();

        for (UnitVacancyReportEvent event : events) {
            String eventType = event.eventType().getValue();
            // TODO: remove this check once the table is full;
            if (eventType == null) {
                continue;
            }
            eventStack.push(event);
            if ("notice".equals(eventType)) {
                break;
            }
        }

        while (!eventStack.isEmpty()) {
            UnitVacancyReportEvent event = eventStack.pop();
            String eventType = event.eventType().getValue();
            // TODO: we almost know for sure the stack starts with "notice"... get it out of the loop"
            // TODO: if not they should be added (fake events to start the timeline: i.e. brand new unit should have "notice" and then concequent "vacant" attached to it)
            if ("notice".equals(eventType)) {
                unit.moveOutDay().setValue(event.moveOutDate().getValue());
                unit.moveInDay().setValue(null);
                unit.rentedFromDate().setValue(null);
                unit.isScoped().setValue(false);
                unit.rentReady().setValue(null);
                unit.rentedStatus().setValue(RentedStatus.Unrented);

            } else if ("scoped".equals(eventType)) {
                unit.isScoped().setValue(true);
                String strVal = event.rentReady().getValue();
                RentReady rentReady = "rentready".equals(strVal) ? RentReady.RentReady : "need repairs".equals(strVal) ? RentReady.NeedRepairs
                        : "reno in progress".equals(strVal) ? RentReady.RenoInProgress : null;
                unit.rentReady().setValue(rentReady);
                if (rentReady.equals(RentReady.RenoInProgress)) {
                    unit.rentedStatus().setValue(RentedStatus.OffMarket);
                }

            } else if ("vacant".equals(eventType)) {
                unit.moveOutDay().setValue(event.moveOutDate().getValue());

            } else if ("rented".equals(eventType)) {
                if (when.compareTo(event.moveInDate().getValue()) < 0) {
                    unit.moveInDay().setValue(event.moveInDate().getValue());
                    unit.rentedStatus().setValue(RentedStatus.Rented);
                } else {
                    // someone lives in the apartment
                    unit.vacancyStatus().setValue(null);
                    unit.isScoped().setValue(null);
                    unit.rentedStatus().setValue(null);
                    unit.moveOutDay().setValue(null);
                    unit.moveInDay().setValue(null);
                    unit.rentedFromDate().setValue(null);
                }

            }
        } // while
    }

    private static void calculateRentDelta(UnitVacancyReport unit) {
        double unitMarketRent = unit.marketRent().getValue();
        double unitRent = unit.unitRent().getValue();

        double rentDeltaAbsoute = unitRent - unitMarketRent;
        double rentDeltaRelative = rentDeltaAbsoute / unitMarketRent * 100;

        unit.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
        unit.rentDeltaRelative().setValue(rentDeltaRelative);
    }

    private static boolean isRevenueLost(UnitVacancyReport unit) {
        return VacancyStatus.Vacant.equals(unit.vacancyStatus().getValue()) & unit.moveOutDay().getValue() != null;
    }

    private static void calclulateDaysVacantAndRevenueLost(UnitVacancyReport unit, final long endOfTime) {
        long availableFrom = 1 + unit.moveOutDay().getValue().getTime();
        long millisecondsVacant = endOfTime - availableFrom;

        int daysVacant = (int) (millisecondsVacant / (1000 * 60 * 60 * 24)); // some really heavy math :)            
        unit.daysVacant().setValue(daysVacant);

        double unitMarketRent = unit.marketRent().getValue();
        double revenueLost = daysVacant * unitMarketRent / 30.0;
        unit.revenueLost().setValue(revenueLost);
    }

    @Override
    public void create(AsyncCallback<UnitVacancyReport> callback, UnitVacancyReport editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<UnitVacancyReport> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(AsyncCallback<UnitVacancyReport> callback, UnitVacancyReport editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // Not Required
    }

    private enum UnitEventParserState {
        INITIAL, VT_NF_RF_SF, VF_NT_RF_SF, VF_NF_RT_SF;
    }
}
