/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IFormat;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IFocusWidget;

// TODO maybe add generic option selector widget
public class SubsetSelector<OPTION_TYPE> extends SimplePanel implements IFocusWidget, HasValueChangeHandlers<Set<OPTION_TYPE>>, HasValue<Set<OPTION_TYPE>> {

    public enum Layout {

        Horizontal, Vertical;

    }

    private final Map<OPTION_TYPE, HasValue<Boolean>> optionsState;

    private final CellPanel panel;

    private final GroupFocusHandler focusHandlerManager;

    private boolean enabled;

    private boolean editable;

    /**
     * @param layout
     *            self explanatory
     * @param options
     *            set of options that can be selected (please note that <code>options</code> of ITEM_TYPE are expected to be immutable)
     */
    public SubsetSelector(Layout layout, Set<OPTION_TYPE> options) {
        this(layout, options, new IFormat<OPTION_TYPE>() {

            @Override
            public String format(OPTION_TYPE value) {
                return value.toString();
            }

            @Override
            public OPTION_TYPE parse(String string) throws ParseException {
                throw new IllegalStateException("not required");
            }
        });
    }

    public SubsetSelector(Layout layout, Set<OPTION_TYPE> options, IFormat<OPTION_TYPE> format) {
        if (layout == Layout.Horizontal) {
            this.panel = new HorizontalPanel();
        } else if (layout == Layout.Vertical) {
            this.panel = new VerticalPanel();
        } else {
            throw new IllegalArgumentException("layout is not specified");
        }
        this.optionsState = new HashMap<OPTION_TYPE, HasValue<Boolean>>();

        this.focusHandlerManager = new GroupFocusHandler(this);
        for (OPTION_TYPE option : options) {
            final CheckBox checkBox = new CheckBox(format.format(option));
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    SubsetSelector.this.fireEvent(event);
                }

            });
            this.panel.add(checkBox);
            if (layout == Layout.Horizontal) {
                checkBox.getElement().getStyle().setPaddingRight(1d, Unit.EM);
            }
            this.optionsState.put(option, checkBox);
        }
        this.setWidget(panel);
    }

    @Override
    public Set<OPTION_TYPE> getValue() {
        Set<OPTION_TYPE> selectedOptions = new HashSet<OPTION_TYPE>();
        for (Map.Entry<OPTION_TYPE, HasValue<Boolean>> entry : optionsState.entrySet()) {
            if (entry.getValue().getValue()) {
                selectedOptions.add(entry.getKey());
            }
        }
        return selectedOptions;
    }

    @Override
    public void setValue(Set<OPTION_TYPE> value) {
        setOptionsState(value);
    }

    @Override
    public void setValue(Set<OPTION_TYPE> value, boolean fireEvents) {
        setOptionsState(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        // TODO
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        // TODO
    }

    @Override
    public boolean isEditable() {
        return editable;
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
    public int getTabIndex() {
        if (optionsState.values().iterator().hasNext()) {
            Object next = optionsState.values().iterator().next();
            if (next instanceof Focusable) {
                return ((Focusable) next).getTabIndex();
            }
        }
        return -1;
    }

    @Override
    public void setTabIndex(int index) {
        if (optionsState.values().iterator().hasNext()) {
            for (Object o : optionsState.values()) {
                if (o instanceof Focusable) {
                    ((Focusable) o).setTabIndex(index);
                }
            }
        }
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setFocus(boolean focused) {
        if (focused & optionsState.values().iterator().hasNext()) {
            Object next = optionsState.values().iterator().next();
            // this looks strange but it's needed if we want to create the widget out of some kind of generic boolean selector widget
            if (next instanceof Focusable) {
                ((Focusable) next).setFocus(true);
            }
        } else {
            for (Object o : optionsState.values()) {
                if (o instanceof Focusable) {
                    ((Focusable) o).setFocus(false);
                }
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<OPTION_TYPE>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void setOptionsState(Set<OPTION_TYPE> selectedOptions) {
        for (HasValue<Boolean> stateHolder : optionsState.values()) {
            stateHolder.setValue(false, false);
        }
        if (selectedOptions != null) {
            for (OPTION_TYPE selectedOption : selectedOptions) {
                HasValue<Boolean> stateHolder = optionsState.get(selectedOption);
                if (stateHolder != null) {
                    stateHolder.setValue(true, false);
                } else {
                    throw new IllegalArgumentException("its impossilbe to set an unknown option: '" + selectedOption + "'");
                }
            }
        }
    }
}
