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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.common.client.ui.components.folders.ApplicationStatusFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseFormBase;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationForm extends LeaseFormBase<LeaseApplicationDTO> {

    private Tab onlineStatusTab;

    public LeaseApplicationForm() {
        this(false);
    }

    public LeaseApplicationForm(boolean viewMode) {
        super(LeaseApplicationDTO.class, viewMode);
    }

    @Override
    public void createTabs() {

        createCommonContent();

        Tab tab = addTab(createInfoTab(), i18n.tr("Information"));
        setTabEnabled(tab, !isEditable());

        tab = addTab(createFinancialTab(), i18n.tr("Financial"));
        setTabEnabled(tab, !isEditable());

// TODO : credit check (Equifax) isn't implemented yet (see LeaseApplicationViewerViewImpl)!        
//      tabPanel.add(createApprovalTab(), i18n.tr("Approval"));
//        tabPanel.setLastTabDisabled(true);

        if (VistaFeatures.instance().onlineApplication()) {
            onlineStatusTab = addTab(createOnlineStatusTab(), i18n.tr("Online Status Details"));
            setTabEnabled(onlineStatusTab, !isEditable());
        }
    }

    @Override
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

        if (VistaFeatures.instance().onlineApplication()) {
            setTabEnabled(onlineStatusTab, isTabEnabled(onlineStatusTab) || getValue().leaseApplication().onlineApplication().isNull());
        }
    }

    private Widget createInfoTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantInfo(), createTenantView()));

        return main;
    }

    private Widget createFinancialTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantFinancials(), createFinancialView()));

        return main;
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

        return main;
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

        return main;
    }
}