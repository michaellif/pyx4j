/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.company.Employee;

public class EmployeeEditorForm extends CrmEntityForm<Employee> {

    public EmployeeEditorForm() {
        super(Employee.class, new CrmEditorsComponentFactory());
    }

    public EmployeeEditorForm(IEditableComponentFactory factory) {
        super(Employee.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().title()), 20);

        main.add(new HTML("&nbsp"));

        main.add(inject(proto().name().namePrefix()), 6);
        main.add(inject(proto().name().firstName()), 15);
        main.add(inject(proto().name().middleName()), 15);
        main.add(inject(proto().name().lastName()), 15);
        main.add(inject(proto().name().maidenName()), 15);
        main.add(inject(proto().name().nameSuffix()), 6);
        main.add(inject(proto().birthDate()), 8.2);

        main.add(inject(proto().homePhone()), 15);
        main.add(inject(proto().mobilePhone()), 15);
        main.add(inject(proto().workPhone()), 15);
        main.add(inject(proto().email()), 25);

        main.add(new HTML("&nbsp"));

        main.add(inject(proto().description()), 50);

        return new CrmScrollPanel(main);
    }
}
