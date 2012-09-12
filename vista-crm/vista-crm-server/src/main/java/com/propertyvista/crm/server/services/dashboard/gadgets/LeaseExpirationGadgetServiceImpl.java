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
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetServiceImpl implements LeaseExpirationGadgetService {

    @Override
    public void countData(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildings) {
        LeaseExpirationGadgetDataDTO proto = EntityFactory.getEntityPrototype(LeaseExpirationGadgetDataDTO.class);

        LeaseExpirationGadgetDataDTO gadgetData = EntityFactory.create(LeaseExpirationGadgetDataDTO.class);

        gadgetData.numOfLeasesEndingThisMonth().setValue(
                Persistence.service().count(fillLeaseFilterCriteria(EntityQueryCriteria.create(Lease.class), buildings, proto.numOfLeasesEndingThisMonth())));
        gadgetData.numOfLeasesEndingNextMonth().setValue(
                Persistence.service().count(fillLeaseFilterCriteria(EntityQueryCriteria.create(Lease.class), buildings, proto.numOfLeasesEndingNextMonth())));
        gadgetData.numOfLeasesEndingOver90Days().setValue(
                Persistence.service().count(fillLeaseFilterCriteria(EntityQueryCriteria.create(Lease.class), buildings, proto.numOfLeasesEndingOver90Days())));

        gadgetData.unitsOccupied().setValue(numOfOccupiedUnits(buildings));

        int numOfUnits = CommonQueries.numOfUnits(buildings);
        if (numOfUnits != 0) {
            gadgetData.unitOccupancy().setValue((double) gadgetData.unitsOccupied().getValue() / numOfUnits);
        }

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

    private <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria fillLeaseFilterCriteria(Criteria leaseCriteria, Vector<Building> buildings,
            IObject<?> leaseFilter) {

        LogicalDate leaseToLowerBound = null;
        LogicalDate leaseToUpperBound = null;
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());

        LeaseExpirationGadgetDataDTO proto = EntityFactory.getEntityPrototype(LeaseExpirationGadgetDataDTO.class);
        if (proto.numOfLeasesEndingThisMonth() == leaseFilter) {

            leaseToLowerBound = Utils.beginningOfMonth(today);
            leaseToUpperBound = Utils.endOfMonth(today);
        } else if (proto.numOfLeasesEndingNextMonth() == leaseFilter) {

            leaseToLowerBound = Utils.beginningOfNextMonth(today);
            leaseToUpperBound = Utils.endOfMonth(leaseToLowerBound);
        } else if (proto.numOfLeasesEndingOver90Days() == leaseFilter) {
            // TODO this is not correct
            leaseToLowerBound = Utils.beginningOfNextMonth(Utils.beginningOfNextMonth(Utils.beginningOfNextMonth(today)));
            leaseToUpperBound = null;
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

        criteria.add(PropertyCriterion.eq(criteria.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.leased));

        return criteria;
    }

}
