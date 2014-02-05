/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Dec 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class CSignature extends CFocusComponent<ISignature, NSignature> {

    private static final I18n i18n = I18n.get(CSignature.class);

    private IsWidget descriptionWidget;

    private EditableValueValidator<ISignature> signatureComplitionsValidator;

    public CSignature(String text) {
        this(new Label(text));
    }

    public CSignature(IsWidget widget) {
        super();
        descriptionWidget = widget;
        setNativeWidget(new NSignature(this));
        asWidget().setWidth("100%");

        setSignatureCompletionValidator(new EditableValueValidator<ISignature>() {
            @Override
            public ValidationError isValid(CComponent<ISignature> component, ISignature value) {
                if (value != null) {
                    SignatureFormat signatureFormat = value.signatureFormat().isNull() ? SignatureFormat.None : value.signatureFormat().getValue();
                    switch (signatureFormat) {
                    case None:
                        break;
                    case AgreeBox:
                        if (!value.agree().isBooleanTrue()) {
                            return new ValidationError(component, i18n.tr("You must agree to the Terms to continue"));
                        }
                        break;
                    case AgreeBoxAndFullName:
                        if (!value.agree().isBooleanTrue()) {
                            return new ValidationError(component, i18n.tr("You must agree to the Terms to continue"));
                        }
                    case FullName:
                        if (value.fullName().getValue() == null || value.fullName().getValue().trim().equals("")) {
                            return new ValidationError(component, i18n.tr("You must agree to the Terms by typing your First and Last name to continue"));
                        }
                        break;
                    case Initials:
                        if (value.initials().getValue() == null || value.initials().getValue().trim().equals("")) {
                            return new ValidationError(component, i18n.tr("You must agree to the Terms by typing your Initials to continue"));
                        }
                        break;
                    }
                }
                return null;
            }
        });

    }

    public void setSignatureCompletionValidator(EditableValueValidator<ISignature> validator) {
        removeValueValidator(signatureComplitionsValidator);
        signatureComplitionsValidator = validator;
        addValueValidator(signatureComplitionsValidator);
    }

    public IsWidget getDescriptionWidget() {
        return descriptionWidget;
    }

}
