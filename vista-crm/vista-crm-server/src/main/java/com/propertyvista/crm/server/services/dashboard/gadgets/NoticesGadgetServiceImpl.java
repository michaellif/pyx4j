/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
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

import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.CommonQueries;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class NoticesGadgetServiceImpl implements NoticesGadgetService {

    @Override
    public void countData(AsyncCallback<NoticesGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        NoticesGadgetDataDTO gadgetData = EntityFactory.create(NoticesGadgetDataDTO.class);

        countNotices(gadgetData.noticesLeavingThisMonth(), buildingsFilter);
        countNotices(gadgetData.noticesLeavingNextMonth(), buildingsFilter);
        countNotices(gadgetData.noticesLeaving60to90Days(), buildingsFilter);
        countNotices(gadgetData.noticesLeavingOver90Days(), buildingsFilter);

        gadgetData.totalUnits().setValue(CommonQueries.numOfUnits(buildingsFilter));
        gadgetData.vacantUnits().setValue(numOfVacantUnits(buildingsFilter));

        callback.onSuccess(gadgetData);
    }

    @Override
    public void makeUnitFilterCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, Vector<Building> buildingsFilter, String unitsFilter) {
        callback.onSuccess(vacantUnitsCriteria(EntityListCriteria.create(AptUnitDTO.class), buildingsFilter));
    }

    @Override
    public void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter, String leaseFilter) {
        callback.onSuccess(fillNoticesCriteria(EntityListCriteria.create(LeaseDTO.class), EntityFactory.getEntityPrototype(NoticesGadgetDataDTO.class)
                .getMember(new Path(leaseFilter)), buildingsFilter));
    }

    private void countNotices(IPrimitive<Integer> member, Vector<Building> buildingsFilter) {
        EntityListCriteria<Lease> criteria = fillNoticesCriteria(EntityListCriteria.create(Lease.class), member, buildingsFilter);
        Persistence.applyDatasetAccessRule(criteria);
        member.setValue(Persistence.service().count(criteria));
    }

    private <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria fillNoticesCriteria(Criteria criteria, IObject<?> noticesFilterPreset,
            Vector<Building> buildingsFilter) {

        LogicalDate lowerLeavingBound = null;
        LogicalDate upperLeavingBound = null;
        LogicalDate today = SystemDateManager.getLogicalDate();

        NoticesGadgetDataDTO proto = EntityFactory.getEntityPrototype(NoticesGadgetDataDTO.class);
        IObject<?> noticesFilter = proto.getMember(noticesFilterPreset.getPath());

        if (proto.noticesLeavingThisMonth() == noticesFilter) {
            lowerLeavingBound = today;
            upperLeavingBound = Util.endOfMonth(today);
        } else if (proto.noticesLeavingNextMonth() == noticesFilter) {
            lowerLeavingBound = Util.beginningOfNextMonth(today);
            upperLeavingBound = Util.endOfMonth(lowerLeavingBound);
        } else if (proto.noticesLeaving60to90Days() == noticesFilter) {
            lowerLeavingBound = Util.addDays(today, 60);
            upperLeavingBound = Util.addDays(today, 90);
        } else if (proto.noticesLeavingOver90Days() == noticesFilter) {
            lowerLeavingBound = Util.addDays(today, 91);
            upperLeavingBound = null;
        } else {
            throw new RuntimeException("unknown filter criteria: " + noticesFilter.getPath().toString());
        }

        if (lowerLeavingBound != null) {
            criteria.add(PropertyCriterion.ge(criteria.proto().expectedMoveOut(), lowerLeavingBound));
        }
        if (upperLeavingBound != null) {
            criteria.add(PropertyCriterion.le(criteria.proto().expectedMoveOut(), upperLeavingBound));
        }
        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildingsFilter));
        }
        criteria.add(PropertyCriterion.eq(criteria.proto().completion(), Lease.CompletionType.Notice));
        return criteria;
    }

    private <Criteria extends EntityQueryCriteria<? extends AptUnit>> Criteria vacantUnitsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildingsFilter));
        }
        LogicalDate today = SystemDateManager.getLogicalDate();
        criteria.add(PropertyCriterion.le(criteria.proto().availability().availableForRent(), today));
        return criteria;
    }

    private Integer numOfVacantUnits(Vector<Building> buildingsFilter) {
        EntityQueryCriteria<AptUnit> criteria = vacantUnitsCriteria(EntityQueryCriteria.create(AptUnit.class), buildingsFilter);
        Persistence.applyDatasetAccessRule(criteria);
        return Persistence.service().count(criteria);
    }

}
