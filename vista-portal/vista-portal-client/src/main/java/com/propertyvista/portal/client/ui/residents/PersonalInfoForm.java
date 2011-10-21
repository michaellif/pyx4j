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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.editors.EmergencyContactFolder;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoForm extends CEntityEditor<ResidentDTO> implements PersonalInfoView {

    private PersonalInfoView.Presenter presenter;

    private static I18n i18n = I18n.get(PersonalInfoForm.class);

    public PersonalInfoForm() {
        super(ResidentDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();
        //contact details

        int row = 0;

        container.setHeader(row++, 0, 1, i18n.tr("Contact Details"));

        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().name().firstName()), 12));
        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().name().middleName()), 12));
        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().name().lastName()), 20));
        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().homePhone()), 15));
        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().mobilePhone()), 15));
        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().workPhone()), 15));
        container.setWidget(row++, 0, WidgetDecorator.build(inject(proto().email()), 20));

        //Emergency Contacts
        container.setHeader(row++, 0, 1, proto().emergencyContacts().getMeta().getCaption());
        container.setWidget(row++, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(ResidentDTO personalInfo) {
        super.populate(personalInfo);

    }

}
