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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
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

    private IsWidget createGeneralTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().date()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().time()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().address()).decorate();
        formPanel.append(Location.Left, proto().status()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().agent()).decorate();
        formPanel.append(Location.Right, proto().phone()).decorate().componentWidth(150).customLabel(i18n.tr("Agent Phone"));
        formPanel.append(Location.Right, proto().email()).decorate().componentWidth(200).customLabel(i18n.tr("Agent Email"));

        formPanel.append(Location.Full, proto().closeReason()).decorate();
        formPanel.append(Location.Full, proto().notes()).decorate();

        // tweak UI:
        get(proto().status()).setEditable(false);
        get(proto().closeReason()).setEditable(false);

        return formPanel;
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