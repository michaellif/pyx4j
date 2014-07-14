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
package com.propertyvista.crm.client.ui.crud.lease.application;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.ApplicationStatusFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.FinancialViewForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.InfoViewForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.LeaseApplicationDocumentFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationForm extends LeaseFormBase<LeaseApplicationDTO> {

    private FormPanel onlineStatusPanel;

    private final Tab paymentsTab, financialTab, applicationDocumentsTab;

    public LeaseApplicationForm(IForm<LeaseApplicationDTO> view) {
        super(LeaseApplicationDTO.class, view);

        createCommonContent();

        addTab(createInfoTab(), i18n.tr("Information"));
        if (!VistaFeatures.instance().yardiIntegration()) {
            chargesTab = addTab(createChargesTab(), i18n.tr("Potential Charges"));
        }
        paymentsTab = addTab(((LeaseApplicationViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
        financialTab = addTab(createFinancialTab(), i18n.tr("Financial"));
        addTab(createApprovalTab(), i18n.tr("Approval"));
        applicationDocumentsTab = addTab(createApplicationDocumentsTab(), i18n.tr("Application Documents"));
    }

    @Override
    public void onReset() {
        super.onReset();

        // Tabs visibility by permission:  
        paymentsTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(PaymentRecordDTO.class)));
        financialTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(TenantFinancialDTO.class)));
        applicationDocumentsTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(LeaseApplicationDocument.class)));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().leaseApplication().applicationId()).setVisible(true);
        get(proto().leaseApplication().yardiApplicationId()).setVisible(VistaFeatures.instance().yardiIntegration());

        if (onlineStatusPanel != null) {
            onlineStatusPanel.setVisible(!getValue().leaseApplication().onlineApplication().isNull());
        }

        // show processing result:
        LeaseApplication.Status status = getValue().leaseApplication().status().getValue();

        get(proto().leaseApplication().decidedBy()).setVisible(status.isProcessed());
        get(proto().leaseApplication().decisionDate()).setVisible(status.isProcessed());
        get(proto().leaseApplication().decisionReason()).setVisible(status.isProcessed());
    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, inject(proto().tenantInfo(), createTenantView()));
        return formPanel;
    }

    private IsWidget createFinancialTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, inject(proto().tenantFinancials(), createFinancialView()));
        return formPanel;
    }

    private CFolder<TenantInfoDTO> createTenantView() {
        VistaBoxFolder<TenantInfoDTO> folder = new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {
            @Override
            protected CForm<TenantInfoDTO> createItemForm(IObject<?> member) {
                return new InfoViewForm(true);
            }

            @Override
            public IFolderItemDecorator<TenantInfoDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantInfoDTO> decor = (BoxFolderItemDecorator<TenantInfoDTO>) super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        };
        folder.setNoDataLabel(i18n.tr("No tenants data has been entered yet. Navigate Views->Tenants/Guarantors to view/edit"));
        return folder;
    }

    private CFolder<TenantFinancialDTO> createFinancialView() {
        VistaBoxFolder<TenantFinancialDTO> folder = new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {
            @Override
            protected CForm<TenantFinancialDTO> createItemForm(IObject<?> member) {
                return new FinancialViewForm(true);
            }

            @Override
            public IFolderItemDecorator<TenantFinancialDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantFinancialDTO> decor = (BoxFolderItemDecorator<TenantFinancialDTO>) super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        };
        folder.setNoDataLabel(i18n.tr("No financial data has been entered yet. Navigate Views->Tenants/Guarantors to view/edit"));
        return folder;
    }

    private IsWidget createApprovalTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().leaseApplication().status(), new CEnumLabel()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().leaseApplication().decidedBy()).decorate();
        formPanel.append(Location.Left, proto().leaseApplication().decisionDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().leaseApplication().decisionReason()).decorate();

        if (VistaFeatures.instance().onlineApplication()) {
            formPanel.append(Location.Dual, onlineStatusPanel = createOnlineStatusPanel());
        }

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            formPanel.br();

            formPanel.h1(i18n.tr("Credit Check"));
            formPanel.append(Location.Left, proto().leaseApproval().percenrtageApproved()).decorate().componentWidth(80);
            formPanel.append(Location.Left, proto().leaseApproval().totalAmountApproved()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().leaseApproval().rentAmount()).decorate().componentWidth(120);

            formPanel.br();

            formPanel.append(Location.Left, proto().leaseApproval().recommendedDecision()).decorate();
        }

        formPanel.br();

        formPanel.append(Location.Dual, proto().leaseApproval().participants(), new LeaseParticipanApprovalFolder(false,
                ((LeaseApplicationViewerView) getParentView())));

        return formPanel;
    }

    private FormPanel createOnlineStatusPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h2(i18n.tr("Online Status Details"));
        formPanel.append(Location.Left, proto().leaseApplication().onlineApplication().status()).decorate();
        formPanel.append(Location.Left, proto().masterApplicationStatus().progress()).decorate();

        formPanel.br();
        formPanel.append(Location.Dual, proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder());

        return formPanel;
    }

    private IsWidget createApplicationDocumentsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().applicationDocuments(), new LeaseApplicationDocumentFolder());
        return formPanel;
    }
}