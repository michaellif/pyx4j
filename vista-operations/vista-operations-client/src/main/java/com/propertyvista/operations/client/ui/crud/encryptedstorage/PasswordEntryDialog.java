/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;

public abstract class PasswordEntryDialog extends OkCancelDialog {

    private PasswordEntryForm form;

    public PasswordEntryDialog(String caption) {
        super(caption);
        // TODO Auto-generated constructor stub
    }

    public static class PasswordEntryForm extends CEntityDecoratableForm<PasswordEntryDTO> {

        private final boolean requirePasswordConfirm;

        public PasswordEntryForm(boolean requirePasswordConfirm) {
            super(PasswordEntryDTO.class);
            this.requirePasswordConfirm = requirePasswordConfirm;
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();
            contentPanel.add(new FormDecoratorBuilder(inject(proto().password())).build());
            if (requirePasswordConfirm) {
                contentPanel.add(new FormDecoratorBuilder(inject(proto().passwordConfirm())).build());
                get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
                    @Override
                    public ValidationError isValid(CComponent<String> component, String value) {
                        if (value != null && !value.equals(get(proto().password()).getValue())) {
                            return new ValidationError(component, "Password and Password confirmation don't match");
                        }
                        return null;
                    }
                });
            }

            return contentPanel;
        }

    }

    public PasswordEntryDialog() {
        this(true);
    }

    public PasswordEntryDialog(boolean requirePasswordConfirm) {
        super("");

        form = new PasswordEntryForm(requirePasswordConfirm);
        form.initContent();
        form.populateNew();

        setBody(form);
    }

    public char[] getPassword() {
        form.revalidate();
        form.setUnconditionalValidationErrorRendering(true);
        if (form.isValid()) {
            return form.getValue().password().getValue().toCharArray();
        } else {
            return null;
        }
    }

}
