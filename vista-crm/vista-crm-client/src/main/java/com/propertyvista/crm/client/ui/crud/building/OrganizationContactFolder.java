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
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.OrganizationContact;

class OrganizationContactFolder extends VistaBoxFolder<OrganizationContact> {
    public OrganizationContactFolder(boolean modifyable) {
        super(OrganizationContact.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof OrganizationContact) {
            return new OrganizationContactEditor();
        } else {
            return super.create(member);
        }
    }

    static public class OrganizationContactEditor extends CEntityDecoratableEditor<OrganizationContact> {

        public OrganizationContactEditor() {
            super(OrganizationContact.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person()), 35).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 35).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 35).build());

            // repopulate related fields from selected employee:
            get(proto().person()).addValueChangeHandler(new ValueChangeHandler<Employee>() {
                @Override
                public void onValueChange(ValueChangeEvent<Employee> event) {
                    OrganizationContact value = getValue();
                    value.person().set(event.getValue());
                    setValue(value);
                }
            });

            return main;
        }
    }
}