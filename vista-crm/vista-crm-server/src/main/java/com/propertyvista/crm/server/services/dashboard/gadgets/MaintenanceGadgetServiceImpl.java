/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
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
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.MaintenanceGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.maintenance.IssuePriority;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceGadgetServiceImpl implements MaintenanceGadgetService {

    @Override
    public void countData(AsyncCallback<MaintenanceGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        MaintenanceGadgetDataDTO summary = EntityFactory.create(MaintenanceGadgetDataDTO.class);

        count(summary.openWorkOrders(), buildingsFilter);
        count(summary.urgentWorkOrders(), buildingsFilter);
        count(summary.outstandingWorkOrders1to2days(), buildingsFilter);
        count(summary.outstandingWorkOrders2to3days(), buildingsFilter);
        count(summary.outstandingWorkOrdersMoreThan3days(), buildingsFilter);

        callback.onSuccess(summary);
    }

    @Override
    public void makeMaintenaceRequestCriteria(AsyncCallback<EntityListCriteria<MaintenanceRequestDTO>> callback, Vector<Building> buildingsFilter, String preset) {
        callback.onSuccess(fillCriteria(EntityListCriteria.create(MaintenanceRequestDTO.class), buildingsFilter,
                EntityFactory.getEntityPrototype(MaintenanceGadgetDataDTO.class).getMember(new Path(preset))));
    }

    private void count(IPrimitive<Integer> member, Vector<Building> buildingsFilter) {
        IObject<?> protoMember = EntityFactory.getEntityPrototype(MaintenanceGadgetDataDTO.class).getMember(member.getPath());
        member.setValue(Persistence.service().count(fillCriteria(EntityQueryCriteria.create(MaintenanceRequest.class), buildingsFilter, protoMember)));
    }

    <Criteria extends EntityQueryCriteria<? extends MaintenanceRequest>> Criteria fillCriteria(Criteria criteria, Vector<Building> buildingsFilter,
            IObject<?> member) {
        MaintenanceGadgetDataDTO proto = EntityFactory.getEntityPrototype(MaintenanceGadgetDataDTO.class);

        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
        LogicalDate lowerBound = null;
        LogicalDate upperBound = null;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(today);

        criteria.add(PropertyCriterion.in(criteria.proto().status(), new Vector<MaintenanceRequestStatus>(MaintenanceRequestStatus.opened())));

        if (proto.openWorkOrders() == member) {
            // already this is just to enforce the validity of the parameter
        } else if (proto.urgentWorkOrders() == member) {
            criteria.add(PropertyCriterion.eq(criteria.proto().issueClassification().priority(), IssuePriority.EMERGENCY));
        } else if (proto.outstandingWorkOrders1to2days() == member) {
            cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
            upperBound = new LogicalDate(cal.getTime());
            cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
            lowerBound = new LogicalDate(cal.getTime());
        } else if (proto.outstandingWorkOrders2to3days() == member) {
            cal.add(GregorianCalendar.DAY_OF_YEAR, -2);
            upperBound = new LogicalDate(cal.getTime());
            cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
            lowerBound = new LogicalDate(cal.getTime());
        } else if (proto.outstandingWorkOrdersMoreThan3days() == member) {
            cal.add(GregorianCalendar.DAY_OF_YEAR, -3);
            upperBound = new LogicalDate(cal.getTime());
        } else {
            throw new RuntimeException("don't know how to prepare a search criteria for member '" + member.getPath().toString() + "'");
        }

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().leaseParticipant().lease().unit().building(), buildingsFilter));
        }
        if (lowerBound != null) {
            criteria.add(PropertyCriterion.ge(criteria.proto().submitted(), lowerBound));
        }
        if (upperBound != null) {
            criteria.add(PropertyCriterion.le(criteria.proto().submitted(), upperBound));
        }

        return criteria;
    }
}
