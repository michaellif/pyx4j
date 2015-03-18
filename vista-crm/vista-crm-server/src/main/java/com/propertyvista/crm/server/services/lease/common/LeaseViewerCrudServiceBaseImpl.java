/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 */
package com.propertyvista.crm.server.services.lease.common;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.legal.eviction.EvictionCaseFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseViewerCrudServiceBaseImpl<DTO extends LeaseDTO> extends LeaseCrudServiceBaseImpl<DTO> implements LeaseViewerCrudServiceBase<DTO> {

    protected LeaseViewerCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(dtoClass);
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, to, retrieveTarget);

        Persistence.service().retrieve(to.currentTerm().version().utilities());

        // create bill preview for draft leases/applications:
        if (in.status().getValue().isDraft() && Lease.Status.isApplicationUnitSelected(in)) {
            to.billingPreview().set(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(in)));
        }

        if (Lease.Status.isApplicationUnitSelected(to)) {
            Persistence.service().retrieveMember(to.unit().floorplan(), AttachLevel.ToStringMembers);

            Pair<Date, Lease> result = ServerSideFactory.create(OccupancyFacade.class).isReserved(to.unit().getPrimaryKey());
            to.isUnitReserved().setValue(in.equals(result.getB()));
            if (to.isUnitReserved().getValue()) {
                to.reservedUntil().setValue(result.getA());
            }

            checkUnitMoveOut(to);
        }

        to.evictionHistory().addAll(ServerSideFactory.create(EvictionCaseFacade.class).getEvictionHistory(in));
    }

    @Override
    public void retrieveParticipants(final AsyncCallback<Vector<LeaseTermParticipant<?>>> callback, Key entityId, final Boolean includeDependants) {
        retrieve(new AsyncCallback<DTO>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DTO result) {
                Vector<LeaseTermParticipant<?>> participants = new Vector<LeaseTermParticipant<?>>();

                if (includeDependants) {
                    participants.addAll(result.currentTerm().version().tenants());
                } else {
                    for (LeaseTermTenant tenant : result.currentTerm().version().tenants()) {
                        Persistence.ensureRetrieve(tenant, AttachLevel.Attached);
                        switch (tenant.role().getValue()) {
                        case Applicant:
                        case CoApplicant:
                            participants.add(tenant);
                        default:
                            break;
                        }
                    }
                }

                participants.addAll(result.currentTerm().version().guarantors());

                callback.onSuccess(participants);
            }
        }, entityId, RetrieveTarget.View);
    }

    void checkUnitMoveOut(DTO dto) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().unit(), dto.unit());
        criteria.in(criteria.proto().status(), Lease.Status.current());
        criteria.ne(criteria.proto().id(), dto.getPrimaryKey());
        criteria.isNotNull(criteria.proto().completion());
        criteria.isNull(criteria.proto().actualMoveOut());

        if (Persistence.service().exists(criteria)) {
            dto.unitMoveOutNote().setValue("This unit is not freed completely by previous tenant!");
        }
    }

    @Override
    public void reserveUnit(AsyncCallback<VoidSerializable> callback, Key entityId, int durationHours) {
        Lease lease = Persistence.secureRetrieve(Lease.class, entityId);
        ServerSideFactory.create(OccupancyFacade.class).reserve(lease, durationHours);
        callback.onSuccess(null);
    }

    @Override
    public void releaseUnit(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, entityId);
        ServerSideFactory.create(OccupancyFacade.class).unreserveIfReservered(lease);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}