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

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.forms.client.ui.NSignature.SignaturePanel;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.ITextWidget;
import com.pyx4j.widgets.client.TextBox;

public class NSignature extends NTextFieldBase<ISignature, SignaturePanel, CSignature> {

    public NSignature(CSignature cComponent) {
        super(cComponent);
    }

    @Override
    protected SignaturePanel createEditor() {
        return new SignaturePanel();
    }

    @Override
    public void setNativeValue(ISignature value) {
        super.setNativeValue(value);
        if (value != null && getEditor() != null) {
            getEditor().checkBox.setValue(value.agreeBox().getValue());
        }
    }

    @Override
    public ISignature getNativeValue() throws java.text.ParseException {
        ISignature signature = super.getNativeValue();
        if (getEditor() != null) {
            signature.agreeBox().setValue(getEditor().checkBox.getValue());
        }
        return signature.duplicate();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setFocus(true);
                getCComponent().onEditingStop();
            }
        });

    }

    class SignaturePanel extends FlowPanel implements ITextWidget {

        private final CheckBox checkBox;

        private final TextBox textBox;

        public SignaturePanel() {

            checkBox = new CheckBox(getCComponent().getCheckBoxText());
            add(checkBox);
            add(new InlineHTML("&nbsp;"));
            textBox = new TextBox();
            add(textBox);

            switch (getCComponent().getSignatureType()) {
            case AgreeBox:
            case AgreeBoxAndFullName:

                checkBox.setVisible(true);

                if (getCComponent().getCheckBoxAnchorText() != null) {
                    Anchor checkBoxAnchor = new Anchor(getCComponent().getCheckBoxAnchorText(), getCComponent().getCheckBoxAnchorCommand());
                    add(checkBoxAnchor);
                }
                break;
            default:
                break;

            }

            switch (getCComponent().getSignatureType()) {
            case AgreeBox:
                textBox.setVisible(false);
                break;
            case AgreeBoxAndFullName:
            case FullName:
                textBox.setVisible(true);
                textBox.setWidth("100%");
                break;
            case Initials:
                textBox.setVisible(true);
                textBox.setWidth("4em");
                break;

            }

        }

        @Override
        public HandlerRegistration addFocusHandler(FocusHandler handler) {
            return textBox.addFocusHandler(handler);
        }

        @Override
        public HandlerRegistration addBlurHandler(BlurHandler handler) {
            return textBox.addBlurHandler(handler);
        }

        @Override
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            return textBox.addKeyDownHandler(handler);
        }

        @Override
        public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
            return textBox.addKeyUpHandler(handler);
        }

        @Override
        public void setEnabled(boolean enabled) {
            textBox.setEnabled(enabled);
            checkBox.setEnabled(enabled);
        }

        @Override
        public boolean isEnabled() {
            return textBox.isEnabled();
        }

        @Override
        public void setEditable(boolean editable) {
            textBox.setEditable(editable);
            checkBox.setEditable(editable);
        }

        @Override
        public boolean isEditable() {
            return textBox.isEditable();
        }

        @Override
        public int getTabIndex() {
            return textBox.getTabIndex();
        }

        @Override
        public void setAccessKey(char key) {
            textBox.setAccessKey(key);
        }

        @Override
        public void setFocus(boolean focused) {
            textBox.setFocus(focused);
        }

        @Override
        public void setTabIndex(int index) {
            textBox.setTabIndex(index);
        }

        @Override
        public String getText() {
            return textBox.getText().trim();
        }

        @Override
        public void setText(String text) {
            textBox.setText(text);
        }

        @Override
        public HandlerRegistration addChangeHandler(ChangeHandler handler) {
            return textBox.addChangeHandler(handler);
        }
    }
}
