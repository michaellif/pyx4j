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
 */
package com.propertyvista.common.client.ui.components.editors.dto.wizards;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.dto.vista2pmc.PersonalInformationDTO;

public class PersonalInformationForm extends CForm<PersonalInformationDTO> {

    public PersonalInformationForm() {
        super(PersonalInformationDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().name(), new NameEditor());

        formPanel.append(Location.Dual, proto().dto_personalAddress(), new InternationalAddressEditor());

        formPanel.append(Location.Left, proto().email()).decorate();
        formPanel.append(Location.Left, proto().dateOfBirth()).decorate();
        formPanel.append(Location.Left, proto().sin()).decorate();
        return formPanel;
    }

}
