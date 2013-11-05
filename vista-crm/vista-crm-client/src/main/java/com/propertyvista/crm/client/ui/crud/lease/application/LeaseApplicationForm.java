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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.common.client.ui.components.folders.ApplicationStatusFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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
        addTab(createFinancialTab());
        addTab(createApprovalTab());

        if (VistaFeatures.instance().onlineApplication()) {
            onlineStatusTab = addTab(createOnlineStatusTab());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (VistaFeatures.instance().onlineApplication()) {
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
            public CComponent<?> create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return new InfoViewForm(true);
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
            public CComponent<?> create(IObject<?> member) {
                if (member instanceof TenantFinancialDTO) {
                    return new FinancialViewForm(true);
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
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApplication().status(), new CEnumLabel()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApplication().decidedBy()), 25).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApplication().decisionDate()), 10).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApplication().decisionReason()), 50).build());

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            main.setBR(++row, 0, 2);

            main.setH1(++row, 0, 2, i18n.tr("Credit Check"));
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApproval().percenrtageApproved()), 5).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApproval().totalAmountApproved()), 10).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApproval().rentAmount()), 10).build());

            main.setBR(++row, 0, 2);

            main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().leaseApproval().suggestedDecision()), 50, true).build());
        }

        main.setBR(++row, 0, 0);

        main.setWidget(++row, 0, 2,
                inject(proto().leaseApproval().participants(), new LeaseParticipanApprovalFolder(false, ((LeaseApplicationViewerView) getParentView()))));

        return main;
    }

    private TwoColumnFlexFormPanel createOnlineStatusTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Online Status Details"));

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseApplication().onlineApplication().status()), 15).labelWidth(20).build());
        get(proto().leaseApplication().onlineApplication().status()).setViewable(true);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().masterApplicationStatus().progress()), 5).labelWidth(20).build());
        get(proto().masterApplicationStatus().progress()).setViewable(true);

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder()));

        return main;
    }
}