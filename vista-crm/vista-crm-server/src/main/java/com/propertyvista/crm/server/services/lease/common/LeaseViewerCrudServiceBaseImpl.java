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
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseViewerCrudServiceBaseImpl<DTO extends LeaseDTO> extends LeaseCrudServiceBaseImpl<DTO> implements LeaseViewerCrudServiceBase<DTO> {

    private final boolean isApplication;

    protected LeaseViewerCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(dtoClass);
        isApplication = dtoClass.equals(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO dto) {
        super.enhanceRetrieved(in, dto);

        // create bill preview for draft leases/applications:
        if (in.status().getValue().isDraft()) {
            dto.billingPreview().set(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(in)));
        }
    }

    @Override
    public void retrieveUsers(AsyncCallback<Vector<LeaseParticipant>> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(dboClass, (isApplication ? entityId.asDraftKey() : entityId));
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        Vector<LeaseParticipant> users = new Vector<LeaseParticipant>();

        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenant : lease.version().tenants()) {
            Persistence.service().retrieve(tenant);
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            default:
                break;
            }
        }

        Persistence.service().retrieve(lease.version().guarantors());
        for (Guarantor guarantor : lease.version().guarantors()) {
            users.add(guarantor);
        }

        callback.onSuccess(users);
    }
}