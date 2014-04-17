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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
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

        addTab(createInfoTab());
        if (!VistaFeatures.instance().yardiIntegration()) {
            chargesTab = addTab(createChargesTab());
        }
        addTab(((LeaseApplicationViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
        addTab(createFinancialTab());
        addTab(createApprovalTab());
        addTab(createApplicationDocumentsTab());

        if (VistaFeatures.instance().onlineApplication()) {
            onlineStatusTab = addTab(createOnlineStatusTab());
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

    @Override
    protected String getChargesTabTitle() {
        return i18n.tr("Potential Charges");
    }

    private TwoColumnFlexFormPanel createInfoTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Information"));

        main.setWidget(0, 0, 2, inject(proto().tenantInfo(), createTenantView()));

        return main;
    }

    private TwoColumnFlexFormPanel createFinancialTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Financial"));

        main.setWidget(0, 0, 2, inject(proto().tenantFinancials(), createFinancialView()));

        return main;
    }

    private CEntityFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {
            @Override
            public <T extends CComponent<T, ?>> T create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return (T) new InfoViewForm(true);
                }
                return super.create(member);
            }

            @Override
            public IFolderItemDecorator<TenantInfoDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantInfoDTO> decor = (BoxFolderItemDecorator<TenantInfoDTO>) super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        };
    }

    private CEntityFolder<TenantFinancialDTO> createFinancialView() {
        return new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {
            @Override
            public <T extends CComponent<T, ?>> T create(IObject<?> member) {
                if (member instanceof TenantFinancialDTO) {
                    return (T) new FinancialViewForm(true);
                }
                return super.create(member);
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
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Approval"));

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().leaseApplication().status(), new CEnumLabel(), new FieldDecoratorBuilder(15, true).build()));
        main.setWidget(++row, 0, 2, inject(proto().leaseApplication().decidedBy(), new FieldDecoratorBuilder(25, true).build()));
        main.setWidget(++row, 0, 2, inject(proto().leaseApplication().decisionDate(), new FieldDecoratorBuilder(10, true).build()));
        main.setWidget(++row, 0, 2, inject(proto().leaseApplication().decisionReason(), new FieldDecoratorBuilder(50, true).build()));

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            main.setBR(++row, 0, 2);

            main.setH1(++row, 0, 2, i18n.tr("Credit Check"));
            main.setWidget(++row, 0, 2, inject(proto().leaseApproval().percenrtageApproved(), new FieldDecoratorBuilder(5, true).build()));
            main.setWidget(++row, 0, 2, inject(proto().leaseApproval().totalAmountApproved(), new FieldDecoratorBuilder(10, true).build()));
            main.setWidget(++row, 0, 2, inject(proto().leaseApproval().rentAmount(), new FieldDecoratorBuilder(10, true).build()));

            main.setBR(++row, 0, 2);

            main.setWidget(++row, 0, 2, inject(proto().leaseApproval().suggestedDecision(), new FieldDecoratorBuilder(50, true).build()));
        }

        main.setBR(++row, 0, 2);

        main.setWidget(++row, 0, 2,
                inject(proto().leaseApproval().participants(), new LeaseParticipanApprovalFolder(false, ((LeaseApplicationViewerView) getParentView()))));

        return main;
    }

    private TwoColumnFlexFormPanel createOnlineStatusTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Online Status Details"));

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().leaseApplication().onlineApplication().status(), new FieldDecoratorBuilder(20, 15, 20).build()));
        main.setWidget(++row, 0, 2, inject(proto().masterApplicationStatus().progress(), new FieldDecoratorBuilder(20, 5, 20).build()));

        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, 2, inject(proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder()));

        return main;
    }

    private BasicFlexFormPanel createApplicationDocumentsTab() {
        int row = -1;
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Application Documents"));
        panel.setWidget(++row, 0, 2, inject(proto().applicationDocuments(), new LeaseApplicationDocumentFolder()));
        return panel;
    }
}