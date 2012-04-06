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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.common.client.ui.components.folders.ApplicationStatusFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.OnlineMasterApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class OnlineMasterApplicationEditorForm extends CrmEntityForm<OnlineMasterApplicationDTO> {

    private static final I18n i18n = I18n.get(OnlineMasterApplicationEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public OnlineMasterApplicationEditorForm() {
        this(false);
    }

    public OnlineMasterApplicationEditorForm(boolean viewMode) {
        super(OnlineMasterApplicationDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("Details"));
        tabPanel.add(isEditable() ? new HTML() : ((OnlineMasterApplicationViewerView) getParentView()).getTenantsView().asWidget(), i18n.tr("Tenants"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(createInfoTab(), i18n.tr("Information"));
        tabPanel.add(createFinancialTab(), i18n.tr("Financial"));
// TODO: should be hidden untill back end implementation:   
//        tabPanel.add(createApprovalTab(), i18n.tr("Approval"));
        tabPanel.add(createAppStatustab(), i18n.tr("Status Details"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(
                inject(proto().lease(), new CEntityCrudHyperlink<Lease>(MainActivityMapper.getCrudAppPlace(Lease.class))), 15).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mainApplicant(), new CEntityLabel<Tenant>()), 25).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfOccupants()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfCoApplicants()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfGuarantors()), 5).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createDate(), new CDateLabel()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).build());

// TODO Hide these data for the 1st version!        
//        row = -1;
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().rentPrice()), 5).build());
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().parkingPrice()), 5).build());
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().otherPrice()), 5).build());
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().deposit()), 5).build());
//
//        main.setBR(++row, 1, 1);
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().discounts()), 5).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    private Widget createApprovalTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().decidedBy()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().decisionDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().decisionReason()), 40).build());
        main.setHR(++row, 0, 1);

        main.setBR(++row, 0, 1);
        main.setH1(++row, 0, 1, i18n.tr("Equifax check results"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxApproval().percenrtageApproved()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxApproval().suggestedDecision()), 25).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().tenantFinancials(), new TenantApprovalFolder(isEditable())));

        return new CrmScrollPanel(main);
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

    private Widget createAppStatustab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
//        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).labelWidth(20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().masterApplicationStatus().progress()), 5).labelWidth(20).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder(isEditable())));

        return new CrmScrollPanel(main);
    }

    private CEntityFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return new InfoViewForm();
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
                    return new FinancialViewForm();
                }
                return super.create(member);
            }
        };
    }
}