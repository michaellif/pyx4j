/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 21, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.profile;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class AddressEditor extends CEntityForm<AddressSimple> {

    private final ProfileViewImpl view;

    public AddressEditor(ProfileViewImpl view) {
        super(AddressSimple.class);
        this.view = view;
    }

    @Override
    public FormFlexPanel createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().street1()), "200px").customLabel("Address").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().city()), "200px").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().province()), "200px").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().country()), "200px").build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().postalCode()), "200px").build());

        view.updateDecoratorsLayout(this, view.getWidgetLayout());

        return main;
    }

}