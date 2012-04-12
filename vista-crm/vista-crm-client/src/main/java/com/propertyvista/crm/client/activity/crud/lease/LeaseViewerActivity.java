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
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.DeferredProcessDialog;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerActivity extends CrmViewerActivity<LeaseDTO> implements LeaseViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivity.class);

    private final IListerView.Presenter<BillDTO> billLister;

    private final IListerView.Presenter<PaymentRecord> paymentLister;

    private LeaseDTO currentValue;

    @SuppressWarnings("unchecked")
    public LeaseViewerActivity(Place place) {
        super(place, TenantViewFactory.instance(LeaseViewerView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class));

        billLister = new ListerActivityBase<BillDTO>(place, ((LeaseViewerView) view).getBillListerView(),
                (AbstractCrudService<BillDTO>) GWT.create(BillCrudService.class), BillDTO.class);

        paymentLister = new ListerActivityBase<PaymentRecord>(place, ((LeaseViewerView) view).getPaymentListerView(),
                (AbstractCrudService<PaymentRecord>) GWT.create(PaymentCrudService.class), PaymentRecord.class);
    }

    @Override
    public Presenter<BillDTO> getBillListerPresenter() {
        return billLister;
    }

    @Override
    public Presenter<PaymentRecord> getPaymentListerPresenter() {
        return paymentLister;
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);

        populateBills(currentValue = result);
        populatePayments(result);
    }

    protected void populateBills(LeaseDTO result) {
        List<PropertyCriterion> preDefinedFilters = new ArrayList<PropertyCriterion>();
        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(BillDTO.class).billingAccount().id(), result.billingAccount()
                .getPrimaryKey()));
        billLister.setPreDefinedFilters(preDefinedFilters);
        billLister.populate();
    }

    protected void populatePayments(LeaseDTO result) {
        List<PropertyCriterion> preDefinedFilters = new ArrayList<PropertyCriterion>();
        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(PaymentRecord.class).billingAccount().id(), result.billingAccount()
                .getPrimaryKey()));
        paymentLister.setPreDefinedFilters(preDefinedFilters);
        paymentLister.setParent(result.billingAccount().getPrimaryKey());
        paymentLister.populate();
    }

    // Actions:

    @Override
    public void startApplication() {
        ((LeaseCrudService) service).startApplication(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }

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
    public void applicationAction(LeaseApplicationActionDTO action) {
        ((LeaseCrudService) service).applicationAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, action);
    }

    @Override
    public void notice(LogicalDate date, LogicalDate moveOut) {
        ((LeaseCrudService) service).notice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, date, moveOut);
    }

    @Override
    public void cancelNotice() {
        ((LeaseCrudService) service).cancelNotice(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }

    @Override
    public void evict(LogicalDate date, LogicalDate moveOut) {
        ((LeaseCrudService) service).evict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, date, moveOut);
    }

    @Override
    public void cancelEvict() {
        ((LeaseCrudService) service).cancelEvict(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }

    @Override
    public void sendMail(List<Tenant> tenants, EmailTemplateType emailType) {
        ((LeaseCrudService) service).sendMail(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, new Vector<Tenant>(tenants), emailType);
    }
}
