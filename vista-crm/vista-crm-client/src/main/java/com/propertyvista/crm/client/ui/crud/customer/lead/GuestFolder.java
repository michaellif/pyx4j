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

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

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
            public FieldValidationError isValid(CComponent<IList<Guest>> component, IList<Guest> value) {
                if (value != null) {
                    return (value.isEmpty() ? new FieldValidationError(component, i18n.tr("At least one guest data should be entered")) : null);
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

    class GuestEditor extends CEntityForm<Guest> {

        public GuestEditor() {
            super(Guest.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel right = new BasicFlexFormPanel();
            int row = -1;

            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().email()), 22).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().homePhone()), 15).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().workPhone()), 15).build());

            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
            main.setWidget(0, 0, inject(proto().person().name(), new NameEditor(i18n.tr("Person"), true)));
            main.setWidget(0, 1, right);

            return main;
        }
    }
}
