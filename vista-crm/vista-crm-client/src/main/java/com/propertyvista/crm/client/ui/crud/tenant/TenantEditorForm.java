/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    private final VistaDecoratorsFlowPanel person = new VistaDecoratorsFlowPanel();

    private final VistaDecoratorsFlowPanel company = new VistaDecoratorsFlowPanel();

    public TenantEditorForm() {
        super(TenantDTO.class, new CrmEditorsComponentFactory());
    }

    public TenantEditorForm(IEditableComponentFactory factory) {
        super(TenantDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        //Person
        person.add(new CrmHeader1Decorator(i18n.tr("Details")));
        person.add(inject(proto().person().name().namePrefix()), 7);
        person.add(inject(proto().person().name().firstName()), 15);
        person.add(inject(proto().person().name().middleName()), 15);
        person.add(inject(proto().person().name().lastName()), 15);
        person.add(inject(proto().person().name().maidenName()), 15);
        person.add(inject(proto().person().name().nameSuffix()), 7);

        person.add(inject(proto().person().homePhone()), 7);
        person.add(inject(proto().person().mobilePhone()), 7);
        person.add(inject(proto().person().workPhone()), 7);
        person.add(inject(proto().person().email()), 7);

        //Company
        company.add(new CrmHeader1Decorator(i18n.tr("Company")));
        company.add(inject(proto().company().name()), 15);
        //TODO Leon
        //Is a new sub page necessary for addresses?
        SubtypeInjectors.injectPhones(company, proto().company().phones(), this);
        company.add(inject(proto().company().website()), 23);
        SubtypeInjectors.injectEmails(company, proto().company().emails(), this);
        //TODO Leon
        //Is a new sub page necessary for OrganizationContacts?

        FlowPanel main = new FlowPanel();
        main.add(person);
        main.add(company);
        main.setWidth("100%");
        return main;
    }

    @Override
    public void populate(TenantDTO value) {
        super.populate(value);
//        setVisibility(value);
    }

    private void setVisibility(TenantDTO tenant) {
        if (tenant.person() == null) {
            person.setVisible(false);
        } else {
            company.setVisible(false);
        }
    }
}