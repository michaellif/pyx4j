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

public class TextBox extends com.google.gwt.user.client.ui.TextBox implements ITextWidget, WatermarkComponent {

    private TextWatermark watermark;

    public TextBox() {
        setStyleName(DefaultWidgetsTheme.StyleName.TextBox.name());
        addStyleDependentName(DefaultWidgetsTheme.StyleDependent.singleLine.name());
    }

    @Override
    public void setWatermark(String text) {
        if (watermark == null) {
            watermark = new TextWatermark(this) {

                @Override
                String getText() {
                    return TextBox.super.getText();
                }

                @Override
                void setText(String text) {
                    TextBox.super.setText(text);
                }
            };
        }
        watermark.setWatermark(text);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (watermark != null) {
            watermark.show();
        }
    }

    @Override
    public String getText() {
        if (watermark != null && watermark.isShown()) {
            return null;
        } else {
            return super.getText();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !isReadOnly();
    }

}
