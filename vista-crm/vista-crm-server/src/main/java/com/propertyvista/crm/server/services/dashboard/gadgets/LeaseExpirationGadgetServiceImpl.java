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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseExpirationGadgetServiceImpl implements LeaseExpirationGadgetService {

    @Override
    public void leaseExpriation(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildings) {

        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());

        LeaseExpirationGadgetDataDTO gadgetData = EntityFactory.create(LeaseExpirationGadgetDataDTO.class);

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

        callback.onSuccess(gadgetData);
    }

    private EntityQueryCriteria<Lease> leaseCriteria(Vector<Building> buildings, LogicalDate leaseToLowerBound, LogicalDate leaseToUpperBound) {

        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
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
}
