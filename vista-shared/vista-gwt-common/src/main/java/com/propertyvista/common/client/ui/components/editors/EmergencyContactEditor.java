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
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactEditor extends CEntityDecoratableEditor<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactEditor.class);

    private final boolean twoColumns;

    public EmergencyContactEditor() {
        this(true);
    }

    public EmergencyContactEditor(boolean twoColumns) {
        super(EmergencyContact.class);
        this.twoColumns = twoColumns;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        FormFlexPanel left = new FormFlexPanel();
        int row = -1;
        left.setWidget(++row, 0, inject(proto().name(), new NameEditor(i18n.tr("Person"))));
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sex()), 7).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().birthDate()), 9).build());

        FormFlexPanel right = new FormFlexPanel();
        row = -1;
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 25).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homePhone()), 15).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().workPhone()), 15).build());

        // assemble main panel:
        if (twoColumns) {
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);
            main.setHR(1, 0, 2);
            main.setWidget(2, 0, inject(proto().address(), new AddressStructuredEditor(twoColumns, true)));
            main.getFlexCellFormatter().setColSpan(2, 0, 2);

            main.getColumnFormatter().setWidth(0, "50%");
            main.getColumnFormatter().setWidth(1, "50%");
        } else {
            main.setWidget(0, 0, left);
            main.setWidget(1, 0, right);
            main.setWidget(3, 0, inject(proto().address(), new AddressStructuredEditor(twoColumns, true)));
        }
        main.setWidth("100%");

        return main;
    }
}