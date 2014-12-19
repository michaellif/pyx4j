/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2014
 * @author stanp
 */
package com.propertyvista.common.client.ui.components.c;

import java.text.ParseException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.forms.client.ui.NFocusField;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.TextBox;

public class NProvinceComboBox extends NFocusField<String, IFocusWidget, CProvinceComboBox, HTML> {

    private ComboEditor comboEditor;

    private boolean textMode;

    // from NCombpoBox
    private String value;

    private boolean deferredSetSelectedStarted = false;

    public NProvinceComboBox(CProvinceComboBox cComponent) {
        super(cComponent);
        setTextMode(true);
    }

    private ComboEditor ensureEditor() {
        return comboEditor == null ? comboEditor = new ComboEditor() : comboEditor;
    }

    private TextBox<String> getTextEditor() {
        return ensureEditor().textBox;
    }

    private ListBox getComboEditor() {
        return ensureEditor().listBox;
    }

    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
        ensureEditor().setTextMode(textMode);
    }

    public boolean isTextMode() {
        return textMode;
    }

    @Override
    public void setNativeValue(String newValue) {
        value = newValue;
        String textValue = getCComponent().formatValue(newValue);
        if (isViewable()) {
            getViewer().setText(textValue);
        } else {
            // set text editor value
            if (!textValue.equals(getTextEditor().getValue())) {
                getTextEditor().setValue(textValue);
            }
            // set combo editor value
            if (value != null && !isTextMode() && ((getCComponent().getOptions() == null) || !getCComponent().getOptions().contains(value))) {
                refreshOptions();
            } else {
                setSelectedValue(value);
            }
        }
        NativeValueChangeEvent.fire(getCComponent(), newValue);
    }

    @Override
    public String getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
        } else if (textMode) {
            // trim user input before parsing
            value = getCComponent().parseValue(getTextEditor().getValue().trim());
        } else {
            value = getValueByNativeOptionIndex(getComboEditor().getSelectedIndex());
        }
        return value;
    }

    @Override
    protected IFocusWidget createEditor() {
        return ensureEditor();
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    public void refreshOptions() {
        getComboEditor().clear();

        if (getCComponent().getOptions() != null) {
            for (String o : getCComponent().getConvertedOptions()) {
                getComboEditor().addItem(getCComponent().formatValue(o));
            }
            // Clear selection if not found in options
            if ((this.value != null) && (getNativeOptionIndex(this.value) == -1)) {
                getCComponent().setValue(null, false);
            }
        }
        setSelectedValue(this.value);
    }

    private void setSelectedValue(String value) {
        this.value = value;
        if (!deferredSetSelectedStarted) {
            deferredSetSelectedStarted = true;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    deferredSetSelectedStarted = false;
                    getComboEditor().setSelectedIndex(getNativeOptionIndex(NProvinceComboBox.this.value));
                }
            });
        }
    }

    private String getValueByNativeOptionIndex(int index) {
        return (getCComponent().getOptions() != null && index >= 0) ? getCComponent().getConvertedOptions().get(index) : null;
    }

    private int getNativeOptionIndex(String opt) {
        return (getCComponent().getOptions() != null && opt != null) ? getCComponent().getConvertedOptions().indexOf(opt) : -1;
    }

    static class ComboEditor extends SimplePanel implements IFocusWidget {

        private StringBox textBox;

        private ListBox listBox;

        private IFocusWidget editor;

        public ComboEditor() {
            this(true);
        }

        public ComboEditor(boolean textMode) {
            ensureTextBox();
            ensureListBox();
            setTextMode(textMode);
        }

        private StringBox ensureTextBox() {
            if (textBox == null) {
                textBox = new StringBox();
                textBox.setWidth("100%");
            }
            return textBox;
        }

        private ListBox ensureListBox() {
            if (listBox == null) {
                listBox = new ListBox();
                listBox.setWidth("100%");
            }
            return listBox;
        }

        public void setTextMode(boolean textMode) {
            setWidget(editor = textMode ? ensureTextBox() : ensureListBox());
        }

        @Override
        public void setEnabled(boolean enabled) {
            textBox.setEnabled(enabled);
            listBox.setEnabled(enabled);
        }

        @Override
        public boolean isEnabled() {
            return editor.isEnabled();
        }

        @Override
        public void setEditable(boolean editable) {
            textBox.setEditable(editable);
            listBox.setEditable(editable);
        }

        @Override
        public boolean isEditable() {
            return editor.isEditable();
        }

        @Override
        public int getTabIndex() {
            return editor.getTabIndex();
        }

        @Override
        public void setAccessKey(char key) {
            textBox.setAccessKey(key);
            listBox.setAccessKey(key);
        }

        @Override
        public void setFocus(boolean focused) {
            textBox.setFocus(focused);
            listBox.setFocus(focused);
        }

        @Override
        public void setTabIndex(int index) {
            textBox.setTabIndex(index);
            listBox.setTabIndex(index);
        }

        @Override
        public void addStyleName(String styleName) {
            textBox.addStyleName(styleName);
            listBox.addStyleName(styleName);
        }

        @Override
        public void addStyleDependentName(String styleSuffix) {
            textBox.addStyleDependentName(styleSuffix);
            listBox.addStyleDependentName(styleSuffix);
        }

        @Override
        public void removeStyleDependentName(String styleSuffix) {
            textBox.removeStyleDependentName(styleSuffix);
            listBox.removeStyleDependentName(styleSuffix);
        }

        @Override
        public void setDebugId(IDebugId debugId) {
            ensureDebugId(debugId.debugId());
        }

        @Override
        protected void onEnsureDebugId(String baseID) {
            // pass through non-empty debug id
            if (!CommonsStringUtils.isEmpty(baseID)) {
                ensureDebugId(null);
                textBox.ensureDebugId(baseID);
                listBox.ensureDebugId(baseID);
            }
        }

        @Override
        public HandlerRegistration addFocusHandler(FocusHandler handler) {
            final HandlerRegistration hr1 = textBox.addFocusHandler(handler);
            final HandlerRegistration hr2 = listBox.addFocusHandler(handler);
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
            final HandlerRegistration hr1 = textBox.addBlurHandler(handler);
            final HandlerRegistration hr2 = listBox.addBlurHandler(handler);
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
            final HandlerRegistration hr1 = textBox.addKeyDownHandler(handler);
            final HandlerRegistration hr2 = listBox.addKeyDownHandler(handler);
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
            final HandlerRegistration hr1 = textBox.addKeyUpHandler(handler);
            final HandlerRegistration hr2 = listBox.addKeyUpHandler(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    hr1.removeHandler();
                    hr2.removeHandler();
                }
            };
        }

        @Override
        public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
            final HandlerRegistration hr1 = textBox.addKeyPressHandler(handler);
            final HandlerRegistration hr2 = listBox.addKeyPressHandler(handler);
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
