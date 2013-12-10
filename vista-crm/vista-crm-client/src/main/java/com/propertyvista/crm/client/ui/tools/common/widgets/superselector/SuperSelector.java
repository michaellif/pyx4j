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
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.IFormat;

public abstract class SuperSelector<DataType> extends Composite {

    public enum Styles implements IStyleName {

        SuperSelectorStyle;

    }

    private final TextBox inputTextBox;

    private final FlowPanel selectedItemsContainerPanel;

    private final List<SelectedItemHolder<DataType>> selectedWidgets;

    private final int minInputBoxWidth = 50;

    private final IFormat<DataType> format;

    private final boolean allowSame;

    private boolean isFocused;

    private FlowPanel containerBox;

    private boolean inlineInput;

    /**
     * The format will be used to parse input and convert it to stuff, and to display selected items. if convert fails it can return "null" to avoid adding an
     * item.
     */
    public SuperSelector(IFormat<DataType> format, boolean allowSame, boolean inlineInput) {
        this.format = format;
        this.allowSame = allowSame;
        this.inlineInput = inlineInput;

        FlowPanel panel = new FlowPanel();
        panel.setStyleName(Styles.SuperSelectorStyle.name());

        inputTextBox = new TextBox();
        inputTextBox.getElement().getStyle().setDisplay(Display.BLOCK);
        inputTextBox.getElement().getStyle().setBorderStyle(BorderStyle.NONE);

        inputTextBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode() || KeyCodes.KEY_TAB == event.getNativeEvent().getKeyCode()) {
                    SuperSelector.this.onAddItemRequest();
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
                if (!SuperSelector.this.inlineInput) {
                    if (!selectedWidgets.isEmpty()) {
                        inputTextBox.getElement().getStyle().setDisplay(Display.NONE);
                        adjustInnerWidgetSizes();
                    }
                }
                SuperSelector.this.onBlur();
            }
        });

        selectedItemsContainerPanel = new FlowPanel();
        selectedItemsContainerPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!SuperSelector.this.inlineInput) {
                    inputTextBox.getElement().getStyle().setDisplay(Display.BLOCK);
                    inputTextBox.setFocus(true);
                    adjustInnerWidgetSizes();
                }
            }
        }, ClickEvent.getType());
        selectedItemsContainerPanel.getElement().getStyle().setDisplay(Display.BLOCK);
        selectedItemsContainerPanel.getElement().getStyle().setBorderWidth(0, Unit.PX);

        // this 'container box' is used to calculate client width of the panel that does not include padding
        if (inlineInput) {
            containerBox = new FlowPanel();
            containerBox.getElement().getStyle().setWidth(100, Unit.PCT);
            containerBox.getElement().getStyle().setHeight(100, Unit.PCT);
            containerBox.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
            containerBox.getElement().getStyle().setBorderWidth(0, Unit.PX);
            containerBox.getElement().getStyle().setPadding(0, Unit.PX);
            containerBox.getElement().getStyle().setMargin(0, Unit.PX);
            panel.add(containerBox);

            selectedItemsContainerPanel.getElement().getStyle().setDisplay(Display.INLINE);
            containerBox.add(selectedItemsContainerPanel);

            inputTextBox.getElement().getStyle().setDisplay(Display.INLINE);
            containerBox.add(inputTextBox);
        } else {
            inputTextBox.setWidth("100%");
            inputTextBox.getElement().getStyle().setMarginBottom(2, Unit.PX);
            panel.add(inputTextBox);
            panel.add(selectedItemsContainerPanel);
        }

        initWidget(panel);
        selectedWidgets = new LinkedList<SelectedItemHolder<DataType>>();
    }

    public SuperSelector(IFormat<DataType> format) {
        this(format, false, false);
    }

    public void setInput(String input) {
        inputTextBox.setValue(input);
        onInputChanged(input);
    }

    /** Produces a newly allocated list of selected items */
    public List<DataType> getSelectedItems() {
        LinkedList<DataType> items = new LinkedList<DataType>();
        for (SelectedItemHolder<DataType> i : selectedWidgets) {
            items.add(i.getItem());
        }
        return items;
    }

    /**
     * Warning: passing <code>null</code> will be silently ignored.
     */
    public void addItem(DataType item) {
        if (item == null) {
            return;
        }
        if (!allowSame && getSelectedItems().contains(item)) {
            return;
        }

        SelectedItemHolder<DataType> w = new SelectedItemHolder<DataType>(this.format, this, item);
        selectedWidgets.add(w);
        selectedItemsContainerPanel.add(w);

        adjustInnerWidgetSizes();
        onItemAdded(item);
    }

    public final void removeItem(DataType item) {
        SelectedItemHolder<DataType> itemContainerWidget = null;
        for (SelectedItemHolder<DataType> w : selectedWidgets) {
            if (w.getItem().equals(item)) {
                itemContainerWidget = w;
                break;
            }
        }
        if (itemContainerWidget != null) {
            selectedWidgets.remove(itemContainerWidget);
            selectedItemsContainerPanel.remove(itemContainerWidget);
        }

        if (!inlineInput && selectedWidgets.isEmpty()) {
            inputTextBox.getElement().getStyle().setDisplay(Display.BLOCK);
        }
        adjustInnerWidgetSizes();
        onItemRemoved(item);
    }

    public final void removeAll() {
        for (SelectedItemHolder<DataType> w : selectedWidgets) {
            removeItem(w.getItem());
        }
    }

    protected void onAddItemRequest() {
        addItemFromInputBox();
    }

    protected void onInputChanged(String newInput) {
    }

    /**
     * Called after an item has been added. Default implementation does nothing.
     */
    protected void onItemAdded(DataType item) {
    }

    /**
     * Called after an item has been removed. Default implementation does nothing.
     */
    protected void onItemRemoved(DataType item) {
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

    private void adjustInnerWidgetSizes() {
        if (inlineInput) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    int maxRowWidth = containerBox.getElement().getClientWidth();
                    int rowWidth = 0;
                    for (SelectedItemHolder<DataType> w : selectedWidgets) {
                        rowWidth += w.getElement().getOffsetWidth();
                        if (rowWidth > maxRowWidth) {
                            rowWidth = w.getElement().getOffsetWidth();
                        }
                    }
                    int proposedInputWidth = maxRowWidth - rowWidth;
                    int effectiveInputWidth = (proposedInputWidth < minInputBoxWidth ? maxRowWidth : proposedInputWidth) - 2;
                    inputTextBox.getElement().getStyle().setWidth(effectiveInputWidth, Unit.PX);
                    if (SuperSelector.this.isAttached()) {
                        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                            @Override
                            public void execute() {
                                onRedraw();
                            }
                        });
                    }
                }

            });
        } else {
            if (SuperSelector.this.isAttached()) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        onRedraw();
                    }
                });
            }
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        adjustInnerWidgetSizes();
    }

    protected void onRedraw() {

    }

}
