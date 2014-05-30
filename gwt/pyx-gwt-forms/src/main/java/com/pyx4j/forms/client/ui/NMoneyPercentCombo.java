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
 * Created on May 30, 2014
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.ui.NMoneyPercentCombo.MoneyPercentPanel;
import com.pyx4j.widgets.client.ITextWidget;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.TextBox;

public class NMoneyPercentCombo extends NTextFieldBase<IMoneyPercentAmount, MoneyPercentPanel, CMoneyPercentCombo> {

    private final ValueType defaultType;

    public NMoneyPercentCombo(CMoneyPercentCombo cComponent, ValueType defaultType) {
        super(cComponent);
        this.defaultType = defaultType;
    }

    @Override
    public void setNativeValue(IMoneyPercentAmount value) {
        String newValue = getCComponent().format(value);
        if (isViewable()) {
            if (value != null) {
                if (ValueType.Monetary.equals(value.valueType().getValue(defaultType))) {
                    // TODO - apply application-wide money formatting
                    newValue = "$" + newValue;
                } else {
                    newValue += "%";
                }
            }
            getViewer().setText(newValue);
        } else {
            getEditor().setValueType(value == null ? defaultType : value.valueType().getValue(defaultType));
            getEditor().setText(newValue);
        }
        NValueChangeEvent.fire(getCComponent(), newValue);
    }

    @Override
    protected MoneyPercentPanel createEditor() {
        return new MoneyPercentPanel();
    }

    protected ValueType getValueType() {
        ValueType result = getEditor().getValueType();
        return result == null ? defaultType : result;
    }

    class MoneyPercentPanel extends HorizontalPanel implements ITextWidget {

        private TextBox textBox;

        private ListBox typeSwitch;

        public MoneyPercentPanel() {
            setWidth("100%");
            add(getTextBox());
            add(getTypeSwitch());
        }

        private TextBox getTextBox() {
            if (textBox == null) {
                textBox = new TextBox();
                textBox.setWidth("100%");
            }
            return textBox;
        }

        private ListBox getTypeSwitch() {
            if (typeSwitch == null) {
                typeSwitch = new ListBox();
                typeSwitch.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        textBox.setText("");
                    }
                });
                for (ValueType type : ValueType.values()) {
                    typeSwitch.addItem(getCComponent().getValueTypeOptions()[type.ordinal()]);
                }
                typeSwitch.getElement().getStyle().setProperty("marginLeft", "6px");
                typeSwitch.getElement().getStyle().setProperty("verticalAlign", "top");
            }
            return typeSwitch;
        }

        protected void setValueType(ValueType type) {
            getTypeSwitch().setSelectedIndex(type.ordinal());
        }

        protected ValueType getValueType() {
            int idx = getTypeSwitch().getSelectedIndex();
            return idx >= 0 ? ValueType.values()[idx] : null;
        }

        @Override
        public HandlerRegistration addFocusHandler(FocusHandler handler) {
            final HandlerRegistration hr1 = getTextBox().addFocusHandler(handler);
            final HandlerRegistration hr2 = getTypeSwitch().addFocusHandler(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    hr1.removeHandler();
                    hr2.removeHandler();
                }
            };
        }

        @Override
        public HandlerRegistration addBlurHandler(BlurHandler handler) {
            final HandlerRegistration hr1 = getTextBox().addBlurHandler(handler);
            final HandlerRegistration hr2 = getTypeSwitch().addBlurHandler(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    hr1.removeHandler();
                    hr2.removeHandler();
                }
            };
        }

        @Override
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            final HandlerRegistration hr1 = getTextBox().addKeyDownHandler(handler);
            final HandlerRegistration hr2 = getTypeSwitch().addKeyDownHandler(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    hr1.removeHandler();
                    hr2.removeHandler();
                }
            };
        }

        @Override
        public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
            final HandlerRegistration hr1 = getTextBox().addKeyUpHandler(handler);
            final HandlerRegistration hr2 = getTypeSwitch().addKeyUpHandler(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    hr1.removeHandler();
                    hr2.removeHandler();
                }
            };
        }

        @Override
        public void setEnabled(boolean enabled) {
            getTextBox().setEnabled(enabled);
            getTypeSwitch().setEnabled(enabled);
        }

        @Override
        public boolean isEnabled() {
            return getTextBox().isEnabled();
        }

        @Override
        public void setEditable(boolean editable) {
            getTextBox().setEditable(editable);
            getTypeSwitch().setEditable(editable);
        }

        @Override
        public boolean isEditable() {
            return getTextBox().isEditable();
        }

        @Override
        public int getTabIndex() {
            return getTextBox().getTabIndex();
        }

        @Override
        public void setAccessKey(char key) {
            getTextBox().setAccessKey(key);
        }

        @Override
        public void setFocus(boolean focused) {
            getTextBox().setFocus(focused);
        }

        @Override
        public void setTabIndex(int index) {
            getTextBox().setTabIndex(index);
        }

        @Override
        public String getText() {
            return getTextBox().getText();
        }

        @Override
        public void setText(String text) {
            getTextBox().setText(text);
        }

        @Override
        public HandlerRegistration addChangeHandler(ChangeHandler handler) {
            final HandlerRegistration hr1 = getTextBox().addChangeHandler(handler);
            final HandlerRegistration hr2 = getTypeSwitch().addChangeHandler(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    hr1.removeHandler();
                    hr2.removeHandler();
                }
            };
        }
    }
}
