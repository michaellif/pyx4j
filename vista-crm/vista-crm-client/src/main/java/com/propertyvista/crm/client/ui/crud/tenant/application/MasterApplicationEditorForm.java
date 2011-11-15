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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class MasterApplicationEditorForm extends CrmEntityForm<MasterApplicationDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public MasterApplicationEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public MasterApplicationEditorForm(IEditableComponentFactory factory) {
        super(MasterApplicationDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("Details"));
        tabPanel.add(createInfoTab(), i18n.tr("Info"));
        tabPanel.add(createFinancilaTab(), i18n.tr("Financial"));

        tabPanel.setDisableMode(isEditable());
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
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().leaseID()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().type()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().status(), new CEnumLabel()), 15).build());

        main.setWidget(++row, 0, new HTML("&nbsp"));

        HorizontalPanel leaseDatePanel = new HorizontalPanel();
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().lease().leaseFrom()), 8).build());
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().lease().leaseTo()), 8).labelWidth(10).build());
        main.setWidget(++row, 0, leaseDatePanel);

        main.setWidget(++row, 0, new HTML("&nbsp"));

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().unit().belongsTo()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().unit()), 20).build());

        main.setWidget(++row, 0, new HTML("&nbsp"));

        leaseDatePanel = new HorizontalPanel();
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().lease().expectedMoveIn()), 8).build());
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().lease().expectedMoveOut()), 8).labelWidth(10).build());
        main.setWidget(++row, 0, leaseDatePanel);

        leaseDatePanel = new HorizontalPanel();
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().lease().actualMoveIn()), 8).build());
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().lease().actualMoveOut()), 8).labelWidth(10).build());
        main.setWidget(++row, 0, leaseDatePanel);

        main.setWidget(++row, 0, new HTML("&nbsp"));

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().signDate()), 8).build());

        return new CrmScrollPanel(main);
    }

    private Widget createInfoTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantsWithInfo(), createTenantView()));

        return new CrmScrollPanel(main);
    }

    private Widget createFinancilaTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantFinancials(), createFinancialView()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return new InfoViewForm(new VistaViewersComponentFactory());
                }
                return super.create(member);
            }

            @Override
            public IFolderItemDecorator<TenantInfoDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantInfoDTO> decorator = (BoxFolderItemDecorator<TenantInfoDTO>) super.createItemDecorator();
                decorator.setExpended(false);
                return decorator;
            }

        };
    }

    private CEntityFolder<TenantFinancialDTO> createFinancialView() {
        return new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantFinancialDTO) {
                    return new FinancialViewForm(new VistaViewersComponentFactory());
                }
                return super.create(member);
            }

            @Override
            public IFolderItemDecorator<TenantFinancialDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantFinancialDTO> decorator = (BoxFolderItemDecorator<TenantFinancialDTO>) super.createItemDecorator();
                decorator.setExpended(false);
                return decorator;
            }
        };
    }
}