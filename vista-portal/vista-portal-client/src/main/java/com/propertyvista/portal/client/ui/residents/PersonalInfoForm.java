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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.editors.EmergencyContactFolder;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoForm extends CEntityEditor<ResidentDTO> implements PersonalInfoView {

    private final DecorationData decor;

    private PersonalInfoView.Presenter presenter;

    private static I18n i18n = I18n.get(PersonalInfoForm.class);

    public PersonalInfoForm() {
        super(ResidentDTO.class, new VistaEditorsComponentFactory());
        decor = new DecorationData(10d, 20);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        //contact details
        container.add(new VistaHeaderBar(i18n.tr("Contact Details"), "100%"));
        container.add(new VistaWidgetDecorator(inject(proto().name().firstName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().name().middleName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().name().lastName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().homePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().mobilePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().workPhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().email()), decor));
        //Emergency Contacts
        container.add(new VistaHeaderBar(proto().emergencyContacts(), "100%"));
        container.add(inject(proto().emergencyContacts(), new EmergencyContactFolder()));
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
