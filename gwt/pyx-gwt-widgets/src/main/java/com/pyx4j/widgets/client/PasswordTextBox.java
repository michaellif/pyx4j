/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

public class PasswordTextBox extends ValueBoxBase<String> {

    private boolean revealText;

    public PasswordTextBox() {
        setTextBoxWidget(new com.google.gwt.user.client.ui.PasswordTextBox());
        revealText(false);
    }

    @Override
    protected void setText(String text, boolean watermark) {
        if (!revealText) {
            if (watermark) {
                getTextBoxWidget().getElement().setAttribute("type", "text");
            } else {
                getTextBoxWidget().getElement().setAttribute("type", "password");
            }
        }
        super.setText(text, watermark);
    }

    public void revealText(boolean reveal) {
        this.revealText = reveal;
        if (reveal) {
            getElement().setAttribute("type", "text");
        } else {
            getElement().setAttribute("type", "password");
        }
    }

}
