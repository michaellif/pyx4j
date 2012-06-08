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
package com.propertyvista.crm.client.activity.crud.financial;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.financial.MerchantTransactionViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.FinancialViewFactory;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;

public class AggregatedTransferViewerActivity extends CrmViewerActivity<AggregatedTransfer> {

    private final IListerView.Presenter<PaymentRecord> paymentLister;

    private final IListerView.Presenter<PaymentRecord> returnedPaymentLister;

    @SuppressWarnings("unchecked")
    public AggregatedTransferViewerActivity(CrudAppPlace place) {
        super(place, FinancialViewFactory.instance(MerchantTransactionViewerView.class), GWT
                .<AggregatedTransferCrudService> create(AggregatedTransferCrudService.class));

        paymentLister = new ListerActivityBase<PaymentRecord>(place, ((MerchantTransactionViewerView) getView()).getPaymentsListerView(),
                GWT.<PaymentRecordListService> create(PaymentRecordListService.class), PaymentRecord.class);

        returnedPaymentLister = new ListerActivityBase<PaymentRecord>(place, ((MerchantTransactionViewerView) getView()).getReturnedPaymentsListerView(),
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
    }
}