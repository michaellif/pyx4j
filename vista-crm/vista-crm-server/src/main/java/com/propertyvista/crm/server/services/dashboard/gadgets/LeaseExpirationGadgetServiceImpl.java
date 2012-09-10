/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.GregorianCalendar;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta.GadgetView;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetServiceImpl implements LeaseExpirationGadgetService {

    @Deprecated
    private static final boolean UI_MOCKUP = true;

    @Override
    public void leaseExpriation(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildings) {

        LeaseExpirationGadgetDataDTO gadgetData = EntityFactory.create(LeaseExpirationGadgetDataDTO.class);

        if (UI_MOCKUP) {
            gadgetData.numOfLeasesEndingThisMonth().setValue(5);
            gadgetData.numOfLeasesEndingNextMonth().setValue(6);
            gadgetData.numOfLeasesEndingOver90Days().setValue(15);
            gadgetData.numOfLeasesOnMonthToMonth().setValue(55);
            gadgetData.unitsOccupied().setValue(5);
            gadgetData.unitOccupancyPct().setValue(5d);

        } else {
            LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());

            LogicalDate monthsBeginning = beginningOfMonth(today);
            LogicalDate monthsEnd = endOfMonth(today);
            gadgetData.numOfLeasesEndingThisMonth().setValue(Persistence.service().count(leaseCriteria(buildings, monthsBeginning, monthsEnd)));

            LogicalDate nextMonthsBeginning = beginningOfNextMonth(today);
            LogicalDate nextMonthsEnd = endOfMonth(nextMonthsBeginning);
            gadgetData.numOfLeasesEndingNextMonth().setValue(Persistence.service().count(leaseCriteria(buildings, nextMonthsBeginning, nextMonthsEnd)));

            // TODO gadgetData.numOfLeasesEndingOver90Days().setValue(0);
            // TODO gadgetData.numOfLeasesOnMonthToMonth().setValue(0);

            gadgetData.unitsOccupied().setValue(numOfOccupiedUnits(today, buildings));

            int numOfUnits = numOfUnits(buildings);
            if (numOfUnits != 0) {
                gadgetData.unitOccupancyPct().setValue(gadgetData.unitsOccupied().getValue() / (double) numOfUnits);
            }
        }

        callback.onSuccess(gadgetData);
    }

    @Override
    public void listCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter, GadgetView activeView) {
        activeView = activeView != null ? activeView : GadgetView.LEASES_ENDING_THIS_MONTH;

        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
        switch (activeView) {

        case LEASES_ENDING_THIS_MONTH:
            callback.onSuccess(toDTOCriteria(leaseCriteria(buildingsFilter, beginningOfMonth(today), endOfMonth(today))));
            break;

        case LEASES_ENDING_NEXT_MONTH:
            LogicalDate nextMonthsBeginning = beginningOfNextMonth(today);
            LogicalDate nextMonthsEnd = endOfMonth(nextMonthsBeginning);

            callback.onSuccess(toDTOCriteria(leaseCriteria(buildingsFilter, nextMonthsBeginning, nextMonthsEnd)));
            break;

        case LEASES_ENDING_90_DAYS:
            callback.onSuccess(toDTOCriteria(leaseCriteria(buildingsFilter, beginningOfNextMonth(today), null)));
            break;

        case LEASES_ON_MONTH_TO_MONTH:
        case UNIT_OCCUPANCY_NUM:
        case SUMMARY:
        case UNIT_OCCUPANCY_PCT:
        default:
            throw new RuntimeException("wrong state for this operation!");
        }

    }

    private EntityListCriteria<Lease> leaseCriteria(Vector<Building> buildings, LogicalDate leaseToLowerBound, LogicalDate leaseToUpperBound) {

        EntityListCriteria<Lease> criteria = EntityListCriteria.create(Lease.class);
        if (buildings != null && !buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildings));
        }
        if (leaseToLowerBound != null) {
            criteria.add(PropertyCriterion.ge(criteria.proto().leaseTo(), leaseToLowerBound));
        }
        if (leaseToUpperBound != null) {
            criteria.add(PropertyCriterion.le(criteria.proto().leaseFrom(), leaseToUpperBound));
        }
        return criteria;
    }

    private LogicalDate beginningOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate endOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate beginningOfNextMonth(LogicalDate dayInMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(beginningOfMonth(dayInMonth));
        cal.add(GregorianCalendar.MONTH, 1);
        return new LogicalDate(cal.getTime());
    }

    private int numOfOccupiedUnits(LogicalDate when, Vector<Building> buildings) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);

        if (buildings != null && !buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildings));
        }

        criteria.add(PropertyCriterion.le(criteria.proto().dateFrom(), when));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), when));

        criteria.add(PropertyCriterion.eq(criteria.proto().status(), AptUnitOccupancySegment.Status.leased));

        return Persistence.service().count(criteria);
    }

    private int numOfUnits(Vector<Building> buildings) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        return Persistence.service().count(criteria);
    }

    private EntityListCriteria<LeaseDTO> toDTOCriteria(EntityQueryCriteria<Lease> criteria) {
        EntityDtoBinder<LeaseDTO, Lease> binder = new EntityDtoBinder<LeaseDTO, Lease>(LeaseDTO.class, Lease.class) {
            @Override
            protected void bind() {
                bind(dtoProto.leaseId(), dboProto.leaseId());
                bind(dtoProto.type(), dboProto.type());

                bind(dtoProto.unit().building().propertyCode(), dboProto.unit().building().propertyCode());
                bind(dtoProto.unit(), dboProto.unit());

                bind(dtoProto.status(), dboProto.status());
                bind(dtoProto.completion(), dboProto.completion());

                bind(dtoProto.billingAccount().accountNumber(), dboProto.billingAccount().accountNumber());

                bind(dtoProto.leaseFrom(), dboProto.leaseFrom());
                bind(dtoProto.leaseTo(), dboProto.leaseTo());

                bind(dtoProto.expectedMoveIn(), dboProto.expectedMoveIn());
                bind(dtoProto.expectedMoveOut(), dboProto.expectedMoveOut());
                bind(dtoProto.actualMoveIn(), dboProto.actualMoveIn());
                bind(dtoProto.actualMoveOut(), dboProto.actualMoveOut());
                bind(dtoProto.moveOutNotice(), dboProto.moveOutNotice());

                bind(dtoProto.approvalDate(), dboProto.approvalDate());
                bind(dtoProto.creationDate(), dboProto.creationDate());

            }
        };
        EntityDto2DboCriteriaConverter<LeaseDTO, Lease> converter = new EntityDto2DboCriteriaConverter<LeaseDTO, Lease>(LeaseDTO.class, Lease.class,
                EntityDto2DboCriteriaConverter.makeMapper(binder));

        EntityListCriteria<LeaseDTO> criteriaDto = EntityListCriteria.create(LeaseDTO.class);
        criteriaDto.addAll(converter.convertDTOSearchCriteria(criteria.getFilters()));

        return criteriaDto;
    }

}
