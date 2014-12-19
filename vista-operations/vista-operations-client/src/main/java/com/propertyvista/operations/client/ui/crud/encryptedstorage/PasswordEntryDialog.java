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
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class PasswordEntryDialog extends OkCancelDialog {

    private PasswordEntryForm form;

    public PasswordEntryDialog(String caption) {
        super(caption);
        // TODO Auto-generated constructor stub
    }

    public static class PasswordEntryForm extends CForm<PasswordEntryDTO> {

        private final boolean requirePasswordConfirm;

        public PasswordEntryForm(boolean requirePasswordConfirm) {
            super(PasswordEntryDTO.class);
            this.requirePasswordConfirm = requirePasswordConfirm;
        }

        @Override
        protected IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();
            contentPanel.add(inject(proto().password(), new FieldDecoratorBuilder().build()));
            if (requirePasswordConfirm) {
                contentPanel.add(inject(proto().passwordConfirm(), new FieldDecoratorBuilder().build()));
                get(proto().passwordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
                    @Override
                    public BasicValidationError isValid() {
                        if (getCComponent().getValue() != null && !getCComponent().getValue().equals(get(proto().password()).getValue())) {
                            return new BasicValidationError(getCComponent(), "Password and Password confirmation don't match");
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
        form.init();
        form.populateNew();

        setBody(form);
    }

    public char[] getPassword() {
        form.setVisitedRecursive();
        if (form.isValid()) {
            return form.getValue().password().getValue().toCharArray();
        } else {
            return null;
        }
    }

}
