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

import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityFiltersBuilder;
import com.pyx4j.essentials.client.DeferredProcessDialog;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.billing.bill.BillListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase;
import com.propertyvista.crm.client.activity.crud.lease.common.deposit.DepositLifecycleListerActivity;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerActivity extends LeaseViewerActivityBase<LeaseDTO> implements LeaseViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivity.class);

    private final DepositLifecycleListerActivity depositLister;

    private final IListerView.Presenter<BillDataDTO> billLister;

    private final IListerView.Presenter<PaymentRecordDTO> paymentLister;

    private final IListerView.Presenter<LeaseAdjustment> leaseAdjustmentLister;

    private LeaseDTO currentValue;

    public LeaseViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseViewerView.class), GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class));

        depositLister = new DepositLifecycleListerActivity(place, ((LeaseViewerView) getView()).getDepositListerView());

        billLister = new BillListerActivity(place, ((LeaseViewerView) getView()).getBillListerView());

        paymentLister = new ListerActivityBase<PaymentRecordDTO>(place, ((LeaseViewerView) getView()).getPaymentListerView(),
                GWT.<PaymentCrudService> create(PaymentCrudService.class), PaymentRecordDTO.class);

        leaseAdjustmentLister = new ListerActivityBase<LeaseAdjustment>(place, ((LeaseViewerView) getView()).getLeaseAdjustmentListerView(),
                GWT.<LeaseAdjustmentCrudService> create(LeaseAdjustmentCrudService.class), LeaseAdjustment.class);
    }

    @Override
    public void onStop() {
        ((AbstractActivity) depositLister).onStop();
        ((AbstractActivity) billLister).onStop();
        ((AbstractActivity) paymentLister).onStop();
        ((AbstractActivity) leaseAdjustmentLister).onStop();
        super.onStop();
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);

        currentValue = result;

        populateDeposits(result);
        populateBills(result);
        populatePayments(result);
        populateLeaseAdjustments(result);
    }

    protected void populateDeposits(Lease result) {
        depositLister.setParent(result.billingAccount().getPrimaryKey());
        depositLister.populate();
    }

    protected void populateBills(Lease result) {
        EntityFiltersBuilder<BillDataDTO> filters = EntityFiltersBuilder.create(BillDataDTO.class);
        filters.eq(filters.proto().bill().billingAccount().id(), result.billingAccount().getPrimaryKey());
        billLister.setPreDefinedFilters(filters.getFilters());
        billLister.populate();
    }

    protected void populatePayments(Lease result) {
        paymentLister.setParent(result.billingAccount().getPrimaryKey());
        paymentLister.populate();
    }

    protected void populateLeaseAdjustments(Lease result) {
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
        }, EntityFactory.createIdentityStub(Lease.class, getEntityId()));

    }

    @Override
    public void notice(LogicalDate date, LogicalDate moveOut) {
        ((LeaseViewerCrudService) getService()).notice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), date, moveOut);
    }

    @Override
    public void cancelNotice(String decisionReason) {
        ((LeaseViewerCrudService) getService()).cancelNotice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void evict(LogicalDate date, LogicalDate moveOut) {
        ((LeaseViewerCrudService) getService()).evict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), date, moveOut);
    }

    @Override
    public void cancelEvict(String decisionReason) {
        ((LeaseViewerCrudService) getService()).cancelEvict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void sendMail(List<LeaseParticipant<?>> users, EmailTemplateType emailType) {
        ((LeaseViewerCrudService) getService()).sendMail(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseViewerView) getView()).reportSendMailActionResult(message);
            }
        }, getEntityId(), new Vector<LeaseParticipant<?>>(users), emailType);
    }

    @Override
    public void activate() {
        ((LeaseViewerCrudService) getService()).activate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                setEntityIdAsCurrentKey();
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void completeLease(LogicalDate from) {
        ((LeaseViewerCrudService) getService()).completeLease(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), from);
    }

    @Override
    public void closeLease(String decisionReason) {
        ((LeaseViewerCrudService) getService()).closeLease(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void cancelLease(String decisionReason) {
        ((LeaseViewerCrudService) getService()).cancelLease(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), decisionReason);
    }

    @Override
    public void createOffer(Type type) {
        ((LeaseViewerCrudService) getService()).createOffer(new DefaultAsyncCallback<LeaseTermDTO>() {
            @Override
            public void onSuccess(LeaseTermDTO result) {
                AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(result));
            }
        }, getEntityId(), type);
    }
}
