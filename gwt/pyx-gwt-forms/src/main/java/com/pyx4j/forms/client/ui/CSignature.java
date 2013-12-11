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

import java.text.ParseException;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureType;

public class CSignature extends CTextFieldBase<ISignature, NSignature> {

    private final SignatureType signatureType;

    private final String checkBoxText;

    private final String checkBoxAnchorText;

    private final Command checkBoxAnchorCommand;

    public CSignature(SignatureType signatureType, String checkBoxText) {
        this(signatureType, checkBoxText, null, null);
    }

    public CSignature(SignatureType signatureType, String checkBoxText, String checkBoxAnchorText, Command checkBoxAnchorCommand) {
        super();
        this.signatureType = signatureType;
        this.checkBoxText = checkBoxText;
        this.checkBoxAnchorText = checkBoxAnchorText;
        this.checkBoxAnchorCommand = checkBoxAnchorCommand;
        setNativeWidget(new NSignature(this));
        asWidget().setWidth("100%");

        setFormat(new IFormat<ISignature>() {

            @Override
            public String format(ISignature value) {
                if (value == null) {
                    return "";
                } else if (value.signatureType().getValue() == SignatureType.FullName || value.signatureType().getValue() == SignatureType.AgreeBoxAndFullName) {
                    return value.fullName().getValue();
                } else if (value.signatureType().getValue() == SignatureType.Initials) {
                    return value.initials().getValue();
                } else {
                    return "";
                }
            }

            @Override
            public ISignature parse(String string) throws ParseException {
                if (getValue().signatureType().getValue() == SignatureType.FullName
                        || getValue().signatureType().getValue() == SignatureType.AgreeBoxAndFullName) {
                    getValue().fullName().setValue(string);
                } else if (getValue().signatureType().getValue() == SignatureType.Initials) {
                    getValue().initials().setValue(string);
                } else {
                }
                return getValue();
            }
        });

    }

    public SignatureType getSignatureType() {
        return signatureType;
    }

    public String getCheckBoxText() {
        return checkBoxText;
    }

    public String getCheckBoxAnchorText() {
        return checkBoxAnchorText;
    }

    public Command getCheckBoxAnchorCommand() {
        return checkBoxAnchorCommand;
    }

}
