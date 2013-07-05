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
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.web.client.ui.EntityViewImpl;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class EmergencyContactEditor extends CEntityForm<EmergencyContact> {

    public EmergencyContactEditor() {
        super(EmergencyContact.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), "250px").customLabel("").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email()), "250px").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), "250px").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), "250px").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), "250px").build());
        main.setWidget(++row, 0, inject(proto().address(), new AddressEditor()));

        EntityViewImpl.updateDecoratorsLayout(this, Layout.vertical);

        return main;
    }
}