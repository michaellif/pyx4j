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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.crm.client.ui.crud.lease.common.LegalStatusHistoryFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.N4LegalLetterFolder;
import com.propertyvista.crm.client.ui.crud.lease.invoice.TransactionHistoryViewer;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseForm extends LeaseFormBase<LeaseDTO> {

    private final Tab depositsTab, adjustmentsTab, billsTab, paymentsTab, financialTab, maintenanceTab, communicationTab, legalTab;

    public LeaseForm(IForm<LeaseDTO> view) {
        super(LeaseDTO.class, view);

        createCommonContent();

        depositsTab = addTab(((LeaseViewerView) getParentView()).getDepositListerView().asWidget(), i18n.tr("Deposits"));
        adjustmentsTab = addTab(((LeaseViewerView) getParentView()).getLeaseAdjustmentListerView().asWidget(), i18n.tr("Adjustments"));
        billsTab = addTab(((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));
        paymentsTab = addTab(((LeaseViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
        financialTab = addTab(createFinancialTransactionHistoryTab().asWidget(), i18n.tr("Financial Summary"));
        legalTab = addTab(createLegalTab().asWidget(), i18n.tr("Legal"));
        communicationTab = addTab(createCommunicationsTab(), i18n.tr("Documents/Communication"));
        maintenanceTab = addTab(((LeaseViewerView) getParentView()).getMaintenanceListerView().asWidget(), i18n.tr("Maintenance"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setTabVisible(depositsTab, !getValue().status().getValue().isDraft());
        setTabVisible(adjustmentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(billsTab, !getValue().status().getValue().isDraft() && SecurityController.checkBehavior(VistaCrmBehavior.Billing));
        setTabVisible(paymentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(financialTab, !getValue().status().getValue().isDraft());

        if (VistaFeatures.instance().yardiIntegration()) {
            setTabVisible(depositsTab, false);
            setTabVisible(adjustmentsTab, false);
            setTabVisible(billsTab, false);
        }
    }

    private IsWidget createFinancialTransactionHistoryTab() {
        TwoColumnFlexFormPanel financialTransactionHistory = new TwoColumnFlexFormPanel();
        int row = -1;

        CViewer<TransactionHistoryDTO> transactionHistoryViewer = VistaFeatures.instance().yardiIntegration() ? new TransactionHistoryViewerYardi()
                : new TransactionHistoryViewer();
        financialTransactionHistory.setWidget(++row, 0, 2, inject(proto().transactionHistory(), transactionHistoryViewer));

        return financialTransactionHistory;
    }

    private Widget createLegalTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().legalStatusHistory(), new LegalStatusHistoryFolder()));
        return panel;
    }

    private Widget createCommunicationsTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().letters(), new N4LegalLetterFolder()));
        return panel;
    }
}