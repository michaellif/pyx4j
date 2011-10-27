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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.EmergencyContact;

public class CEmergencyContact extends CDecoratableEntityEditor<EmergencyContact> {

    private final boolean twoColumns;

    public CEmergencyContact() {
        this(false);
    }

    public CEmergencyContact(boolean twoColumns) {
        super(EmergencyContact.class);
        this.twoColumns = twoColumns;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().middleName()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().lastName()), 25).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name(), new CEntityLabel()), 25).customLabel(i18n.tr("Person")).build());
            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            get(proto().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().workPhone()), 15).build());

        main.setHeader(++row, 0, 1, "");
        main.setWidget(++row, 0, inject(proto().address(), new CAddressStructured(twoColumns)));

        return main;
    }
}