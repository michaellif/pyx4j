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
 * Created on 2010-04-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NativeRadioGroup<E> extends SimplePanel implements INativeEditableComponent<E> {

    private static int uniqueGroupId = 0;

    private final CRadioGroup<E> cComponent;

    private boolean enabled = true;

    private boolean editable = true;

    private boolean focusLost = true;

    private boolean groupFocus = false;

    private final Map<E, RadioButton> buttons = new LinkedHashMap<E, RadioButton>();

    private final HandlerManager focusHandlerManager;

    public NativeRadioGroup(CRadioGroup<E> cComponent) {
        this.cComponent = cComponent;
        Panel panel;
        if (cComponent.getLayout() == CRadioGroup.Layout.HORISONTAL) {
            panel = new HorizontalPanel();
        } else {
            panel = new VerticalPanel();
        }
        this.setWidget(panel);

        String groupName = "rb" + (uniqueGroupId++);

        focusHandlerManager = new HandlerManager(this);

        FocusHandler groupFocusHandler = new FocusHandler() {
            @Override
            public void onFocus(FocusEvent e) {
                focusLost = false;
                if (!groupFocus) {
                    groupFocus = true;
                    focusHandlerManager.fireEvent(e);
                }
            }
        };

        BlurHandler groupBlurHandler = new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent e) {
                focusLost = true;
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                    @Override
                    public void execute() {
                        if (groupFocus && focusLost) {
                            groupFocus = false;
                            focusHandlerManager.fireEvent(e);
                        }
                    }
                });

            }
        };

        for (final E option : cComponent.getOptions()) {
            RadioButton b = new RadioButton(groupName, cComponent.getFormat().format(option));
            buttons.put(option, b);
            b.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        NativeRadioGroup.this.cComponent.update(option);
                    }
                }
            });
            b.addFocusHandler(groupFocusHandler);
            b.addBlurHandler(groupBlurHandler);
            panel.add(b);
        }

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setButtonsEnabled(this.isEnabled() && this.isEditable());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        setButtonsEnabled(this.isEnabled() && this.isEditable());
    }

    private void setButtonsEnabled(boolean enabled) {
        for (RadioButton b : buttons.values()) {
            b.setEnabled(enabled);
        }
    }

    @Override
    public void setNativeValue(E value) {
        RadioButton b = buttons.get(value);
        if (b != null) {
            b.setValue(Boolean.TRUE);
        } else {
            for (RadioButton button : buttons.values()) {
                button.setValue(Boolean.FALSE);
            }
        }
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
        for (Map.Entry<E, RadioButton> me : buttons.entrySet()) {
            me.getValue().ensureDebugId(baseID + "_" + cComponent.getOptionDebugId(me.getKey()));
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public CComponent<?> getCComponent() {
        return cComponent;
    }

    @Override
    public void setValid(boolean valid) {
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

}
