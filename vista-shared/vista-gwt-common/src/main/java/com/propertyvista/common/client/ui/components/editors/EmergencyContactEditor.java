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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactEditor extends CEntityDecoratableForm<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactEditor.class);

    public EmergencyContactEditor() {
        super(EmergencyContact.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().name(), new NameEditor(i18n.tr("Person"))));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sex()), 7).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().birthDate()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().email()), 22).build());
        get(proto().birthDate()).setMandatory(false);

        row = 0;
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().workPhone()), 15).build());

        return main;
    }
}