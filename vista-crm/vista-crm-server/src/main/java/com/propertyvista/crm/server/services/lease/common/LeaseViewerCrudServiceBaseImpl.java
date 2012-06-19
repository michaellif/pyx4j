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
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;

public abstract class LeaseViewerCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractVersionedCrudServiceDtoImpl<Lease, DTO> implements
        LeaseViewerCrudServiceBase<DTO> {

    private final boolean isApplication;

    protected LeaseViewerCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(Lease.class, dtoClass);
        isApplication = dtoClass.equals(LeaseApplicationDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO dto) {
        enhanceRetrievedCommon(in, dto);

        // load detached entities:
        Persistence.service().retrieve(dto.billingAccount().adjustments());
//      Persistence.service().retrieve(dto.documents());

        if (!dto.unit().isNull()) {
            // fill selected building by unit:
            dto.selectedBuilding().set(dto.unit().belongsTo());
        }

        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.version().leaseProducts().serviceItem());
        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item);

            // Need this for navigation
            Persistence.service().retrieve(item.item().product());
        }

        for (Tenant item : dto.version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        for (Guarantor item : dto.version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        // Need this for navigation
        Persistence.service().retrieve(dto.version().leaseProducts().serviceItem().item().product());

        // create bill preview for draft leases/applications:
        if (in.version().status().getValue().isDraft()) {
            dto.billingPreview().set(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(in)));
        }
    }

    @Override
    protected void enhanceListRetrieved(Lease in, DTO dto) {
        enhanceRetrievedCommon(in, dto);
    }

    private void enhanceRetrievedCommon(Lease in, DTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        Persistence.service().retrieve(dto.version().tenants());
        Persistence.service().retrieve(dto.version().guarantors());

        Persistence.service().retrieve(dto.billingAccount());
    }

    @Override
    protected void persist(Lease dbo, DTO in) {
        throw new Error("Should not be called!");
    }

    @Override
    protected void saveAsFinal(Lease entity) {
        ServerSideFactory.create(LeaseFacade.class).saveAsFinal(entity);
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
            }
        }

        Persistence.service().retrieve(lease.version().guarantors());
        for (Guarantor guarantor : lease.version().guarantors()) {
            users.add(guarantor);
        }

        callback.onSuccess(users);
    }
}