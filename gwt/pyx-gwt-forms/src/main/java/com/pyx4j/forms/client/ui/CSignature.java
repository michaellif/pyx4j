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
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

public class CSignature extends CFocusComponent<ISignature, NSignature> {

    private static final I18n i18n = I18n.get(CSignature.class);

    private IsWidget descriptionWidget;

    private AbstractComponentValidator<ISignature> signatureComplitionValidator;

    public CSignature(String text) {
        this(new Label(text));
    }

    public CSignature(IsWidget widget) {
        super();
        descriptionWidget = widget;
        setNativeWidget(new NSignature(this));
        asWidget().setWidth("100%");

        setSignatureCompletionValidator(new AbstractComponentValidator<ISignature>() {
            @Override
            public FieldValidationError isValid() {
                ISignature value = getComponent().getValue();
                if (value != null) {
                    SignatureFormat signatureFormat = value.signatureFormat().isNull() ? SignatureFormat.None : value.signatureFormat().getValue();
                    switch (signatureFormat) {
                    case None:
                        break;
                    case AgreeBox:
                        if (!value.agree().getValue(false)) {
                            return new FieldValidationError(getComponent(), i18n.tr("You must agree to the Terms to continue"));
                        }
                        break;
                    case AgreeBoxAndFullName:
                        if (!value.agree().getValue(false)) {
                            return new FieldValidationError(getComponent(), i18n.tr("You must agree to the Terms to continue"));
                        }
                    case FullName:
                        if (value.fullName().getValue() == null || value.fullName().getValue().trim().equals("")) {
                            return new FieldValidationError(getComponent(),
                                    i18n.tr("You must agree to the Terms by typing your First and Last name to continue"));
                        }
                        break;
                    case Initials:
                        if (value.initials().getValue() == null || value.initials().getValue().trim().equals("")) {
                            return new FieldValidationError(getComponent(), i18n.tr("You must agree to the Terms by typing your Initials to continue"));
                        }
                        break;
                    }
                }
                return null;
            }
        });

    }

    @Override
    public boolean isValidatable() {
        return isVisible() && isEditable() && isEnabled() && !isViewable() && (isVisited() || isEditingInProgress());
    }

    public void setSignatureCompletionValidator(AbstractComponentValidator<ISignature> validator) {
        removeComponentValidator(signatureComplitionValidator);
        signatureComplitionValidator = validator;
        addComponentValidator(signatureComplitionValidator);
    }

    public IsWidget getDescriptionWidget() {
        return descriptionWidget;
    }

    @Override
    public void setMockValue(ISignature value) {
        if (isVisible() && isEditable() && isEnabled() && !isViewable()) {
            setValue(value);
        }
    }

}
