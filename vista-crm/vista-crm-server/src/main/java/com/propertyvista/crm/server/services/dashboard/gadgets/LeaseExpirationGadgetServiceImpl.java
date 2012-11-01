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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.CommonQueries;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetServiceImpl implements LeaseExpirationGadgetService {

    @Override
    public void countData(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        LeaseExpirationGadgetDataDTO gadgetData = EntityFactory.create(LeaseExpirationGadgetDataDTO.class);

        count(gadgetData.numOfLeasesEndingThisMonth(), buildingsFilter);
        count(gadgetData.numOfLeasesEndingNextMonth(), buildingsFilter);
        count(gadgetData.numOfLeasesEnding60to90Days(), buildingsFilter);
        count(gadgetData.numOfLeasesEndingOver90Days(), buildingsFilter);

        if (TODO_LEASES_ON_MONTH_TO_MONTH) {
            count(gadgetData.numOfLeasesOnMonthToMonth(), buildingsFilter);
        }

        gadgetData.totalUnits().setValue(CommonQueries.numOfUnits(buildingsFilter));
        gadgetData.occupiedUnits().setValue(numOfOccupiedUnits(buildingsFilter));

        callback.onSuccess(gadgetData);
    }

    @Override
    public void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter, String path) {
        callback.onSuccess(fillLeaseFilterCriteria(EntityListCriteria.create(LeaseDTO.class), buildingsFilter,
                EntityFactory.getEntityPrototype(LeaseExpirationGadgetDataDTO.class).getMember(new Path(path))));
    }

    @Override
    public void makeUnitFilterCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, Vector<Building> buildingsFilter, String unitsFilter) {
        callback.onSuccess(fillOccupiedUnitsCriteria(EntityListCriteria.create(AptUnitDTO.class), buildingsFilter));
    }

    private void count(IPrimitive<Integer> counter, Vector<Building> buildings) {
        counter.setValue(Persistence.service().count(fillLeaseFilterCriteria(EntityQueryCriteria.create(Lease.class), buildings, counter)));
    }

    private <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria fillLeaseFilterCriteria(Criteria leaseCriteria, Vector<Building> buildings,
            IObject<?> leaseFilterPreset) {
        LeaseExpirationGadgetDataDTO proto = EntityFactory.getEntityPrototype(LeaseExpirationGadgetDataDTO.class);
        IObject<?> leaseFilter = proto.getMember(leaseFilterPreset.getPath());

        LogicalDate leaseToLowerBound = null;
        LogicalDate leaseToUpperBound = null;
        LogicalDate today = Util.dayOfCurrentTransaction();

        if (proto.numOfLeasesEndingThisMonth() == leaseFilter) {
            leaseToLowerBound = today;
            leaseToUpperBound = Util.endOfMonth(today);
        } else if (proto.numOfLeasesEndingNextMonth() == leaseFilter) {
            leaseToLowerBound = Util.beginningOfNextMonth(today);
            leaseToUpperBound = Util.endOfMonth(leaseToLowerBound);
        } else if (proto.numOfLeasesEnding60to90Days() == leaseFilter) {
            leaseToLowerBound = Util.addDays(today, 60);
            leaseToUpperBound = Util.addDays(today, 90);
        } else if (proto.numOfLeasesEndingOver90Days() == leaseFilter) {
            leaseToLowerBound = Util.addDays(today, 91);
            leaseToUpperBound = null;
        } else if (proto.numOfLeasesOnMonthToMonth() == leaseFilter) {
            if (!TODO_LEASES_ON_MONTH_TO_MONTH) {
                throw new RuntimeException("on month to month has not yet been implemented'" + leaseFilter.getPath().toString() + "'");
            }
        } else {
            throw new RuntimeException("it's unknown to to iterpret the lease filter '" + leaseFilter.getPath().toString() + "'");
        }

        if (buildings != null && !buildings.isEmpty()) {
            leaseCriteria.add(PropertyCriterion.in(leaseCriteria.proto().unit().building(), buildings));
        }
        if (leaseToLowerBound != null) {
            leaseCriteria.add(PropertyCriterion.ge(leaseCriteria.proto().leaseTo(), leaseToLowerBound));
        }
        if (leaseToUpperBound != null) {
            leaseCriteria.add(PropertyCriterion.le(leaseCriteria.proto().leaseTo(), leaseToUpperBound));
        }
        return leaseCriteria;
    }

    private int numOfOccupiedUnits(Vector<Building> buildings) {
        return Persistence.service().count(fillOccupiedUnitsCriteria(EntityQueryCriteria.create(AptUnit.class), buildings));
    }

    private <Criteria extends EntityQueryCriteria<? extends AptUnit>> Criteria fillOccupiedUnitsCriteria(Criteria criteria, Vector<Building> buildings) {
        LogicalDate when = new LogicalDate(Persistence.service().getTransactionSystemTime());

        if (buildings != null && !buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }

        criteria.add(PropertyCriterion.le(criteria.proto().unitOccupancySegments().$().dateFrom(), when));
        criteria.add(PropertyCriterion.ge(criteria.proto().unitOccupancySegments().$().dateTo(), when));

        criteria.add(PropertyCriterion.eq(criteria.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.occupied));

        return criteria;
    }

}
