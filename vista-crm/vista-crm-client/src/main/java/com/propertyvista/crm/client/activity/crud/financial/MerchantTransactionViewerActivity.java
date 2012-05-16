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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.financial.MerchantTransactionViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.financial.MerchantTransactionCrudService;
import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.domain.financial.MerchantTransaction;
import com.propertyvista.domain.financial.PaymentRecord;

public class MerchantTransactionViewerActivity extends CrmViewerActivity<MerchantTransaction> {

    private final IListerView.Presenter<PaymentRecord> paymentLister;

    private final IListerView.Presenter<PaymentRecord> returnedPaymentLister;

    @SuppressWarnings("unchecked")
    public MerchantTransactionViewerActivity(CrudAppPlace place) {
        super(place, BuildingViewFactory.instance(MerchantTransactionViewerView.class), (AbstractCrudService<MerchantTransaction>) GWT
                .create(MerchantTransactionCrudService.class));

        paymentLister = new ListerActivityBase<PaymentRecord>(place, ((MerchantTransactionViewerView) getView()).getPaymentsListerView(),
                (AbstractCrudService<PaymentRecord>) GWT.create(PaymentRecordListService.class), PaymentRecord.class);

        returnedPaymentLister = new ListerActivityBase<PaymentRecord>(place, ((MerchantTransactionViewerView) getView()).getReturnedPaymentsListerView(),
                (AbstractCrudService<PaymentRecord>) GWT.create(PaymentRecordListService.class), PaymentRecord.class);
    }

    @Override
    protected void onPopulateSuccess(MerchantTransaction result) {
        super.onPopulateSuccess(result);

        paymentLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(PaymentRecord.class).merchantTransaction(), result));
        paymentLister.populate();

        returnedPaymentLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(PaymentRecord.class).merchantTransactionReturn(),
                result));
        returnedPaymentLister.populate();
    }
}