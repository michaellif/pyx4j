/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 24, 2012
 * @author igor
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RadioGroup<E> extends SimplePanel implements IFocusWidget, HasValueChangeHandlers<E> {

    public enum Layout {
        VERTICAL, HORISONTAL;
    }

    private static int uniqueGroupId = 0;

    private boolean enabled = true;

    private boolean editable = true;

    private static class OptionRadioButton extends RadioButton {

        boolean optionEnabled;

        public OptionRadioButton(String name, SafeHtml label) {
            super(name, label);
            optionEnabled = true;
        }

    }

    private final Map<E, OptionRadioButton> buttons = new LinkedHashMap<E, OptionRadioButton>();

    private final GroupFocusHandler focusHandlerManager;

    private final Panel panel;

    private final String groupName;

    public RadioGroup(Layout layout) {
        if (layout == Layout.HORISONTAL) {
            panel = new HorizontalPanel();
        } else {
            panel = new VerticalPanel();
        }

        this.setWidget(panel);

        groupName = "rb" + (uniqueGroupId++);

        focusHandlerManager = new GroupFocusHandler(this);

        setStyleName(DefaultWidgetsTheme.StyleName.RadioGroup.name());
    }

    public void setOptions(List<E> options) {
        panel.clear();
        buttons.clear();

        for (final E option : options) {
            OptionRadioButton button = new OptionRadioButton(groupName, format(option));
            button.setStyleName(DefaultWidgetsTheme.StyleName.RadioGroupItem.name());
            buttons.put(option, button);
            button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        applySelectionStyles();
                        RadioGroup.this.fireEvent(event);
                    }
                }
            });

            button.addFocusHandler(focusHandlerManager);
            button.addBlurHandler(focusHandlerManager);
            panel.add(button);
            if (panel instanceof VerticalPanel) {
                button.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                ((VerticalPanel) panel).setCellHeight(button, "100%");
                ((VerticalPanel) panel).setCellWidth(button, "100%");
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setGroupEnabled(this.isEnabled() && this.isEditable());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        setGroupEnabled(this.isEnabled() && this.isEditable());
    }

    private void setGroupEnabled(boolean enabled) {
        for (OptionRadioButton b : buttons.values()) {
            b.setEnabled(b.optionEnabled && enabled);
        }
    }

    public void setValue(E value) {
        RadioButton selectedButton = buttons.get(value);
        if (selectedButton != null) {
            selectedButton.setValue(Boolean.TRUE);
        } else {
            for (RadioButton button : buttons.values()) {
                button.setValue(Boolean.FALSE);
            }
        }

        applySelectionStyles();
    }

    public void setOptionEnabled(E optionValue, boolean enabled) {
        OptionRadioButton button = buttons.get(optionValue);
        if (button != null) {
            button.optionEnabled = enabled;
            button.setEnabled(enabled);
            if (enabled) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
            } else {
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
            }
        }
    }

    private void applySelectionStyles() {
        for (RadioButton button : buttons.values()) {
            if (button.getValue()) {
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.active.name());
            } else {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.active.name());
            }
        }
    }

    public E getValue() {
        for (E value : buttons.keySet()) {
            if (buttons.get(value).getValue()) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void setFocus(boolean focused) {
        if (focused) {
            buttons.values().iterator().next().setFocus(true);
        } else {
            for (RadioButton b : buttons.values()) {
                b.setFocus(false);
            }
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        for (Map.Entry<E, OptionRadioButton> me : buttons.entrySet()) {
            me.getValue().ensureDebugId(baseID + "_" + getOptionDebugId(me.getKey()));
        }
    }

    public String getOptionDebugId(E option) {
        return option.toString();
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

    @Override
    public int getTabIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub

    }

    protected SafeHtml format(E value) {
        return SafeHtmlUtils.fromTrustedString(value.toString());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
