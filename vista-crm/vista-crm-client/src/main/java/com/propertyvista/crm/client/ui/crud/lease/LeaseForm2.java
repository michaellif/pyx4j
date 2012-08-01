/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase2;
import com.propertyvista.crm.client.ui.crud.lease.invoice.TransactionHistoryViewer;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseForm2 extends LeaseFormBase2<LeaseDTO2> {

    private Tab adjustmentsTab, billsTab, paymentsTab, financialTab;

    public LeaseForm2() {
        super(LeaseDTO2.class);
    }

    @Override
    public void createTabs() {

        createCommonContent();

        adjustmentsTab = addTab(((LeaseViewerView) getParentView()).getLeaseAdjustmentListerView().asWidget(), i18n.tr("Adjustments"));
        billsTab = addTab(((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));
        paymentsTab = addTab(((LeaseViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
        financialTab = addTab(createFinancialTransactionHistoryTab().asWidget(), i18n.tr("Financial Summary"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setTabVisible(adjustmentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(billsTab, !getValue().status().getValue().isDraft());
        setTabVisible(paymentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(financialTab, !getValue().status().getValue().isDraft());
    }

    private IsWidget createFinancialTransactionHistoryTab() {
        FormFlexPanel financialTransactionHistory = new FormFlexPanel();
        financialTransactionHistory.setWidget(0, 0, inject(proto().transactionHistory(), new TransactionHistoryViewer()));
        return financialTransactionHistory;
    }
}