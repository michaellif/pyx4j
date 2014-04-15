/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.dto.wizards;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.dto.vista2pmc.PersonalInformationDTO;

public class PersonalInformationForm extends CEntityForm<PersonalInformationDTO> {

    public PersonalInformationForm() {
        super(PersonalInformationDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = 0;
        main.setWidget(++row, 0, inject(proto().name(), new NameEditor()));
        main.setWidget(++row, 0, new HTML("&nbsp;"));
        main.setWidget(++row, 0, inject(proto().dto_personalAddress(), new AddressSimpleEditor()));
        main.setWidget(++row, 0, new HTML("&nbsp;"));
        main.setWidget(++row, 0, inject(proto().email(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().dateOfBirth(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().sin(), new FormDecoratorBuilder().build()));
        return main;
    }

}
