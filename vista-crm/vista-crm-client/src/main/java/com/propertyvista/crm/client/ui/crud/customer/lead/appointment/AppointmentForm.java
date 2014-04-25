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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentForm extends CrmEntityForm<Appointment> {

    private static final I18n i18n = I18n.get(AppointmentForm.class);

    public AppointmentForm(IForm<Appointment> view) {
        super(Appointment.class, view);
        selectTab(addTab(createGeneralTab(), i18n.tr("General")));
        setTabEnabled(addTab(createShowingsTab(), i18n.tr("Showings")), !isEditable());

    }

    private TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, inject(proto().date(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 0, inject(proto().time(), new FieldDecoratorBuilder(7).build()));
        main.setWidget(++row, 0, inject(proto().address(), new FieldDecoratorBuilder(25).build()));
        main.setWidget(++row, 0, inject(proto().status(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 0, inject(proto().closeReason(), new FieldDecoratorBuilder(25).build()));
        main.setWidget(++row, 0, inject(proto().notes(), new FieldDecoratorBuilder(25).build()));

        row = -1;
        main.setWidget(++row, 1, inject(proto().agent(), new FieldDecoratorBuilder(20).build()));
        main.setWidget(++row, 1, inject(proto().phone(), new FieldDecoratorBuilder(20).customLabel(i18n.tr("Agent Phone")).build()));
        main.setWidget(++row, 1, inject(proto().email(), new FieldDecoratorBuilder(20).customLabel(i18n.tr("Agent Email")).build()));

        // tweak UI:
        get(proto().status()).setEditable(false);
        get(proto().closeReason()).setEditable(false);

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