/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-22
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.lead;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.tenant.lead.Guest;

public class GuestFolder extends VistaBoxFolder<Guest> {

    private static final I18n i18n = I18n.get(GuestFolder.class);

    public GuestFolder(boolean modifyable) {
        super(Guest.class, modifyable);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue().isEmpty()) {
            addItem(); // at lease one guest should be added!
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addValueValidator(new EditableValueValidator<IList<Guest>>() {
            @Override
            public ValidationError isValid(CComponent<IList<Guest>> component, IList<Guest> value) {
                if (value != null) {
                    return (value.isEmpty() ? new ValidationError(component, i18n.tr("At least one guest data should be entered")) : null);
                }
                return null;
            }
        });
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Guest) {
            return new GuestEditor();
        }
        return super.create(member);
    }

    class GuestEditor extends CEntityDecoratableForm<Guest> {

        public GuestEditor() {
            super(Guest.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel flexPanel = new FormFlexPanel();

            int row = -1;
            flexPanel.setWidget(++row, 0, 2, inject(proto().person().name(), new NameEditor(i18n.tr("Person"))));

            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().email()), 25).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().homePhone()), 15).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().workPhone()), 15).build());

            return flexPanel;
        }
    }
}
