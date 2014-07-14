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

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.crm.client.ui.crud.lease.financial.TransactionHistoryViewer;
import com.propertyvista.crm.client.ui.crud.lease.legal.LegalLetterFolder;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseForm extends LeaseFormBase<LeaseDTO> {

    private final Tab depositsTab, adjustmentsTab, chargesTab, billsTab, paymentsTab, financialTab, maintenanceTab, communicationTab;

    public LeaseForm(IForm<LeaseDTO> view) {
        super(LeaseDTO.class, view);

        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
        depositsTab = addTab(((LeaseViewerView) getParentView()).getDepositListerView().asWidget(), i18n.tr("Deposits"));
        adjustmentsTab = addTab(((LeaseViewerView) getParentView()).getLeaseAdjustmentListerView().asWidget(), i18n.tr("Adjustments"));
        chargesTab = addTab(createChargesTab(), i18n.tr("Charges"));
        billsTab = addTab(((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));
        paymentsTab = addTab(((LeaseViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Receipts"));
        financialTab = addTab(createFinancialTransactionHistoryTab().asWidget(), i18n.tr("Financial Summary"));
        communicationTab = addTab(createCommunicationsTab(), i18n.tr("Documents/Communication"));
        maintenanceTab = addTab(((LeaseViewerView) getParentView()).getMaintenanceListerView().asWidget(), i18n.tr("Maintenance"));
    }

    @Override
    public void onReset() {
        super.onReset();

        // Static Tabs visibility (by permission):  
        depositsTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(DepositLifecycleDTO.class)));
        adjustmentsTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(LeaseAdjustment.class)));
        billsTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(BillDataDTO.class)));
        paymentsTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(PaymentRecordDTO.class)));
        financialTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(TransactionHistoryDTO.class)));
        communicationTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(LegalLetter.class)));
        maintenanceTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(MaintenanceRequestDTO.class)));
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
        setTabVisible(depositsTab, depositsTab.isTabVisible() && !getValue().status().getValue().isDraft());
        setTabVisible(adjustmentsTab, adjustmentsTab.isTabVisible() && !getValue().status().getValue().isDraft());
        setTabVisible(chargesTab, chargesTab.isTabVisible() && getValue().status().getValue().isDraft() && !getValue().billingPreview().isNull());
        setTabVisible(billsTab, billsTab.isTabVisible() && !getValue().status().getValue().isDraft());
        setTabVisible(paymentsTab, paymentsTab.isTabVisible() && !getValue().status().getValue().isDraft());
        setTabVisible(financialTab, financialTab.isTabVisible() && !getValue().status().getValue().isDraft());
    }

    private IsWidget createFinancialTransactionHistoryTab() {
        FormPanel formPanel = new FormPanel(this);

        CViewer<TransactionHistoryDTO> transactionHistoryViewer = VistaFeatures.instance().yardiIntegration() ? new TransactionHistoryViewerYardi()
                : new TransactionHistoryViewer();
        formPanel.append(Location.Dual, proto().transactionHistory(), transactionHistoryViewer);
        transactionHistoryViewer.asWidget().setWidth("100%");

        return formPanel;
    }

    private IsWidget createCommunicationsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().letters(), new LegalLetterFolder());
        return formPanel;
    }
}