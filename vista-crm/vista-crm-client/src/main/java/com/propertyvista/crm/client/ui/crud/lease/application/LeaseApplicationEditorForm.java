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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.common.client.ui.components.folders.ApplicationStatusFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseEditorFormBase;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class LeaseApplicationEditorForm extends LeaseEditorFormBase<LeaseApplicationDTO> {

    private Widget onlineStatusTab;

    public LeaseApplicationEditorForm() {
        this(false);
    }

    public LeaseApplicationEditorForm(boolean viewMode) {
        super(LeaseApplicationDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        createCommonContent();

        tabPanel.add(createInfoTab(), i18n.tr("Information"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(createFinancialTab(), i18n.tr("Financial"));
        tabPanel.setLastTabDisabled(isEditable());

// TODO: should be hidden until back end implementation:   
//      tabPanel.add(createApprovalTab(), i18n.tr("Approval"));
//        tabPanel.setLastTabDisabled(true);
        tabPanel.add(onlineStatusTab = createOnlineStatusTab(), i18n.tr("Online Status Details"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        tabPanel.setTabDisabled(onlineStatusTab, getValue().leaseApplication().onlineApplication().isNull());
    }

    private Widget createInfoTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantInfo(), createTenantView()));

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantFinancials(), createFinancialView()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return new InfoViewForm(true);
                }
                return super.create(member);
            }
        };
    }

    private CEntityFolder<TenantFinancialDTO> createFinancialView() {
        return new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantFinancialDTO) {
                    return new FinancialViewForm(true);
                }
                return super.create(member);
            }
        };
    }

    private Widget createApprovalTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().status(), new CEnumLabel()), 15).build());
//        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().decidedBy()), 25).build());
//        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().decisionDate()), 9).build());
//        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().decisionReason()), 40).build());
        main.setHR(++row, 0, 1);

        main.setBR(++row, 0, 1);
        main.setH1(++row, 0, 1, i18n.tr("Equifax check results"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().equifaxApproval().percenrtageApproved()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().equifaxApproval().suggestedDecision()), 25).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().tenantFinancials(), new TenantApprovalFolder(isEditable())));

        return new CrmScrollPanel(main);
    }

    private Widget createOnlineStatusTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseApplication().onlineApplication().status()), 15).labelWidth(20).build());
        get(proto().leaseApplication().onlineApplication().status()).setViewable(true);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().masterApplicationStatus().progress()), 5).labelWidth(20).build());
        get(proto().masterApplicationStatus().progress()).setViewable(true);

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder()));

        return new CrmScrollPanel(main);
    }
}