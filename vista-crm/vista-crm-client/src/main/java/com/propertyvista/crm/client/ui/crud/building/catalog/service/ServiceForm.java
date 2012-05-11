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
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceForm extends CrmEntityForm<Service> {

    private static final I18n i18n = I18n.get(ServiceForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public ServiceForm() {
        this(false);
    }

    public ServiceForm(boolean viewMode) {
        super(Service.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createEligibilityTab(), i18n.tr("Eligibility"));

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
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().type(), new CLabel()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().name()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().description()), 57).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Items"));
        main.setWidget(++row, 0, inject(proto().version().items(), new ServiceItemFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return new ScrollPanel(main);
    }

    public IsWidget createEligibilityTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Features"));
        main.setWidget(++row, 0, inject(proto().version().features(), new ServiceFeatureFolder(isEditable(), this)));

        main.setH1(++row, 0, 1, i18n.tr("Concessions"));
        main.setWidget(++row, 0, inject(proto().version().concessions(), new ServiceConcessionFolder(isEditable(), this)));

        return new ScrollPanel(main);
    }
}