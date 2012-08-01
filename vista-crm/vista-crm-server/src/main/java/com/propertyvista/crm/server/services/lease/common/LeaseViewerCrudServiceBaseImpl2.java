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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase2;
import com.propertyvista.domain.tenant.Guarantor2;
import com.propertyvista.domain.tenant.Tenant2;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.LeaseParticipant2;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO2;

public abstract class LeaseViewerCrudServiceBaseImpl2<DTO extends LeaseDTO2> extends LeaseCrudServiceBaseImpl2<DTO> implements LeaseViewerCrudServiceBase2<DTO> {

    private final boolean isApplication;

    protected LeaseViewerCrudServiceBaseImpl2(Class<DTO> dtoClass) {
        super(dtoClass);
        isApplication = dtoClass.equals(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease2 in, DTO dto) {
        super.enhanceRetrieved(in, dto);

        // create bill preview for draft leases/applications:
        if (in.status().getValue().isDraft()) {
//            dto.billingPreview().set(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(in)));
        }
    }

    @Override
    public void retrieveUsers(AsyncCallback<Vector<LeaseParticipant2>> callback, Key entityId) {
        Lease2 lease = Persistence.service().retrieve(dboClass, (isApplication ? entityId.asDraftKey() : entityId));
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        LeaseTerm currentLeaseTerm = getCurrentLeaseTerm(lease);

        Vector<LeaseParticipant2> users = new Vector<LeaseParticipant2>();

        Persistence.service().retrieve(currentLeaseTerm.version().tenants());
        for (Tenant2 tenant : currentLeaseTerm.version().tenants()) {
            Persistence.service().retrieve(tenant);
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            default:
                break;
            }
        }

        Persistence.service().retrieve(currentLeaseTerm.version().guarantors());
        for (Guarantor2 guarantor : currentLeaseTerm.version().guarantors()) {
            users.add(guarantor);
        }

        callback.onSuccess(users);
    }
}