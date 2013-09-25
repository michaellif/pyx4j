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
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.shared.config.VistaFeatures;

public abstract class LeaseViewerCrudServiceBaseImpl<DTO extends LeaseDTO> extends LeaseCrudServiceBaseImpl<DTO> implements LeaseViewerCrudServiceBase<DTO> {

    private final boolean isApplication;

    protected LeaseViewerCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(dtoClass);
        isApplication = dtoClass.equals(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, to, retrieveTarget);

        if (!VistaFeatures.instance().yardiIntegration()) {
            // create bill preview for draft leases/applications:
            if (in.status().getValue().isDraft()) {
                to.billingPreview().set(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(in)));
            }
        }

        if (!to.unit().isNull()) {
            Persistence.service().retrieveMember(to.unit().floorplan(), AttachLevel.ToStringMembers);

            checkUnitMoveOut(to);
        }

        EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
        criteria.eq(criteria.proto().lease(), in);
        criteria.ne(criteria.proto().id(), in.currentTerm().getPrimaryKey());
        criteria.ne(criteria.proto().status(), LeaseTerm.Status.Offer);
        to.historyPresent().setValue(Persistence.service().exists(criteria));
    }

    @Override
    public void retrieveUsers(AsyncCallback<Vector<LeaseTermParticipant<?>>> callback, Key entityId) {
        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(EntityFactory.createIdentityStub(Lease.class, entityId), false);

        Vector<LeaseTermParticipant<?>> users = new Vector<LeaseTermParticipant<?>>();

        assert (!lease.currentTerm().isNull());
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            Persistence.service().retrieve(tenant);
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            default:
                break;
            }
        }

        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        for (LeaseTermGuarantor guarantor : lease.currentTerm().version().guarantors()) {
            users.add(guarantor);
        }

        callback.onSuccess(users);
    }

    void checkUnitMoveOut(DTO dto) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().unit(), dto.unit());
        criteria.in(criteria.proto().status(), Lease.Status.current());
        criteria.ne(criteria.proto().id(), dto.getPrimaryKey());
        criteria.isNotNull(criteria.proto().completion());
        criteria.isNull(criteria.proto().actualMoveOut());

        if (Persistence.service().exists(criteria)) {
            dto.unitMoveOutNote().setValue("Warning: This unit is not freed completely by previous tenant!");
        }

    }
}