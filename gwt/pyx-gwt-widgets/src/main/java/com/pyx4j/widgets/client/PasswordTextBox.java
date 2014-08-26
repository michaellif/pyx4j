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

public class PasswordTextBox extends TextBoxBase {

    private boolean revealText = false;

    private TextWatermark watermark;

    public PasswordTextBox() {
        setTextBoxWidget(new com.google.gwt.user.client.ui.PasswordTextBox());
    }

    @Override
    protected TextWatermark createWatermark() {
        watermark = new TextWatermark(getTextBoxWidget()) {

            @Override
            public String getText() {
                return getTextBoxWidget().getText();
            }

            @Override
            public void setText(String text) {
                getTextBoxWidget().setText(text);
            }

            @Override
            void show(boolean show) {
                super.show(show);
                if (!revealText) {
                    if (isShown()) {
                        getTextBoxWidget().getElement().setAttribute("type", "text");
                    } else {
                        getTextBoxWidget().getElement().setAttribute("type", "password");
                    }
                }
            }
        };
        return watermark;
    }

    public void revealText(boolean reveal) {
        this.revealText = reveal;
        if (reveal) {
            getElement().setAttribute("type", "text");
        } else {
            getElement().setAttribute("type", "password");
            if (watermark != null) {
                watermark.show();
            }
        }
    }

}
