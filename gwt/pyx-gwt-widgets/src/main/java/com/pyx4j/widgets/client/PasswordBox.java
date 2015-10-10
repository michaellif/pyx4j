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
 */
package com.pyx4j.widgets.client;

import java.text.ParseException;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.widgets.client.PasswordBox.PasswordStrengthRule.PasswordStrengthVerdict;
import com.pyx4j.widgets.client.event.shared.PasteEvent;
import com.pyx4j.widgets.client.event.shared.PasteHandler;

public class PasswordBox extends FlowPanel implements IValueBoxWidget<String> {

    private static final I18n i18n = I18n.get(PasswordBox.class);

    private final PasswordTextBox textBox;

    private boolean revealText;

    private PasswordStrengthRule passwordStrengthRule;

    private PasswordStrengthWidget passwordStrengthWidget;

    public PasswordBox() {
        textBox = new PasswordTextBox();
        textBox.setTextBoxWidget(new com.google.gwt.user.client.ui.PasswordTextBox());

        add(textBox);

        revealText(false);
    }

    public void setPasswordStrengthRule(PasswordStrengthRule rule) {
        this.passwordStrengthRule = rule;
        if (rule != null && getWidgetCount() == 1) {
            passwordStrengthWidget = new PasswordStrengthWidget();
            add(passwordStrengthWidget);
        }
        if (passwordStrengthWidget != null) {
            passwordStrengthWidget.ratePassword();
        }
    }

    public void revealText(boolean reveal) {
        this.revealText = reveal;
        if (reveal) {
            textBox.getElement().setAttribute("type", "text");
        } else {
            textBox.getElement().setAttribute("type", "password");
        }
    }

    private class PasswordTextBox extends ValueBoxBase<String> {

        public PasswordTextBox() {
            setFormatter(new IFormatter<String, String>() {

                @Override
                public String format(String value) {
                    if (value == null) {
                        value = "";
                    }
                    return value;
                }
            });

            setParser(new IParser<String>() {

                @Override
                public String parse(String string) throws ParseException {
                    if (CommonsStringUtils.isEmpty(string)) {
                        return null;
                    }
                    return string;
                }

            });
        }

        @Override
        protected void setText(String text, boolean watermark) {
            if (!revealText) {
                if (watermark && !BrowserType.isIE8()) {
                    getTextBoxWidget().getElement().setAttribute("type", "text");
                } else {
                    getTextBoxWidget().getElement().setAttribute("type", "password");
                }
            }
            super.setText(text, watermark);
        }

        @Override
        protected void updateTextBox() {
            super.updateTextBox();
            if (passwordStrengthWidget != null) {
                passwordStrengthWidget.ratePassword();
            }
        }

    }

    public class PasswordStrengthWidget extends FlowPanel {

        private final Label label;

        private final SimplePanel progressMarker;

        private PasswordStrengthWidget() {
            getElement().getStyle().setPosition(Position.RELATIVE);
            Label caption = new Label(i18n.tr("Password Strength:"));
            add(caption);

            label = new Label();
            label.getElement().getStyle().setPosition(Position.ABSOLUTE);
            label.getElement().getStyle().setRight(0, Unit.PX);
            label.getElement().getStyle().setTop(0, Unit.PX);
            add(label);

            SimplePanel progressHolder = new SimplePanel();
            progressHolder.setWidth("100%");
            progressHolder.getElement().getStyle().setProperty("backgroundColor", "#e0e0e0");
            add(progressHolder);

            progressMarker = new SimplePanel();
            progressMarker.setHeight("4px");
            progressMarker.getElement().getStyle().setMarginBottom(4, Unit.PX);
            progressHolder.setWidget(progressMarker);

            textBox.addKeyUpHandler(new KeyUpHandler() {

                @Override
                public void onKeyUp(KeyUpEvent event) {
                    ratePassword();
                }
            });
            textBox.addChangeHandler(new ChangeHandler() {

                @Override
                public void onChange(ChangeEvent event) {
                    ratePassword();
                }
            });
            textBox.addPasteHandler(new PasteHandler() {

                @Override
                public void onPaste(PasteEvent event) {
                    ratePassword();
                }
            });
        }

        public void setValue(PasswordStrengthVerdict verdict) {
            if (verdict == null) {
                label.setText("");
                progressMarker.setWidth("0%");
                progressMarker.getElement().getStyle().setProperty("backgroundColor", "#e0e0e0");
            } else {
                label.setText(verdict.toString());
                String color = "red";
                switch (verdict) {
                case Invalid:
                    color = "#CC0000";
                    progressMarker.setWidth("0%");
                    break;
                case TooShort:
                    color = "#808080";
                    progressMarker.setWidth("20%");
                    break;
                case Weak:
                    color = "#da5301";
                    progressMarker.setWidth("40%");
                    break;
                case Fair:
                    color = "#ccbe00";
                    progressMarker.setWidth("60%");
                    break;
                case Good:
                    color = "#1e91ce";
                    progressMarker.setWidth("80%");
                    break;
                case Strong:
                    color = "#008000";
                    progressMarker.setWidth("100%");
                    break;
                }
                progressMarker.getElement().getStyle().setProperty("backgroundColor", color);
                label.getElement().getStyle().setColor(color);
            }

        }

        public void ratePassword() {
            setValue(passwordStrengthRule != null
                    ? passwordStrengthRule.getPasswordVerdict(textBox.isWatermarkShown() ? "" : textBox.getTextBoxWidget().getText()) : null);
        }

    }

    public static interface PasswordStrengthRule {

        @com.pyx4j.i18n.annotations.I18n
        public enum PasswordStrengthVerdict {

            Invalid,

            TooShort,

            Weak,

            Fair,

            Good,

            Strong;

            @Override
            public String toString() {
                return I18nEnum.toString(this);
            }
        }

        public PasswordStrengthVerdict getPasswordVerdict(String password);

    }

    @Override
    public void setEnabled(boolean enabled) {
        textBox.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return textBox.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        textBox.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return textBox.isEditable();
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        textBox.setDebugId(debugId);
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
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return textBox.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return textBox.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBox.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBox.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textBox.addKeyPressHandler(handler);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return textBox.addValueChangeHandler(handler);
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public void setValue(String value) {
        textBox.setValue(value);
    }

    @Override
    public void setParser(IParser<String> parser) {
        textBox.setParser(parser);
    }

    @Override
    public void setFormatter(IFormatter<String, String> formatter) {
        textBox.setFormatter(formatter);
    }

    @Override
    public boolean isParsedOk() {
        return textBox.isParsedOk();
    }

    @Override
    public String getParseExceptionMessage() {
        return textBox.getParseExceptionMessage();
    }

    @Override
    public void setWatermark(String watermark) {
        textBox.setWatermark(watermark);
    }

    @Override
    public String getWatermark() {
        return textBox.getWatermark();
    }
}
