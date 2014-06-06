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

import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.crm.client.ui.crud.lease.invoice.TransactionHistoryViewer;
import com.propertyvista.crm.client.ui.crud.lease.legal.LegalLetterFolder;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseForm extends LeaseFormBase<LeaseDTO> {

    private final Tab depositsTab, adjustmentsTab, billsTab, paymentsTab, financialTab, maintenanceTab, communicationTab;

    public LeaseForm(IForm<LeaseDTO> view) {
        super(LeaseDTO.class, view);

        createCommonContent();

        depositsTab = addTab(((LeaseViewerView) getParentView()).getDepositListerView().asWidget(), i18n.tr("Deposits"));
        adjustmentsTab = addTab(((LeaseViewerView) getParentView()).getLeaseAdjustmentListerView().asWidget(), i18n.tr("Adjustments"));
        if (!VistaFeatures.instance().yardiIntegration()) {
            chargesTab = addTab(createChargesTab(), i18n.tr("Charges"));
        }
        billsTab = addTab(((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));
        paymentsTab = addTab(((LeaseViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Receipts"));
        financialTab = addTab(createFinancialTransactionHistoryTab().asWidget(), i18n.tr("Financial Summary"));
        communicationTab = addTab(createCommunicationsTab(), i18n.tr("Documents/Communication"));
        maintenanceTab = addTab(((LeaseViewerView) getParentView()).getMaintenanceListerView().asWidget(), i18n.tr("Maintenance"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().leaseId()).setVisible(true);

        setTabVisible(depositsTab, !getValue().status().getValue().isDraft());
        setTabVisible(adjustmentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(billsTab, !getValue().status().getValue().isDraft() && SecurityController.checkBehavior(VistaCrmBehavior.Billing_OLD));
        setTabVisible(paymentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(financialTab, !getValue().status().getValue().isDraft());

        if (VistaFeatures.instance().yardiIntegration()) {
            setTabVisible(depositsTab, false);
            setTabVisible(adjustmentsTab, false);
            setTabVisible(billsTab, false);
        }
    }

    private IsWidget createFinancialTransactionHistoryTab() {
        FormPanel formPanel = new FormPanel(this);

        CViewer<TransactionHistoryDTO> transactionHistoryViewer = VistaFeatures.instance().yardiIntegration() ? new TransactionHistoryViewerYardi()
                : new TransactionHistoryViewer();
        formPanel.append(Location.Dual, proto().transactionHistory(), transactionHistoryViewer);

        return formPanel;
    }

    private IsWidget createCommunicationsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().letters(), new LegalLetterFolder());
        return formPanel;
    }
}