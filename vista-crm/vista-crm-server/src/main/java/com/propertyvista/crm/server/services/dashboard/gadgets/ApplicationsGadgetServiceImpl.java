/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.EnumSet;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.gadgets.ApplicationsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ApplicationsGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.dto.LeaseApplicationDTO;

public class ApplicationsGadgetServiceImpl implements ApplicationsGadgetService {

    @Override
    public void countData(AsyncCallback<ApplicationsGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        ApplicationsGadgetDataDTO data = EntityFactory.create(ApplicationsGadgetDataDTO.class);

        count(data.applications(), buildingsFilter);
        count(data.inProgress(), buildingsFilter);
        count(data.pending(), buildingsFilter);
        count(data.approved(), buildingsFilter);
        count(data.declined(), buildingsFilter);
        count(data.cancelled(), buildingsFilter);

        callback.onSuccess(data);
    }

    @Override
    public void makeApplicaitonsCriteria(AsyncCallback<EntityListCriteria<LeaseApplicationDTO>> callback, Vector<Building> buildingsFilter,
            String encodedFilterData) {
        callback.onSuccess(applicationsCriteria(EntityListCriteria.create(LeaseApplicationDTO.class), buildingsFilter, encodedFilterData));
    }

    <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria applicationsCriteria(Criteria criteria, Vector<Building> builidngsFilter,
            String applicationsFilter) {

        criteria.ge(criteria.proto().creationDate(), Util.beginningOfMonth(Util.dayOfCurrentTransaction()));
        criteria.le(criteria.proto().creationDate(), Util.dayOfCurrentTransaction());

        if (builidngsFilter != null && !builidngsFilter.isEmpty()) {
            criteria.in(criteria.proto().unit().building(), builidngsFilter);
        }

        ApplicationsGadgetDataDTO proto = EntityFactory.getEntityPrototype(ApplicationsGadgetDataDTO.class);
        IObject<?> applicationsFilterMember = proto.getMember(new Path(applicationsFilter));

        if (proto.applications() == applicationsFilterMember) {
            criteria.in(criteria.proto().leaseApplication().status(), EnumSet.allOf(LeaseApplication.Status.class));
        } else if (proto.inProgress() == applicationsFilterMember) {
            criteria.in(criteria.proto().leaseApplication().status(), LeaseApplication.Status.InProgress);
        } else if (proto.pending() == applicationsFilterMember) {
            criteria.in(criteria.proto().leaseApplication().status(),
                    EnumSet.of(LeaseApplication.Status.Submitted, LeaseApplication.Status.PendingFurtherInformation, LeaseApplication.Status.PendingDecision));
        } else if (proto.approved() == applicationsFilterMember) {
            criteria.eq(criteria.proto().leaseApplication().status(), LeaseApplication.Status.Approved);
        } else if (proto.declined() == applicationsFilterMember) {
            criteria.eq(criteria.proto().leaseApplication().status(), LeaseApplication.Status.Declined);
        } else if (proto.cancelled() == applicationsFilterMember) {
            criteria.eq(criteria.proto().leaseApplication().status(), LeaseApplication.Status.Cancelled);
        } else {
            throw new IllegalStateException("Unknown filter property: '" + applicationsFilter + "'");
        }

        return criteria;
    }

    private void count(IPrimitive<Integer> member, Vector<Building> buildingsFilter) {
        EntityQueryCriteria<Lease> criteria = applicationsCriteria(EntityQueryCriteria.create(Lease.class), buildingsFilter, member.getPath().toString());
        Persistence.applyDatasetAccessRule(criteria);
        member.setValue(Persistence.service().count(criteria));
    }
}
