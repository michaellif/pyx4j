/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-12-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.superselector;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.forms.client.ui.IFormat;

public abstract class SuperSelector<C> extends Composite {

    private final TextBox inputTextBox;

    private final FlowPanel selectedItemsContainerPanel;

    private final List<SelectedItem<C>> selectedWidgets;

    private final int minInputBoxWidth = 50;

    private final IFormat<C> format;

    // @formatter:off
    // these are fixes for width so that the inputext will not get to next row
    // TODO i'm not sure with this is happending but this shouldn't exits and must be fixed
    private final static int FIX_INPUT_WIDTH_CALCULATION = 20;
    private final static int FIX_ROW_WIDTH_CALCULATION = 5;
    // @formatter:on

    private boolean isFocused;

    /**
     * The format will be used to parse input and convert it to stuff, and to display selected items. if convert fails it can return "null" to avoid adding an
     * item.
     */
    public SuperSelector(IFormat<C> format) {
        this.format = format;
        FlowPanel panel = new FlowPanel();

        selectedItemsContainerPanel = new FlowPanel();
        selectedItemsContainerPanel.getElement().getStyle().setDisplay(Display.INLINE);
        panel.add(selectedItemsContainerPanel);

        inputTextBox = new TextBox();
        inputTextBox.getElement().getStyle().setDisplay(Display.INLINE);

        inputTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode() || KeyCodes.KEY_TAB == event.getNativeEvent().getKeyCode()) {
                    addItemFromInputBox();
                    event.preventDefault();
                } else if (KeyCodes.KEY_BACKSPACE == event.getNativeEvent().getKeyCode()) {
                    if ("".equals(inputTextBox.getValue())) {
                        removeLastItem();
                    }
                }
            }
        });
        inputTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                onInputChanged(inputTextBox.getValue());
            }
        });
        inputTextBox.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                SuperSelector.this.isFocused = true;
                SuperSelector.this.onFocus();
            }
        });
        inputTextBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                SuperSelector.this.isFocused = false;
                SuperSelector.this.onBlur();
            }
        });

        panel.add(inputTextBox);

        initWidget(panel);
        selectedWidgets = new LinkedList<SelectedItem<C>>();
    }

    public void setInput(String input) {
        inputTextBox.setValue(input);
        onInputChanged(input);
    }

    public List<C> getSelectedItems() {
        LinkedList<C> items = new LinkedList<C>();
        for (SelectedItem<C> i : selectedWidgets) {
            items.add(i.getItem());
        }
        return items;
    }

    /**
     * Can be overridden to make validation. Warning: <code>null</code> will be silently ignored.
     */
    public void addItem(C item) {
        if (item == null) {
            return;
        }

        SelectedItem<C> w = new SelectedItem<C>(this.format, this, item);
        selectedWidgets.add(w);
        selectedItemsContainerPanel.add(w);

        updateInputTextBoxWidth();
    }

    public final void removeItem(C item) {
        SelectedItem<C> itemContainerWidget = null;
        for (SelectedItem<C> w : selectedWidgets) {
            if (w.getItem().equals(item)) {
                itemContainerWidget = w;
                break;
            }
        }
        if (itemContainerWidget != null) {
            selectedWidgets.remove(itemContainerWidget);
            selectedItemsContainerPanel.remove(itemContainerWidget);
        }

        updateInputTextBoxWidth();
    }

    protected void onInputChanged(String newInput) {
    }

    protected void onFocus() {

    }

    protected void onBlur() {
    }

    protected boolean isFocused() {
        return isFocused;
    }

    private void addItemFromInputBox() {
        try {
            addItem(format.parse(inputTextBox.getText()));
            inputTextBox.setText("");
        } catch (ParseException e) {
            // TODO deal with this (i.e. render a popup or something like that
        }
    }

    private void removeLastItem() {
        if (!selectedWidgets.isEmpty()) {
            removeItem(selectedWidgets.get(selectedWidgets.size() - 1).getItem());
        }
    }

    private void updateInputTextBoxWidth() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int maxRowWidth = elementWidth(SuperSelector.this.getElement());
                int rowWidth = 0;
                for (SelectedItem<C> w : selectedWidgets) {
                    rowWidth += elementWidth(w.getElement()) + FIX_ROW_WIDTH_CALCULATION;
                }
                rowWidth = rowWidth % maxRowWidth;
                int proposedInputWidth = maxRowWidth - rowWidth;
                int effectiveInputWidth = (proposedInputWidth < minInputBoxWidth ? maxRowWidth : proposedInputWidth) - FIX_INPUT_WIDTH_CALCULATION;
                inputTextBox.getElement().getStyle().setWidth(effectiveInputWidth, Unit.PX);
            }
        });
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                onRedraw();
            }
        });
    }

    private static int elementWidth(Element el) {
        return el.getAbsoluteRight() - el.getAbsoluteLeft();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        updateInputTextBoxWidth();
    }

    protected void onRedraw() {

    }

}
