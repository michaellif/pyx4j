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
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadEditorForm extends CrmEntityForm<Lead> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public LeadEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public LeadEditorForm(IEditableComponentFactory factory) {
        super(Lead.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.addDisable(createAppointmentsTab(), i18n.tr("Appointments"));

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
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().informedFrom()), 10).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().moveInDate()), 8.2).build());

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().rent().min()), 5).customLabel(i18n.tr("Min rent")).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().rent().max()), 5).customLabel(i18n.tr("Max rent")).build());

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().term()), 8).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().beds()), 4).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().baths()), 4).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().floorplan()), 15).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().comments()), 57).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, "");

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().source()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().assignedTo()), 20).build());
        row -= 2;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().createDate()), 8.2).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 10).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    private Widget createAppointmentsTab() {
        if (!isEditable()) {
            return new ScrollPanel(((LeadViewerView) getParentView()).getAppointmentsListerView().asWidget());
        }
        return new HTML(); // just stub - not necessary for editing mode!.. 
    }
}