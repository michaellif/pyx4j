/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.DeferredProcessDialog;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerActivity extends LeaseViewerActivityBase<LeaseDTO> implements LeaseViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivity.class);

    private final IListerView.Presenter<BillDTO> billLister;

    private final IListerView.Presenter<PaymentRecordDTO> paymentLister;

    private final IListerView.Presenter<LeaseAdjustment> leaseAdjustmentLister;

    private LeaseDTO currentValue;

    @SuppressWarnings("unchecked")
    public LeaseViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseViewerView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class));

        billLister = new ListerActivityBase<BillDTO>(place, ((LeaseViewerView) getView()).getBillListerView(),
                (AbstractCrudService<BillDTO>) GWT.create(BillCrudService.class), BillDTO.class);

        paymentLister = new ListerActivityBase<PaymentRecordDTO>(place, ((LeaseViewerView) getView()).getPaymentListerView(),
                (AbstractCrudService<PaymentRecordDTO>) GWT.create(PaymentCrudService.class), PaymentRecordDTO.class);

        leaseAdjustmentLister = new ListerActivityBase<LeaseAdjustment>(place, ((LeaseViewerView) getView()).getLeaseAdjustmentListerView(),
                (AbstractCrudService<LeaseAdjustment>) GWT.create(LeaseAdjustmentCrudService.class), LeaseAdjustment.class);
    }

    @Override
    public Presenter<BillDTO> getBillListerPresenter() {
        return billLister;
    }

    @Override
    public Presenter<PaymentRecordDTO> getPaymentListerPresenter() {
        return paymentLister;
    }

    @Override
    public Presenter<LeaseAdjustment> getLeaseAdjustmentListerPresenter() {
        return leaseAdjustmentLister;
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);

        populateBills(currentValue = result);
        populatePayments(result);
        populateLeaseAdjustments(result);
    }

    protected void populateBills(LeaseDTO result) {
        List<Criterion> preDefinedFilters = new ArrayList<Criterion>();
        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(BillDTO.class).billingAccount().id(), result.billingAccount()
                .getPrimaryKey()));
        billLister.setPreDefinedFilters(preDefinedFilters);
        billLister.populate();
    }

    protected void populatePayments(LeaseDTO result) {
        List<Criterion> preDefinedFilters = new ArrayList<Criterion>();
        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(PaymentRecordDTO.class).billingAccount().id(), result.billingAccount()
                .getPrimaryKey()));
        paymentLister.setPreDefinedFilters(preDefinedFilters);
        paymentLister.setParent(result.billingAccount().getPrimaryKey());
        paymentLister.populate();
    }

    protected void populateLeaseAdjustments(LeaseDTO result) {
        List<Criterion> preDefinedFilters = new ArrayList<Criterion>();
        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(LeaseAdjustment.class).billingAccount().id(), result.billingAccount()
                .getPrimaryKey()));
        leaseAdjustmentLister.setPreDefinedFilters(preDefinedFilters);
        leaseAdjustmentLister.setParent(result.billingAccount().getPrimaryKey());
        leaseAdjustmentLister.populate();
    }

    // Actions:

    @Override
    public void startBilling() {
        GWT.<BillingExecutionService> create(BillingExecutionService.class).startBilling(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Billing"), i18n.tr("Running Billing.."), false) {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        // Navigate to created bill
                        super.onDeferredSuccess(result);
                        populateBills(currentValue);
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, entityId);

    }

    @Override
    public void notice(LogicalDate date, LogicalDate moveOut) {
        ((LeaseCrudService) getService()).notice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, date, moveOut);
    }

    @Override
    public void cancelNotice() {
        ((LeaseCrudService) getService()).cancelNotice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }

    @Override
    public void evict(LogicalDate date, LogicalDate moveOut) {
        ((LeaseCrudService) getService()).evict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, date, moveOut);
    }

    @Override
    public void cancelEvict() {
        ((LeaseCrudService) getService()).cancelEvict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }

    @Override
    public void activate() {
        ((LeaseCrudService) getService()).activate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                entityId = entityId.asCurrentKey();
                populate();
            }
        }, entityId);
    }

    @Override
    public void sendMail(List<LeaseParticipant> users, EmailTemplateType emailType) {
        ((LeaseCrudService) getService()).sendMail(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseViewerView) getView()).reportSendMailActionResult(message);
            }
        }, entityId, new Vector<LeaseParticipant>(users), emailType);
    }
}
