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
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata.NoticesFilter;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class NoticesGadgetServiceImpl implements NoticesGadgetService {

    @Override
    public void notices(AsyncCallback<NoticesGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        NoticesGadgetDataDTO gadgetData = EntityFactory.create(NoticesGadgetDataDTO.class);

        gadgetData.noticesLeavingThisMonth().setValue(Persistence.service().count(noticesCriteria(NoticesFilter.THIS_MONTH, buildingsFilter)));
        gadgetData.noticesLeavingNextMonth().setValue(Persistence.service().count(noticesCriteria(NoticesFilter.NEXT_MONTH, buildingsFilter)));
        gadgetData.noticesLeavingOver90Days().setValue(Persistence.service().count(noticesCriteria(NoticesFilter.OVER_90_DAYS, buildingsFilter)));

        gadgetData.unitsVacant().setValue(Persistence.service().count(vacantUnitsCriteria(buildingsFilter)));

        int numOfUnits = CommonQueries.numOfUnits(buildingsFilter);
        if (numOfUnits != 0) {
            gadgetData.unitVacancy().setValue((double) gadgetData.unitsVacant().getValue() / numOfUnits);
        }

        callback.onSuccess(gadgetData);
    }

    @Override
    public void makeNoticesFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, NoticesFilter noticesFilter, Vector<Building> buildingsFilter) {
        callback.onSuccess(Utils.toDtoLeaseCriteria(noticesCriteria(noticesFilter, buildingsFilter)));
    }

    @Override
    public void makeVacantUnitsFilterCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, Vector<Building> buildingsFilter) {
        callback.onSuccess(Utils.toDtoUnitsCriteria(vacantUnitsCriteria(buildingsFilter)));
    }

    private EntityListCriteria<Lease> noticesCriteria(NoticesFilter noticesFilter, Vector<Building> buildingsFilter) {

        LogicalDate lowerLeavingBound = null;
        LogicalDate upperLeavingBound = null;
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());

        switch (noticesFilter) {
        case THIS_MONTH:
            lowerLeavingBound = Utils.beginningOfMonth(today);
            upperLeavingBound = Utils.endOfMonth(today);
            break;
        case NEXT_MONTH:
            lowerLeavingBound = Utils.beginningOfNextMonth(today);
            upperLeavingBound = Utils.endOfMonth(lowerLeavingBound);
            break;
        case OVER_90_DAYS:
            lowerLeavingBound = Utils.addDays(today, 90);
            upperLeavingBound = null;
            break;
        }

        EntityListCriteria<Lease> criteria = EntityListCriteria.create(Lease.class);

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

    private EntityListCriteria<AptUnit> vacantUnitsCriteria(Vector<Building> buildingsFilter) {

        EntityListCriteria<AptUnit> criteria = EntityListCriteria.create(AptUnit.class);
        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildingsFilter));
        }
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
        criteria.add(PropertyCriterion.le(criteria.proto()._availableForRent(), today));
        return criteria;
    }

}
