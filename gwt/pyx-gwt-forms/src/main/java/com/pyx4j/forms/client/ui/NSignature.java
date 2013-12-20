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
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.forms.client.ui.NSignature.SignaturePanel;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.TextBox;

public class NSignature extends NFocusField<ISignature, SignaturePanel, CSignature, Label> {

    private ISignature signature;

    public NSignature(CSignature cComponent) {
        super(cComponent);
    }

    @Override
    protected SignaturePanel createEditor() {
        return new SignaturePanel();
    }

    @Override
    protected Label createViewer() {
        return new Label();
    }

    @Override
    public void init() {
        super.init();
        if (getEditor() != null) {
            getEditor().init(SignatureFormat.None);
        }
    }

    @Override
    public void setNativeValue(ISignature value) {
        if (value == null || value.isNull()) {
            signature = null;
            if (getEditor() != null) {
                getEditor().checkBox.setValue(null);
                getEditor().textBox.setValue(null);
                getEditor().init(SignatureFormat.None);
            }
            if (getViewer() != null) {
                getViewer().setText(null);
            }
        } else {
            signature = value.duplicate();
            if (getEditor() != null) {
                getEditor().checkBox.setValue(signature.agree().getValue());
                switch (signature.signatureFormat().getValue()) {
                case AgreeBox:
                case None:
                    getEditor().textBox.setValue(null);
                    break;
                case AgreeBoxAndFullName:
                case FullName:
                    getEditor().textBox.setValue(signature.fullName().getValue());
                    break;
                case Initials:
                    getEditor().textBox.setValue(signature.initials().getValue());
                    break;
                }
            }
            if (getViewer() != null) {
                if (signature.agree().isBooleanTrue()) {
                    switch (signature.signatureFormat().getValue()) {
                    case AgreeBox:
                        getViewer().setText(null);
                        break;
                    case AgreeBoxAndFullName:
                    case FullName:
                        getViewer().setText(signature.fullName().getValue());
                        break;
                    case Initials:
                        getViewer().setText(signature.initials().getValue());
                        break;
                    case None:
                        break;
                    }
                } else {
                    getViewer().setText(null);
                }
            }
            getEditor().init(signature.signatureFormat().getValue());
        }
    }

    @Override
    public ISignature getNativeValue() throws java.text.ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            if (getEditor() != null && signature != null) {
                signature.agree().setValue(getEditor().checkBox.getValue());
                switch (signature.signatureFormat().getValue()) {
                case AgreeBox:
                case None:
                    signature.fullName().setValue(null);
                    signature.initials().setValue(null);
                    break;
                case AgreeBoxAndFullName:
                case FullName:
                    signature.fullName().setValue(getEditor().textBox.getText());
                    signature.initials().setValue(null);
                    break;
                case Initials:
                    signature.initials().setValue(getEditor().textBox.getText());
                    signature.fullName().setValue(null);
                    break;
                }
            }
            if (signature == null) {
                return null;
            } else {
                return signature.duplicate();
            }
        }
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

    class SignaturePanel extends FlowPanel implements IFocusWidget {

        private final CheckBox checkBox;

        private final TextBox textBox;

        private final Anchor checkBoxAnchor;

        public SignaturePanel() {

            checkBox = new CheckBox(getCComponent().getCheckBoxText());
            add(checkBox);
            add(new InlineHTML("&nbsp;"));
            checkBoxAnchor = new Anchor(null, new Command() {

                @Override
                public void execute() {
                    if (getCComponent().getCheckBoxAnchorCommand() != null) {
                        getCComponent().getCheckBoxAnchorCommand().execute();
                    }
                }
            });
            add(checkBoxAnchor);
            textBox = new TextBox();
            add(textBox);

        }

        public void init(SignatureFormat format) {
            switch (format) {
            case AgreeBox:
            case AgreeBoxAndFullName:
                checkBox.setVisible(true);
                if (getCComponent().getCheckBoxAnchorText() != null) {
                    checkBoxAnchor.setText(getCComponent().getCheckBoxAnchorText());
                }
                break;
            default:
                break;

            }

            switch (format) {
            case AgreeBox:
                setVisible(true);
                textBox.setVisible(false);
                break;
            case AgreeBoxAndFullName:
            case FullName:
                setVisible(true);
                textBox.setVisible(true);
                textBox.setWidth("100%");
                break;
            case Initials:
                setVisible(true);
                textBox.setVisible(true);
                textBox.setWidth("4em");
                break;
            case None:
                setVisible(false);
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

    }

}
