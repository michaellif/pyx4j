/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-22
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.tenant.lead.Guest;

public class GuestFolder extends VistaBoxFolder<Guest> {

    private static final I18n i18n = I18n.get(GuestFolder.class);

    public GuestFolder(boolean modifyable) {
        super(Guest.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Guest) {
            return new GuestEditor();
        }
        return super.create(member);
    }

    class GuestEditor extends CEntityDecoratableEditor<Guest> {

        public GuestEditor() {
            super(Guest.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            if (isEditable()) {
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().namePrefix()), 5).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().middleName()), 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 20).build());
            } else {
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name(), new CEntityLabel()), 25).customLabel(i18n.tr("Guest")).build());
                get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
                get(proto().person().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
                main.setBR(++row, 0, 1);
            }

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 20).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());

            return main;
        }
    }
}
