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
package com.propertyvista.crm.client.ui.crud.customer.lead.appointment;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.IDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentForm extends CrmEntityForm<Appointment> {

    private static final I18n i18n = I18n.get(AppointmentForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public AppointmentForm() {
        this(false);
    }

    public AppointmentForm(boolean viewMode) {
        super(Appointment.class, viewMode);
    }

    @Override
    protected IDecorator createDecorator() {
        return null;
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createShowingsTab(), i18n.tr("Showings"));
        tabPanel.setLastTabDisabled(isEditable());

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
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().date()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().time()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().address()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 9).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().agent()), 20).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().phone()), 20).customLabel(i18n.tr("Agent Phone")).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().email()), 20).customLabel(i18n.tr("Agent Email")).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new ScrollPanel(main);
    }

    private Widget createShowingsTab() {
        if (!isEditable()) {
            return new ScrollPanel(((AppointmentViewerView) getParentView()).getShowingsListerView().asWidget());
        }
        return new HTML(); // just stub - not necessary for editing mode!..
    }
}