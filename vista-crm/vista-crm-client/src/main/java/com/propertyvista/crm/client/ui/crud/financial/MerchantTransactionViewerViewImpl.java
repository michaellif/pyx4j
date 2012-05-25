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
package com.propertyvista.crm.client.ui.crud.financial;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;

public class MerchantTransactionViewerViewImpl extends CrmViewerViewImplBase<AggregatedTransfer> implements MerchantTransactionViewerView {

    private final IListerView<PaymentRecord> paymentLister;

    private final IListerView<PaymentRecord> returnedPaymentLister;

    public MerchantTransactionViewerViewImpl() {
        super(CrmSiteMap.Finance.AggregatedTransfer.class, true);

        paymentLister = new ListerInternalViewImplBase<PaymentRecord>(new PaymentRecordLister());
        returnedPaymentLister = new ListerInternalViewImplBase<PaymentRecord>(new PaymentRecordLister());

        setForm(new MerchantTransactionForm(true));
    }

    @Override
    public IListerView<PaymentRecord> getPaymentsListerView() {
        return paymentLister;
    }

    @Override
    public IListerView<PaymentRecord> getReturnedPaymentsListerView() {
        return returnedPaymentLister;
    }
}