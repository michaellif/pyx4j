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

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
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

        this.addComponentValidator(new AbstractComponentValidator<IList<Guest>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    return (getComponent().getValue().isEmpty() ? new FieldValidationError(getComponent(), i18n.tr("At least one guest data should be entered"))
                            : null);
                }
                return null;
            }
        });
    }

    @Override
    protected CEntityForm<Guest> createItemForm(IObject<?> member) {
        return new GuestEditor();
    }

    class GuestEditor extends AccessoryEntityForm<Guest> {

        public GuestEditor() {
            super(Guest.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel right = new BasicFlexFormPanel();
            int row = -1;

            right.setWidget(++row, 0, injectAndDecorate(proto().person().email(), 22));
            right.setWidget(++row, 0, injectAndDecorate(proto().person().homePhone(), 15));
            right.setWidget(++row, 0, injectAndDecorate(proto().person().mobilePhone(), 15));
            right.setWidget(++row, 0, injectAndDecorate(proto().person().workPhone(), 15));

            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
            main.setWidget(0, 0, inject(proto().person().name(), new NameEditor(i18n.tr("Person"), true)));
            main.setWidget(0, 1, right);
            main.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            return main;
        }
    }
}
