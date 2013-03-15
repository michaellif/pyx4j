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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.form.IForm;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentForm extends CrmEntityForm<Appointment> {

    private static final I18n i18n = I18n.get(AppointmentForm.class);

    public AppointmentForm(IForm<Appointment> view) {
        super(Appointment.class, view);
        selectTab(addTab(createGeneralTab(i18n.tr("General"))));
        setTabEnabled(addTab(createShowingsTab(), i18n.tr("Showings")), !isEditable());

    }

    private FormFlexPanel createGeneralTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().date()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().time()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().address()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().closeReason()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notes()), 25).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().agent()), 20).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().phone()), 20).customLabel(i18n.tr("Agent Phone")).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().email()), 20).customLabel(i18n.tr("Agent Email")).build());

        // tweak UI:
        get(proto().status()).setViewable(true);
        get(proto().closeReason()).setViewable(true);

        main.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        return main;
    }

    private Widget createShowingsTab() {
        if (!isEditable()) {
            return ((AppointmentViewerView) getParentView()).getShowingsListerView().asWidget();
        }
        return new HTML(); // just stub - not necessary for editing mode!..
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().closeReason()).setVisible(getValue().status().getValue() == Appointment.Status.closed);
    }
}