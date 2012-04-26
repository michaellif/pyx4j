/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoForm extends CEntityDecoratableEditor<ResidentDTO> {

    private static final I18n i18n = I18n.get(PersonalInfoForm.class);

    public PersonalInfoForm() {
        super(ResidentDTO.class, new VistaEditorsComponentFactory());
        initContent();
    }

    @Override
    public IsWidget createContent() {

        Widget widget = null;
        FormFlexPanel container = new FormFlexPanel();

        int row = -1;

        container.setH1(++row, 0, 1, i18n.tr("Contact Details"));

        container.setWidget(++row, 0, widget = new DecoratorBuilder(inject(proto().name().firstName(), new CLabel()), 12).build());
        widget.getElement().getStyle().setMarginTop(20, Unit.PX);

        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().middleName(), new CLabel()), 12).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().lastName(), new CLabel()), 20).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homePhone()), 15).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().workPhone()), 15).build());
        container.setWidget(++row, 0, widget = new DecoratorBuilder(inject(proto().email()), 20).build());
        widget.getElement().getStyle().setMarginBottom(20, Unit.PX);

        //Emergency Contacts
        container.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());

        container.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable(), false)));
        container.getCellFormatter().getElement(row, 0).getStyle().setPadding(10, Unit.PX);
        return container;
    }
}
