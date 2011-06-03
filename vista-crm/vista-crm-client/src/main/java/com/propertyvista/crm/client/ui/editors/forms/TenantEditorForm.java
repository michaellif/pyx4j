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
package com.propertyvista.crm.client.ui.editors.forms;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    public TenantEditorForm() {
        super(TenantDTO.class, new CrmEditorsComponentFactory());
    }

    public TenantEditorForm(IEditableComponentFactory factory) {
        super(TenantDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        //Person
        Widget header = new CrmHeader1Decorator(i18n.tr("Details"));
        header.getElement().getStyle().setMarginTop(0, Unit.EM);
        main.add(header);

        main.add(inject(proto().person().name().namePrefix()), 7);
        main.add(inject(proto().person().name().firstName()), 15);
        main.add(inject(proto().person().name().middleName()), 15);
        main.add(inject(proto().person().name().lastName()), 15);
        main.add(inject(proto().person().name().maidenName()), 15);
        main.add(inject(proto().person().name().nameSuffix()), 7);

        main.add(inject(proto().person().homePhone()), 7);
        main.add(inject(proto().person().mobilePhone()), 7);
        main.add(inject(proto().person().workPhone()), 7);
        main.add(inject(proto().person().email()), 7);

        //Company
        main.add(new CrmHeader1Decorator(i18n.tr("Company Details")));

        main.add(inject(proto().company().name()), 15);
        //TODO Leon
        //Is a new sub page necessary for addresses?
        SubtypeInjectors.injectPhones(main, proto().company().phones(), this);
        main.add(inject(proto().company().website()), 25);
        SubtypeInjectors.injectEmails(main, proto().company().emails(), this);
        //TODO Leon
        //Is a new sub page necessary for OrganizationContacts?

        main.setWidth("100%");
        return main;
    }

    @Override
    public void populate(TenantDTO value) {
        super.populate(value);
        setVisibility(value);
    }

    private void setVisibility(TenantDTO tenant) {
        if (tenant.person() == null) {
            get(proto().person()).setVisible(false);
        } else {
            get(proto().company()).setVisible(false);
        }
    }
}