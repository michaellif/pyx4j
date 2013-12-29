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
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.CommonQueries;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetServiceImpl implements LeaseExpirationGadgetService {

    @Override
    public void countData(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        LeaseExpirationGadgetDataDTO gadgetData = EntityFactory.create(LeaseExpirationGadgetDataDTO.class);

        count(gadgetData.numOfLeasesOnMonthToMonth(), buildingsFilter);
        count(gadgetData.numOfLeasesEndingThisMonth(), buildingsFilter);
        count(gadgetData.numOfLeasesEndingNextMonth(), buildingsFilter);
        count(gadgetData.numOfLeasesEnding60to90Days(), buildingsFilter);
        count(gadgetData.numOfLeasesEndingOver90Days(), buildingsFilter);

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
        EntityQueryCriteria<Lease> criteria = fillLeaseFilterCriteria(EntityQueryCriteria.create(Lease.class), buildings, counter);
        Persistence.applyDatasetAccessRule(criteria);
        counter.setValue(Persistence.service().count(criteria));
    }

    private <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria fillLeaseFilterCriteria(Criteria leaseCriteria, Vector<Building> buildings,
            IObject<?> leaseFilterPreset) {
        LeaseExpirationGadgetDataDTO proto = EntityFactory.getEntityPrototype(LeaseExpirationGadgetDataDTO.class);
        IObject<?> leaseFilter = proto.getMember(leaseFilterPreset.getPath());

        if (buildings != null && !buildings.isEmpty()) {
            leaseCriteria.add(PropertyCriterion.in(leaseCriteria.proto().unit().building(), buildings));
        }
        LogicalDate today = Util.dayOfCurrentTransaction();
        if (proto.numOfLeasesEndingThisMonth() == leaseFilter) {
            leaseCriteria.add(PropertyCriterion.ge(leaseCriteria.proto().leaseTo(), today));
            leaseCriteria.add(PropertyCriterion.le(leaseCriteria.proto().leaseTo(), Util.endOfMonth(today)));
        } else if (proto.numOfLeasesEndingNextMonth() == leaseFilter) {
            leaseCriteria.add(PropertyCriterion.ge(leaseCriteria.proto().leaseTo(), Util.beginningOfNextMonth(today)));
            leaseCriteria.add(PropertyCriterion.le(leaseCriteria.proto().leaseTo(), Util.endOfMonth(Util.beginningOfNextMonth(today))));
        } else if (proto.numOfLeasesEnding60to90Days() == leaseFilter) {
            leaseCriteria.add(PropertyCriterion.ge(leaseCriteria.proto().leaseTo(), Util.addDays(today, 60)));
            leaseCriteria.add(PropertyCriterion.le(leaseCriteria.proto().leaseTo(), Util.addDays(today, 90)));
        } else if (proto.numOfLeasesEndingOver90Days() == leaseFilter) {
            leaseCriteria.add(PropertyCriterion.ge(leaseCriteria.proto().leaseTo(), Util.addDays(today, 91)));
        } else if (proto.numOfLeasesOnMonthToMonth() == leaseFilter) {
            leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().status(), Lease.Status.Active));
            leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().currentTerm().type(), LeaseTerm.Type.Periodic));
            leaseCriteria.add(PropertyCriterion.isNull(leaseCriteria.proto().leaseTo()));
        } else {
            throw new RuntimeException("it's unknown to to interpret the lease filter '" + leaseFilter.getPath().toString() + "'");
        }
        return leaseCriteria;
    }

    private int numOfOccupiedUnits(Vector<Building> buildings) {
        EntityQueryCriteria<AptUnit> criteria = fillOccupiedUnitsCriteria(EntityQueryCriteria.create(AptUnit.class), buildings);
        Persistence.applyDatasetAccessRule(criteria);
        return Persistence.service().count(criteria);
    }

    private <Criteria extends EntityQueryCriteria<? extends AptUnit>> Criteria fillOccupiedUnitsCriteria(Criteria criteria, Vector<Building> buildings) {
        LogicalDate when = new LogicalDate(SystemDateManager.getDate());

        if (buildings != null && !buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }

        criteria.add(PropertyCriterion.le(criteria.proto().unitOccupancySegments().$().dateFrom(), when));
        criteria.add(PropertyCriterion.ge(criteria.proto().unitOccupancySegments().$().dateTo(), when));

        criteria.add(PropertyCriterion.eq(criteria.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.occupied));

        return criteria;
    }

}
