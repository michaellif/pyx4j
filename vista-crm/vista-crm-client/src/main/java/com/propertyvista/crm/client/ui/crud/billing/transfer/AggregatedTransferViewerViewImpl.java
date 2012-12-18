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
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.PaymentRecord;

public class AggregatedTransferViewerViewImpl extends CrmViewerViewImplBase<AggregatedTransfer> implements AggregatedTransferViewerView {

    private static final I18n i18n = I18n.get(AggregatedTransferViewerViewImpl.class);

    private final IListerView<PaymentRecord> paymentLister;

    private final IListerView<PaymentRecord> returnedPaymentLister;

    private final IListerView<PaymentRecord> rejectedBatchPaymentsLister;

    private final MenuItem cancelAction;

    public AggregatedTransferViewerViewImpl() {
        super(CrmSiteMap.Finance.AggregatedTransfer.class, true);

        paymentLister = new ListerInternalViewImplBase<PaymentRecord>(new PaymentRecordLister());
        returnedPaymentLister = new ListerInternalViewImplBase<PaymentRecord>(new PaymentRecordLister());
        rejectedBatchPaymentsLister = new ListerInternalViewImplBase<PaymentRecord>(new PaymentRecordLister());

        setForm(new AggregatedTransferForm(this));

        // Actions:
        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Cancel"), i18n.tr("Do you really want to cancel the transaction?"), new Command() {
                    @Override
                    public void execute() {
                        ((AggregatedTransferViewerView.Presenter) getPresenter()).cancelAction();
                    }
                });
            }
        });
        addAction(cancelAction);
    }

    @Override
    public void reset() {
        setActionVisible(cancelAction, false);
        super.reset();
    }

    @Override
    public void populate(AggregatedTransfer value) {
        super.populate(value);

        setActionVisible(cancelAction, value.status().getValue() == AggregatedTransferStatus.Rejected
                && value.status().getValue() != AggregatedTransferStatus.Canceled);
    }

    @Override
    public IListerView<PaymentRecord> getPaymentsListerView() {
        return paymentLister;
    }

    @Override
    public IListerView<PaymentRecord> getReturnedPaymentsListerView() {
        return returnedPaymentLister;
    }

    @Override
    public IListerView<PaymentRecord> getRejectedBatchPaymentsListerView() {
        return rejectedBatchPaymentsLister;
    }
}