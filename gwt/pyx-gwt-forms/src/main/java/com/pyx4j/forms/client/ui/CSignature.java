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

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class CSignature extends CFocusComponent<ISignature, NSignature> {

    private static final I18n i18n = I18n.get(CSignature.class);

    private String checkBoxText;

    private IsWidget customWidget;

    public CSignature(String checkBoxText) {
        this(checkBoxText, null);
    }

    public CSignature(String checkBoxText, IsWidget customWidget) {
        super();
        setCustomWidget(customWidget);
        setNativeWidget(new NSignature(this));
        asWidget().setWidth("100%");

        addValueValidator(new EditableValueValidator<ISignature>() {
            @Override
            public ValidationError isValid(CComponent<ISignature> component, ISignature value) {
                if (value != null) {
                    switch (value.signatureFormat().getValue()) {
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

    public void setCheckBoxText(String checkBoxText) {
        this.checkBoxText = checkBoxText;
    }

    public String getCheckBoxText() {
        return checkBoxText;
    }

    public void setCustomWidget(IsWidget widget) {
        this.customWidget = widget;
    }

    public IsWidget getCustomWidget() {
        return customWidget;
    }

}
