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
package com.propertyvista.crm.client.activity.crud.billing.transfer;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferViewerView;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;

public class AggregatedTransferViewerActivity extends CrmViewerActivity<AggregatedTransfer> implements AggregatedTransferViewerView.Presenter {

    private final ILister.Presenter<PaymentRecord> paymentLister;

    private final ILister.Presenter<PaymentRecord> returnedPaymentLister;

    private final ILister.Presenter<PaymentRecord> rejectedBatchPaymentsLister;

    public AggregatedTransferViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(AggregatedTransferViewerView.class), GWT
                .<AggregatedTransferCrudService> create(AggregatedTransferCrudService.class));

        paymentLister = new ListerController<PaymentRecord>(((AggregatedTransferViewerView) getView()).getPaymentsListerView(),
                GWT.<PaymentRecordListService> create(PaymentRecordListService.class), PaymentRecord.class);

        returnedPaymentLister = new ListerController<PaymentRecord>(((AggregatedTransferViewerView) getView()).getReturnedPaymentsListerView(),
                GWT.<PaymentRecordListService> create(PaymentRecordListService.class), PaymentRecord.class);

        rejectedBatchPaymentsLister = new ListerController<PaymentRecord>(((AggregatedTransferViewerView) getView()).getRejectedBatchPaymentsListerView(),
                GWT.<PaymentRecordListService> create(PaymentRecordListService.class), PaymentRecord.class);
    }

    @Override
    protected void onPopulateSuccess(AggregatedTransfer result) {
        super.onPopulateSuccess(result);

        paymentLister.clearPreDefinedFilters();
        paymentLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(PaymentRecord.class).aggregatedTransfer(), result));
        paymentLister.populate();

        returnedPaymentLister.clearPreDefinedFilters();
        returnedPaymentLister.addPreDefinedFilter(PropertyCriterion
                .eq(EntityFactory.getEntityPrototype(PaymentRecord.class).aggregatedTransferReturn(), result));
        returnedPaymentLister.populate();

        rejectedBatchPaymentsLister.clearPreDefinedFilters();
        rejectedBatchPaymentsLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(PaymentRecord.class).processing().$()
                .aggregatedTransfer(), result));
        rejectedBatchPaymentsLister.populate();
    }

    @Override
    public void cancelAction() {
        ((AggregatedTransferCrudService) getService()).cancelTransactions(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, EntityFactory.createIdentityStub(AggregatedTransfer.class, getEntityId()));
    }
}