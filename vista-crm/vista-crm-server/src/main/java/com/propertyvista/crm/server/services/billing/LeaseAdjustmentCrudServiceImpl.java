/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;

public class LeaseAdjustmentCrudServiceImpl extends AbstractCrudServiceImpl<LeaseAdjustment> implements LeaseAdjustmentCrudService {

    public LeaseAdjustmentCrudServiceImpl() {
        super(LeaseAdjustment.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void persist(LeaseAdjustment dbo, LeaseAdjustment dto) {
        updateAdjustments(dbo);
        super.persist(dbo, dto);
    }

    @Override
    protected void enhanceRetrieved(LeaseAdjustment entity, LeaseAdjustment dto, RetrieveTarget RetrieveTarget) {
        super.enhanceRetrieved(entity, dto, RetrieveTarget);

        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit());
    }

    private void updateAdjustments(LeaseAdjustment adj) {
        if (adj.created().isNull()) {
            adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
        }
    }

    @Override
    public void calculateTax(final AsyncCallback<BigDecimal> callback, LeaseAdjustment currentValue) {
        ServerSideFactory.create(BillingFacade.class).updateLeaseAdjustmentTax(currentValue);
        callback.onSuccess(currentValue.tax().getValue());
    }

    @Override
    public void submitAdjustment(AsyncCallback<LeaseAdjustment> callback, Key entityId) {
        LeaseAdjustment adjustment = Persistence.service().retrieve(LeaseAdjustment.class, entityId);
        adjustment.status().setValue(Status.submited);

        if (adjustment.executionType().getValue() == ExecutionType.immediate) {
            ServerSideFactory.create(ARFacade.class).postImmediateAdjustment(adjustment);
        }

        Persistence.service().merge(adjustment);
        Persistence.service().commit();

        retrieve(callback, entityId, RetrieveTarget.View);
    }
}
