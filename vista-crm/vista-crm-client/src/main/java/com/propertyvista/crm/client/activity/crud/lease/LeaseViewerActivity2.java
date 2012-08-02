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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.essentials.client.DeferredProcessDialog;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase2;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView2;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService2;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant2;
import com.propertyvista.dto.LeaseDTO2;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerActivity2 extends LeaseViewerActivityBase2<LeaseDTO2> implements LeaseViewerView2.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivity2.class);

    private final IListerView.Presenter<BillDataDTO> billLister;

    private final IListerView.Presenter<PaymentRecordDTO> paymentLister;

    private final IListerView.Presenter<LeaseAdjustment> leaseAdjustmentLister;

    private LeaseDTO2 currentValue;

    public LeaseViewerActivity2(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseViewerView2.class), GWT.<LeaseViewerCrudService2> create(LeaseViewerCrudService2.class));

        billLister = new ListerActivityBase<BillDataDTO>(place, ((LeaseViewerView2) getView()).getBillListerView(),
                GWT.<BillCrudService> create(BillCrudService.class), BillDataDTO.class);

        paymentLister = new ListerActivityBase<PaymentRecordDTO>(place, ((LeaseViewerView2) getView()).getPaymentListerView(),
                GWT.<PaymentCrudService> create(PaymentCrudService.class), PaymentRecordDTO.class);

        leaseAdjustmentLister = new ListerActivityBase<LeaseAdjustment>(place, ((LeaseViewerView2) getView()).getLeaseAdjustmentListerView(),
                GWT.<LeaseAdjustmentCrudService> create(LeaseAdjustmentCrudService.class), LeaseAdjustment.class);
    }

    @Override
    public void onStop() {
        ((AbstractActivity) billLister).onStop();
        ((AbstractActivity) paymentLister).onStop();
        ((AbstractActivity) leaseAdjustmentLister).onStop();
        super.onStop();
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO2 result) {
        super.onPopulateSuccess(result);

        populateBills(currentValue = result);
        populatePayments(result);
        populateLeaseAdjustments(result);
    }

    protected void populateBills(Lease2 result) {
        List<Criterion> preDefinedFilters = new ArrayList<Criterion>();
//        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(BillDataDTO.class).bill().billingAccount().id(), result.billingAccount()
//                .getPrimaryKey()));
        billLister.setPreDefinedFilters(preDefinedFilters);
        billLister.populate();
    }

    protected void populatePayments(Lease2 result) {
//        paymentLister.setParent(result.billingAccount().getPrimaryKey());
        paymentLister.populate();
    }

    protected void populateLeaseAdjustments(Lease2 result) {
//        leaseAdjustmentLister.setParent(result.billingAccount().getPrimaryKey());
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
        ((LeaseViewerCrudService2) getService()).notice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), date, moveOut);
    }

    @Override
    public void cancelNotice() {
        ((LeaseViewerCrudService2) getService()).cancelNotice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void evict(LogicalDate date, LogicalDate moveOut) {
        ((LeaseViewerCrudService2) getService()).evict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), date, moveOut);
    }

    @Override
    public void cancelEvict() {
        ((LeaseViewerCrudService2) getService()).cancelEvict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void activate() {
        ((LeaseViewerCrudService2) getService()).activate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                setEntityIdAsCurrentKey();
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void sendMail(List<LeaseParticipant2> users, EmailTemplateType emailType) {
        ((LeaseViewerCrudService2) getService()).sendMail(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseViewerView2) getView()).reportSendMailActionResult(message);
            }
        }, getEntityId(), new Vector<LeaseParticipant2>(users), emailType);
    }

    @Override
    public void editCurrentTerm() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(((LeaseViewerView2) getView()).getCurrentTerm().getPrimaryKey()));
    }
}
