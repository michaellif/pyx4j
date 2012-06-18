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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.crm.client.ui.crud.lease.invoice.TransactionHistoryViewer;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseForm extends LeaseFormBase<LeaseDTO> {

    private Widget adjustmentsTab, billsTab, paymentsTab, financialTab;

    public LeaseForm() {
        this(false);
    }

    public LeaseForm(boolean viewMode) {
        super(LeaseDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        createCommonContent();

        if (!VistaTODO.removedForProduction) {
            tabPanel.add(adjustmentsTab = isEditable() ? new HTML() : ((LeaseViewerView) getParentView()).getLeaseAdjustmentListerView().asWidget(),
                    i18n.tr("Adjustments"));
            tabPanel.setLastTabDisabled(isEditable());
            tabPanel.add(billsTab = isEditable() ? new HTML() : ((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));
            tabPanel.setLastTabDisabled(isEditable());
            tabPanel.add(paymentsTab = isEditable() ? new HTML() : ((LeaseViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
            tabPanel.setLastTabDisabled(isEditable());
            tabPanel.add(financialTab = isEditable() ? new HTML() : createFinancialTransactionHistoryTab().asWidget(), i18n.tr("Financial Summary"));
            tabPanel.setLastTabDisabled(isEditable());
        }

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        tabPanel.setTabVisible(adjustmentsTab, !getValue().version().status().getValue().isDraft());
        tabPanel.setTabVisible(billsTab, !getValue().version().status().getValue().isDraft());
        tabPanel.setTabVisible(paymentsTab, !getValue().version().status().getValue().isDraft());
        tabPanel.setTabVisible(financialTab, !getValue().version().status().getValue().isDraft());

        if (!isEditable()) {
            ((LeaseViewerView) getParentView()).getLeaseAdjustmentListerView().getLister().getDataTablePanel().getAddButton()
                    .setEnabled(getValue().version().status().getValue() == Status.Closed);
            ((LeaseViewerView) getParentView()).getPaymentListerView().getLister().getDataTablePanel().getAddButton()
                    .setEnabled(getValue().version().status().getValue() == Status.Closed);
        }
    }

    private IsWidget createFinancialTransactionHistoryTab() {
        FormFlexPanel financialTransactionHistory = new FormFlexPanel();
        financialTransactionHistory.setWidget(0, 0, inject(proto().transactionHistory(), new TransactionHistoryViewer()));
        return new ScrollPanel(financialTransactionHistory);
    }
}