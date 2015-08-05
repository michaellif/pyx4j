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
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class CheckGroup<E> extends OptionGroup<E> {

    private final Map<E, CheckGroupButton> buttons;

    public CheckGroup(Layout layout) {
        super(layout);

        this.buttons = new LinkedHashMap<E, CheckGroupButton>();
    }

    public void setValue(Collection<E> value) {
        setValue(value, true);
    }

    public void setValue(Collection<E> value, boolean fireChangeEvent) {
        for (OptionGroupButton button : getButtons().values()) {
            button.setValue(Boolean.FALSE);
        }
        if (value != null) {
            for (E item : value) {
                @SuppressWarnings("unchecked")
                CheckGroupButton selectedButton = (CheckGroupButton) getButtons().get(item);
                if (selectedButton != null) {
                    selectedButton.setValue(Boolean.TRUE);
                    if (fireChangeEvent) {
                        fireEvent(new ValueChangeEvent<Boolean>(fireChangeEvent) {
                        });
                    }
                }
            }
        }
        applySelectionStyles();
    }

    @Override
    public void setOptions(List<E> options) {
        clear();
        buttons.clear();

        for (final E option : options) {
            CheckGroupButton button = new CheckGroupButton(getFormatter().format(option));
            buttons.put(option, button);
            button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        addStyleDependentName(WidgetsTheme.StyleDependent.active.name());
                    } else {
                        removeStyleDependentName(WidgetsTheme.StyleDependent.active.name());
                    }
                    CheckGroup.this.fireEvent(event);
                }
            });

            button.addFocusHandler(focusHandlerManager);
            button.addBlurHandler(focusHandlerManager);
            add(button);
        }
    }

    public Collection<E> getValue() {
        List<E> value = new ArrayList<>();
        for (E item : getButtons().keySet()) {
            if (getButtons().get(item).getValue()) {
                value.add(item);
            }
        }
        return value;
    }

    private class CheckGroupButton extends OptionGroupButton {

        public CheckGroupButton(SafeHtml label) {
            super(label);
        }

        @Override
        protected void initCheckBox(SafeHtml label) {
            {
                this.checkBox = new CheckBox(label);
                initWidget(checkBox);
                setStyleName(WidgetsTheme.StyleName.OptionGroupItem.name());
            }
        }

    }

    @Override
    public Map<E, ? extends OptionGroupButton> getButtons() {
        return buttons;
    }
}
