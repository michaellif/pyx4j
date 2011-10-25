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
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceEditorForm extends CrmEntityForm<Service> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public ServiceEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public ServiceEditorForm(IEditableComponentFactory factory) {
        super(Service.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createEligibilityTab(), i18n.tr("Eligibility"));

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

    public IsWidget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CLabel()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 57).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setHeader(++row, 0, 2, i18n.tr("Items"));
        main.setWidget(++row, 0, inject(proto().items(), new ServiceItemFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().depositType()), 15).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    public IsWidget createEligibilityTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setHeader(++row, 0, 1, i18n.tr("Features"));
        main.setWidget(
                ++row,
                0,
                inject(proto().features(), new ServiceFeatureFolder(isEditable(), isEditable() ? ((ServiceEditorView) getParentView()).getFeatureListerView()
                        : null)));

        main.setHeader(++row, 0, 1, i18n.tr("Concessions"));
        main.setWidget(
                ++row,
                0,
                inject(proto().concessions(),
                        new ServiceConcessionFolder(isEditable(), isEditable() ? ((ServiceEditorView) getParentView()).getConcessionListerView() : null)));

        return new CrmScrollPanel(main);
    }
}