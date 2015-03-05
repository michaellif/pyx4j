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
 */
package com.propertyvista.crm.server.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
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
        bindCompleteObject();
    }

    @Override
    protected LeaseAdjustment init(InitializationData initializationData) {
        LeaseAdjustment entity = EntityFactory.create(LeaseAdjustment.class);

        entity.receivedDate().setValue(SystemDateManager.getLogicalDate());
        entity.targetDate().setValue(SystemDateManager.getLogicalDate());
        entity.status().setValue(Status.draft);

        return entity;
    }

    @Override
    protected boolean persist(LeaseAdjustment dbo, LeaseAdjustment to) {
        updateAdjustments(dbo);
        return super.persist(dbo, to);
    }

    @Override
    protected void enhanceRetrieved(LeaseAdjustment bo, LeaseAdjustment to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.service().retrieve(to.billingAccount());
        Persistence.service().retrieve(to.billingAccount().lease());
        Persistence.service().retrieve(to.billingAccount().lease().unit());
    }

    private void updateAdjustments(LeaseAdjustment adj) {
        if (adj.created().isNull()) {
            adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
        }
    }

    @Override
    public void calculateTax(final AsyncCallback<LeaseAdjustment> callback, LeaseAdjustment currentValue) {
        currentValue = currentValue.<LeaseAdjustment> duplicate();
        ServerSideFactory.create(BillingFacade.class).updateLeaseAdjustmentTax(currentValue);
        callback.onSuccess(currentValue);
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
