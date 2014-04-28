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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
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
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationForm extends LeaseFormBase<LeaseApplicationDTO> {

    private Tab onlineStatusTab;

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

        if (VistaFeatures.instance().onlineApplication()) {
            onlineStatusTab = addTab(createOnlineStatusTab(), i18n.tr("Online Status Details"));
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().leaseApplication().applicationId()).setVisible(true);
        get(proto().leaseApplication().yardiApplicationId()).setVisible(VistaFeatures.instance().yardiIntegration());

        if (onlineStatusTab != null) {
            setTabVisible(onlineStatusTab, !getValue().leaseApplication().onlineApplication().isNull());
        }

        // show processing result:
        LeaseApplication.Status status = getValue().leaseApplication().status().getValue();

        get(proto().leaseApplication().decidedBy()).setVisible(status.isProcessed());
        get(proto().leaseApplication().decisionDate()).setVisible(status.isProcessed());
        get(proto().leaseApplication().decisionReason()).setVisible(status.isProcessed());
    }

    private TwoColumnFlexFormPanel createInfoTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        main.setWidget(0, 0, 2, inject(proto().tenantInfo(), createTenantView()));

        return main;
    }

    private TwoColumnFlexFormPanel createFinancialTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        main.setWidget(0, 0, 2, inject(proto().tenantFinancials(), createFinancialView()));

        return main;
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

    private TwoColumnFlexFormPanel createApprovalTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApplication().status(), new CEnumLabel(), 15, true));
        main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApplication().decidedBy(), 25, true));
        main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApplication().decisionDate(), 10, true));
        main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApplication().decisionReason(), 50, true));

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            main.setBR(++row, 0, 2);

            main.setH1(++row, 0, 2, i18n.tr("Credit Check"));
            main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApproval().percenrtageApproved(), 5, true));
            main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApproval().totalAmountApproved(), 10, true));
            main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApproval().rentAmount(), 10, true));

            main.setBR(++row, 0, 2);

            main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApproval().recomendedDecision(), 50, true));
        }

        main.setBR(++row, 0, 2);

        main.setWidget(++row, 0, 2,
                inject(proto().leaseApproval().participants(), new LeaseParticipanApprovalFolder(false, ((LeaseApplicationViewerView) getParentView()))));

        return main;
    }

    private TwoColumnFlexFormPanel createOnlineStatusTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, 2, injectAndDecorate(proto().leaseApplication().onlineApplication().status(), 20, 15, 20));
        main.setWidget(++row, 0, 2, injectAndDecorate(proto().masterApplicationStatus().progress(), 20, 5, 20));

        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, 2, inject(proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder()));

        return main;
    }

    private BasicFlexFormPanel createApplicationDocumentsTab() {
        int row = -1;
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        panel.setWidget(++row, 0, 2, inject(proto().applicationDocuments(), new LeaseApplicationDocumentFolder()));
        return panel;
    }
}