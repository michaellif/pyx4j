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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.domain.company.OrganizationContact;

class OrganizationContactFolder extends VistaBoxFolder<OrganizationContact> {
    public OrganizationContactFolder() {
        super(OrganizationContact.class);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof OrganizationContact) {
            return new OrganizationContactEditor();
        } else {
            return super.create(member);
        }
    }

    static class OrganizationContactEditor extends CEntityEditor<OrganizationContact> {

        public OrganizationContactEditor() {
            super(OrganizationContact.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, decorate(inject(proto().description()), 35));
            //TODO
//                if (parent.isEditable()) {
//                    main.setWidget(++row, 0, decorate(inject(proto().person()), 35));
//                } else {
            main.setWidget(++row, 0, decorate(inject(proto().person()), 35));
            main.setWidget(++row, 0, decorate(inject(proto().person().workPhone()), 10));
            main.setWidget(++row, 0, decorate(inject(proto().person().mobilePhone()), 10));
            main.setWidget(++row, 0, decorate(inject(proto().person().homePhone()), 10));
            main.setWidget(++row, 0, decorate(inject(proto().person().email()), 20));
//                }

            return main;
        }

        private WidgetDecorator decorate(CComponent<?> component, double componentWidth) {
            return new WidgetDecorator.Builder(component).componentWidth(componentWidth).readOnlyMode(!isEditable()).build();
        }
    }
}