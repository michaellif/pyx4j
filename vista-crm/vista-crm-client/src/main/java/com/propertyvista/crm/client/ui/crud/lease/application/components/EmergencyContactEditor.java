/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactEditor extends CForm<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactEditor.class);

    public EmergencyContactEditor() {
        super(EmergencyContact.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel main = (new TwoColumnFlexFormPanel());
        int row = -1;
        int col = (1);
        int span = (2);

        main.setWidget(++row, 0, span, inject(proto().name(), new NameEditor(i18n.tr("Person"), false)));
        main.setWidget(++row, 0, inject(proto().sex(), new FieldDecoratorBuilder(7).build()));

        row = (row - 1);
        main.setWidget(++row, col, inject(proto().birthDate(), new FieldDecoratorBuilder(10).build()));

        main.setH3(++row, 0, 2, i18n.tr("Contact Info"));
        main.setWidget(++row, 0, inject(proto().homePhone(), new FieldDecoratorBuilder(15).build()));
        main.setWidget(++row, 0, inject(proto().mobilePhone(), new FieldDecoratorBuilder(15).build()));
        main.setWidget(++row, 0, inject(proto().workPhone(), new FieldDecoratorBuilder(15).build()));

        row = (row - 3);
        main.setWidget(++row, col, inject(proto().relationship(), new FieldDecoratorBuilder(15).build()));
        main.setWidget(++row, col, inject(proto().email(), new FieldDecoratorBuilder(22).build()));

        row = (row + 1);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, span, inject(proto().address(), new AddressSimpleEditor(false)));

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
    }
}