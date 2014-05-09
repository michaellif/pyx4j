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
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.ApplicationStatusFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.FinancialViewForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.InfoViewForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.LeaseApplicationDocumentFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationForm extends LeaseFormBase<LeaseApplicationDTO> {

    private BasicCFormPanel onlineStatusPanel;

    public LeaseApplicationForm(IForm<LeaseApplicationDTO> view) {
        super(LeaseApplicationDTO.class, view);

        createCommonContent();

        addTab(createInfoTab(), i18n.tr("Information"));
        if (!VistaFeatures.instance().yardiIntegration()) {
            chargesTab = addTab(createChargesTab(), i18n.tr("Potential Charges"));
        }
        addTab(((LeaseApplicationViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
        addTab(createFinancialTab(), i18n.tr("Financial"));
        addTab(createApprovalTab(), i18n.tr("Approval"));
        addTab(createApplicationDocumentsTab(), i18n.tr("Application Documents"));
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
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Dual, inject(proto().tenantInfo(), createTenantView()));
        return formPanel;
    }

    private IsWidget createFinancialTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Dual, inject(proto().tenantFinancials(), createFinancialView()));
        return formPanel;
    }

    private CFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {

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
    }

    private CFolder<TenantFinancialDTO> createFinancialView() {
        return new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {

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
    }

    private IsWidget createApprovalTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

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

    private BasicCFormPanel createOnlineStatusPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.h2(i18n.tr("Online Status Details"));
        formPanel.append(Location.Left, proto().leaseApplication().onlineApplication().status()).decorate();
        formPanel.append(Location.Left, proto().masterApplicationStatus().progress()).decorate();

        formPanel.br();
        formPanel.append(Location.Dual, proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder());

        return formPanel;
    }

    private IsWidget createApplicationDocumentsTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Dual, proto().applicationDocuments(), new LeaseApplicationDocumentFolder());
        return formPanel;
    }
}