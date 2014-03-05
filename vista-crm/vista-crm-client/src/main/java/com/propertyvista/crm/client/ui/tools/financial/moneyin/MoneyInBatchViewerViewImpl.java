/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.forms.MoneyInBatchForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchViewerViewImpl extends CrmViewerViewImplBase<MoneyInBatchDTO> implements MoneyInBatchViewerView {

    private static final I18n i18n = I18n.get(MoneyInBatchViewerViewImpl.class);

    private Button postButton;

    private Button cancelBatchButton;

    public MoneyInBatchViewerViewImpl() {
        setBreadcrumbsBar(null);
        setForm(new MoneyInBatchForm(this) {
            @Override
            public void onShowToPaymentRecord(Key paymentRecordId) {
                MoneyInBatchViewerViewImpl.this.showPaymentRecord(paymentRecordId);
            }
        });

        addHeaderToolbarItem(new Button(i18n.tr("Create Deposit Slip..."), new Command() {
            @Override
            public void execute() {
                MoneyInBatchViewerViewImpl.this.createDepositSlip();
            }
        }));
        addHeaderToolbarItem(postButton = new Button(i18n.tr("Post..."), new Command() {
            @Override
            public void execute() {
                MoneyInBatchViewerViewImpl.this.post();
            }
        }));
        addHeaderToolbarItem(cancelBatchButton = new Button(i18n.tr("Cancel Batch..."), new Command() {
            @Override
            public void execute() {
                MoneyInBatchViewerViewImpl.this.cancelBatch();
            }
        }));
    }

    @Override
    public void populate(MoneyInBatchDTO value) {
        super.populate(value);
        postButton.setVisible(((MoneyInBatchViewerView.Presenter) getPresenter()).canPost());
        cancelBatchButton.setVisible(((MoneyInBatchViewerView.Presenter) getPresenter()).canCancel());
    }

    @Override
    protected void populateBreadcrumbs(MoneyInBatchDTO value) {
        // we don't have breadcrumbs for this view
    }

    private void createDepositSlip() {
        ((MoneyInBatchViewerView.Presenter) getPresenter()).createDownloadableDepositSlipPrintout();
    }

    private void post() {
        ((MoneyInBatchViewerView.Presenter) getPresenter()).postBatch();
    }

    private void cancelBatch() {
        ((MoneyInBatchViewerView.Presenter) getPresenter()).cancelBatch();
    }

    private void showPaymentRecord(Key paymentRecordId) {
        ((MoneyInBatchViewerView.Presenter) getPresenter()).showPaymentRecord(paymentRecordId);
    }

}
