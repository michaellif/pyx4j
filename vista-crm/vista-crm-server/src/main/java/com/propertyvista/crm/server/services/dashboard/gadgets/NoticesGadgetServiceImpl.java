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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class NoticesGadgetServiceImpl implements NoticesGadgetService {

    @Override
    public void countData(AsyncCallback<NoticesGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        NoticesGadgetDataDTO gadgetData = EntityFactory.create(NoticesGadgetDataDTO.class);

        gadgetData.noticesLeavingThisMonth().setValue(
                Persistence.service().count(
                        fillNoticesCriteria(EntityListCriteria.create(Lease.class), EntityFactory.getEntityPrototype(NoticesGadgetDataDTO.class)
                                .noticesLeavingThisMonth(), buildingsFilter)));
        gadgetData.noticesLeavingNextMonth().setValue(
                Persistence.service().count(
                        fillNoticesCriteria(EntityListCriteria.create(Lease.class), EntityFactory.getEntityPrototype(NoticesGadgetDataDTO.class)
                                .noticesLeavingNextMonth(), buildingsFilter)));
        gadgetData.noticesLeavingOver90Days().setValue(
                Persistence.service().count(
                        fillNoticesCriteria(EntityListCriteria.create(Lease.class), EntityFactory.getEntityPrototype(NoticesGadgetDataDTO.class)
                                .noticesLeavingOver90Days(), buildingsFilter)));

        int numOfUnits = CommonQueries.numOfUnits(buildingsFilter);
        if (numOfUnits != 0) {
            int numOfVacantUnits = Persistence.service().count(vacantUnitsCriteria(EntityQueryCriteria.create(AptUnit.class), buildingsFilter));
            double pctOfVacantUnits = (double) numOfVacantUnits / numOfUnits;
            gadgetData.unitsVacant().setValue(Utils.countAndPercentLabel(numOfVacantUnits, pctOfVacantUnits));
        } else {
            gadgetData.unitsVacant().setValue("0");
        }

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

    private <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria fillNoticesCriteria(Criteria criteria, IObject<?> noticesFilter,
            Vector<Building> buildingsFilter) {

        LogicalDate lowerLeavingBound = null;
        LogicalDate upperLeavingBound = null;
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());

        NoticesGadgetDataDTO proto = EntityFactory.getEntityPrototype(NoticesGadgetDataDTO.class);

        if (proto.noticesLeavingThisMonth() == noticesFilter) {
            lowerLeavingBound = Utils.beginningOfMonth(today);
            upperLeavingBound = Utils.endOfMonth(today);
        } else if (proto.noticesLeavingNextMonth() == noticesFilter) {
            lowerLeavingBound = Utils.beginningOfNextMonth(today);
            upperLeavingBound = Utils.endOfMonth(lowerLeavingBound);
        } else if (proto.noticesLeavingOver90Days() == noticesFilter) {
            lowerLeavingBound = Utils.addDays(today, 90);
            upperLeavingBound = null;
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

        return criteria;
    }

    private <Criteria extends EntityQueryCriteria<? extends AptUnit>> Criteria vacantUnitsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildingsFilter));
        }
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
        criteria.add(PropertyCriterion.le(criteria.proto()._availableForRent(), today));
        return criteria;
    }

}
