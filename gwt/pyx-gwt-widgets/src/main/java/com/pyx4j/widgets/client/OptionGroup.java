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
 * Created on Dec 15, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public abstract class OptionGroup<E> extends FlowPanel implements IFocusWidget, HasValueChangeHandlers<E> {

    public enum Layout {
        VERTICAL, HORIZONTAL;
    }

    private final Map<E, OptionGroupButton> buttons;

    private boolean enabled = true;

    private boolean editable = true;

    final String uniqueId;

    private IFormatter<E, SafeHtml> formatter;

    protected final GroupFocusHandler focusHandlerManager;

    public OptionGroup(Layout layout) {
        uniqueId = Document.get().createUniqueId();

        focusHandlerManager = new GroupFocusHandler(this);

        setStyleName(WidgetsTheme.StyleName.OptionGroup.name());

        if (layout == Layout.HORIZONTAL) {
            addStyleDependentName(WidgetsTheme.StyleDependent.horizontal.name());
        } else if (layout == Layout.VERTICAL) {
            addStyleDependentName(WidgetsTheme.StyleDependent.vertical.name());
        }

        buttons = new LinkedHashMap<E, OptionGroupButton>();

    }

    abstract public void setOptions(List<E> options);

    public Map<E, OptionGroupButton> getButtons() {
        return buttons;
    }

    public void setFormatter(IFormatter<E, SafeHtml> formatter) {
        this.formatter = formatter;
    }

    public IFormatter<E, SafeHtml> getFormatter() {
        if (formatter == null) {
            formatter = new IFormatter<E, SafeHtml>() {

                @Override
                public SafeHtml format(E value) {
                    return SafeHtmlUtils.fromTrustedString(value.toString());
                }
            };
        }
        return formatter;
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
        for (OptionGroupButton b : buttons.values()) {
            b.setEnabled(enabled);
        }
    }

    protected void applySelectionStyles() {
        for (OptionGroupButton button : buttons.values()) {
            if (button.getValue()) {
                button.addStyleDependentName(WidgetsTheme.StyleDependent.active.name());
            } else {
                button.removeStyleDependentName(WidgetsTheme.StyleDependent.active.name());
            }
        }
    }

    @Override
    public void setFocus(boolean focused) {
        if (focused) {
            buttons.values().iterator().next().setFocus(true);
        } else {
            for (OptionGroupButton b : buttons.values()) {
                b.setFocus(false);
            }
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        for (Entry<E, ? extends OptionGroupButton> me : buttons.entrySet()) {
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
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return null;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub

    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}