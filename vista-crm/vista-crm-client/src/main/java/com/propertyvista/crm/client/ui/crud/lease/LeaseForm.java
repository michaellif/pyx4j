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
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.crm.client.ui.crud.lease.financial.TransactionHistoryViewer;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.EvictionCaseDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseForm extends LeaseFormBase<LeaseDTO> {

    private final Tab depositsTab, adjustmentsTab, chargesTab, billsTab, paymentsTab, financialTab, evictionHistoryTab;

    public LeaseForm(IPrimeFormView<LeaseDTO, ?> view) {
        super(LeaseDTO.class, view);

        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
        depositsTab = addTab(getParentView().getDepositLister().asWidget(), i18n.tr("Deposits"), DataModelPermission.permissionRead(DepositLifecycleDTO.class));
        adjustmentsTab = addTab(getParentView().getLeaseAdjustmentLister().asWidget(), i18n.tr("Adjustments"),
                DataModelPermission.permissionRead(LeaseAdjustment.class));
        chargesTab = addTab(createChargesTab(), i18n.tr("Charges"));
        billsTab = addTab(getParentView().getBillLister().asWidget(), i18n.tr("Bills"), DataModelPermission.permissionRead(BillDataDTO.class));
        paymentsTab = addTab(getParentView().getPaymentListerView().asWidget(), i18n.tr("Receipts"), DataModelPermission.permissionRead(PaymentRecordDTO.class));
        financialTab = addTab(createFinancialTransactionHistoryTab().asWidget(), i18n.tr("Financial Summary"),
                DataModelPermission.permissionRead(TransactionHistoryDTO.class));
        evictionHistoryTab = addTab(getParentView().getEvictionCaseLister().asWidget(), i18n.tr("Eviction History"),
                DataModelPermission.permissionRead(EvictionCaseDTO.class));
    }

    @Override
    public LeaseViewerView getParentView() {
        return (LeaseViewerView) super.getParentView();
    }

    @Override
    public void onReset() {
        super.onReset();
        // Yardi mode overrides:
        if (VistaFeatures.instance().yardiIntegration()) {
            setTabVisible(depositsTab, false);
            setTabVisible(adjustmentsTab, false);
            setTabVisible(chargesTab, false);
            setTabVisible(billsTab, false);
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().leaseId()).setVisible(true);

        // dynamic tabs visibility management:
        boolean isInternalMode = !VistaFeatures.instance().yardiIntegration();

        setTabVisible(depositsTab, isInternalMode && !getValue().status().getValue().isDraft());
        setTabVisible(adjustmentsTab, isInternalMode && !getValue().status().getValue().isDraft());
        setTabVisible(chargesTab, isInternalMode && getValue().status().getValue().isDraft() && !getValue().billingPreview().isNull());
        setTabVisible(billsTab, isInternalMode && !getValue().status().getValue().isDraft());

        setTabVisible(paymentsTab, !getValue().status().getValue().isDraft());
        setTabVisible(financialTab, !getValue().status().getValue().isDraft());
        setTabVisible(evictionHistoryTab, !getValue().status().getValue().isDraft());
    }

    private IsWidget createFinancialTransactionHistoryTab() {
        FormPanel formPanel = new FormPanel(this);

        CViewer<TransactionHistoryDTO> transactionHistoryViewer = VistaFeatures.instance().yardiIntegration() ? new TransactionHistoryViewerYardi()
                : new TransactionHistoryViewer();
        formPanel.append(Location.Dual, proto().transactionHistory(), transactionHistoryViewer);
        transactionHistoryViewer.asWidget().setWidth("100%");

        return formPanel;
    }
}