/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.common.client.ui.components.folders.PhoneFolder;
import com.propertyvista.domain.company.Company;

public class CCompany extends CEntityDecoratableEditor<Company> {

    public CCompany() {
        super(Company.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());

// TODO : design representation for:
//      main.add(parent.inject(proto.addresses()), 15);

        main.setWidget(++row, 0, inject(proto().phones(), new PhoneFolder(isEditable())));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().website()), 35).build());

        main.setWidget(++row, 0, inject(proto().emails(), new EmailFolder(isEditable())));

//TODO : design representation for:
//      main.add(parent.inject(proto.contacts()), 15);
//      main.add(parent.inject(proto.logo()), 15);

        return main;
    }
}
