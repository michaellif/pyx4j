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
package com.propertyvista.portal.web.client.ui.profile;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.web.client.ui.AbstractPortalPanel;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class EmergencyContactForm extends CEntityForm<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactForm.class);

    private final ProfilePageViewImpl view;

    public EmergencyContactForm(ProfilePageViewImpl view) {
        super(EmergencyContact.class);
        this.view = view;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), 200).customLabel(i18n.tr("Full Name")).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email()), 230).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), 200).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), 200).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), 200).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().street1()), 200).customLabel("Address").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().city()), 200).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().province()), 200).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().country()), 200).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().postalCode()), 200).build());

        AbstractPortalPanel.updateDecoratorsLayout(main, view.getWidgetLayout());

        return main;
    }
}