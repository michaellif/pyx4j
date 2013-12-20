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

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.ISignature;

public class CSignature extends CFocusComponent<ISignature, NSignature> {

    private String checkBoxText;

    private String checkBoxAnchorText;

    private Command checkBoxAnchorCommand;

    public CSignature(String checkBoxText) {
        this(checkBoxText, null, null);
    }

    public CSignature(String checkBoxText, String checkBoxAnchorText, Command checkBoxAnchorCommand) {
        super();
        setCheckBoxText(checkBoxText);
        setCheckBoxAnchor(checkBoxAnchorText, checkBoxAnchorCommand);
        setNativeWidget(new NSignature(this));
        asWidget().setWidth("100%");

    }

    public void setCheckBoxText(String checkBoxText) {
        this.checkBoxText = checkBoxText;
    }

    public String getCheckBoxText() {
        return checkBoxText;
    }

    public void setCheckBoxAnchor(String checkBoxAnchorText, Command checkBoxAnchorCommand) {
        this.checkBoxAnchorText = checkBoxAnchorText;
        this.checkBoxAnchorCommand = checkBoxAnchorCommand;
    }

    public String getCheckBoxAnchorText() {
        return checkBoxAnchorText;
    }

    public Command getCheckBoxAnchorCommand() {
        return checkBoxAnchorCommand;
    }

}
