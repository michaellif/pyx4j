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

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactEditor extends CEntityForm<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactEditor.class);

    private final boolean oneColumn;

    public EmergencyContactEditor() {
        this(false);
    }

    public EmergencyContactEditor(boolean oneColumn) {
        super(EmergencyContact.class);
        this.oneColumn = oneColumn;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = (oneColumn ? new BasicFlexFormPanel() : new TwoColumnFlexFormPanel());
        int row = -1;
        int col = (oneColumn ? 0 : 1);
        int span = (oneColumn ? 1 : 2);

        main.setWidget(++row, 0, span, inject(proto().name(), new NameEditor(i18n.tr("Person"), oneColumn)));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sex()), 7).build());

        row = (oneColumn ? row : row - 1);
        main.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().birthDate()), 10).build());

        main.setH3(++row, 0, 2, i18n.tr("Contact Info"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), 15).build());

        row = (oneColumn ? row : row - 3);
        main.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().relationship()), 15).build());
        main.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().email()), 22).build());

        row = (oneColumn ? row : row + 1);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, span, inject(proto().address(), new AddressSimpleEditor(oneColumn)));

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
    }
}