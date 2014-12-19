/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-22
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.selectorfolder;

import java.text.ParseException;

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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;

public class SelectorFolderDecorator<E extends IEntity> extends Composite implements IFolderDecorator<E> {

    public enum Styles implements IStyleName {

        SuperSelectorStyle;

    }

    private final TextBox inputTextBox;

    private final FlowPanel selectedItemsContainerPanel;

    private final int minInputBoxWidth = 50;

    private final IFormatter<E, String> format;

    private final IParser<E> parser;

    private final boolean allowSame;

    private boolean isFocused;

    private FlowPanel containerBox;

    private final boolean inlineInput;

    private SelectorFolder<E> selectorFolder;

    /**
     * The format will be used to parse input and convert it to stuff, and to display selected items. if convert fails it can return "null" to avoid adding an
     * item.
     */
    public SelectorFolderDecorator(IFormatter<E, String> format, IParser<E> parser, boolean allowSame, boolean inlineInput) {
        this.format = format;
        this.parser = parser;

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
                    SelectorFolderDecorator.this.onAddItemRequest();
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
                SelectorFolderDecorator.this.isFocused = true;
                SelectorFolderDecorator.this.onFocus();
            }
        });
        inputTextBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                SelectorFolderDecorator.this.isFocused = false;
                if (!SelectorFolderDecorator.this.inlineInput) {
                    if (!selectorFolder.getValue().isEmpty()) {
                        inputTextBox.getElement().getStyle().setDisplay(Display.NONE);
                        adjustInnerWidgetSizes();
                    }
                }
                SelectorFolderDecorator.this.onBlur();
            }
        });

        selectedItemsContainerPanel = new FlowPanel();
        selectedItemsContainerPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!SelectorFolderDecorator.this.inlineInput) {
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
    }

    public SelectorFolderDecorator(IFormatter<E, String> format, IParser<E> parser) {
        this(format, parser, false, true);
    }

    // Part of folder
    @Override
    public void init(CFolder<E> folder) {
        selectorFolder = (SelectorFolder<E>) folder;
    }

    @Override
    public void setAddButtonVisible(boolean show) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContent(IsWidget content) {

    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setItemAddCommand(Command command) {
        // TODO Auto-generated method stub
    }

    /*
     * public void addItem(DataType item) {
     * if (item == null) {
     * return;
     * }
     * if (!allowSame && getSelectedItems().contains(item)) {
     * return;
     * }
     * 
     * SelectedItemHolder<DataType> w = new SelectedItemHolder<DataType>(this.format, this, item);
     * selectedWidgets.add(w);
     * selectedItemsContainerPanel.add(w);
     * 
     * adjustInnerWidgetSizes();
     * onItemAdded(item);
     * }
     */

    /*
     * public final void removeItem(DataType item) {
     * SelectedItemHolder<DataType> itemContainerWidget = null;
     * for (SelectedItemHolder<DataType> w : selectedWidgets) {
     * if (w.getItem().equals(item)) {
     * itemContainerWidget = w;
     * break;
     * }
     * }
     * if (itemContainerWidget != null) {
     * selectedWidgets.remove(itemContainerWidget);
     * selectedItemsContainerPanel.remove(itemContainerWidget);
     * }
     * 
     * if (!inlineInput && selectedWidgets.isEmpty()) {
     * inputTextBox.getElement().getStyle().setDisplay(Display.BLOCK);
     * }
     * adjustInnerWidgetSizes();
     * onItemRemoved(item);
     * }
     * 
     * public final void removeAll() {
     * for (SelectedItemHolder<DataType> w : selectedWidgets) {
     * removeItem(w.getItem());
     * }
     * }
     */
    public void setInput(String input) {
        inputTextBox.setValue(input);
        onInputChanged(input);
    }

    protected void onAddItemRequest() {
        addItemFromInputBox();
    }

    protected void onInputChanged(String newInput) {
    }

    /**
     * Called after an item has been added.
     */
    protected void onItemAdded(E item) {
        setInput("");
    }

    /**
     * Called after an item has been removed. Default implementation does nothing.
     */
    protected void onItemRemoved(E item) {
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
            addItem(parser.parse(inputTextBox.getText()));
            inputTextBox.setText("");
        } catch (ParseException e) {
            // TODO deal with this (i.e. render a popup or something like that
        }
    }

    private void addItem(E item) {

        selectorFolder.getValue().add(item);
        selectorFolder.setValue(selectorFolder.getValue(), true);

    }

    private void removeLastItem() {
        if (!selectorFolder.getValue().isEmpty()) {
            removeItem(selectorFolder.getValue().get(selectorFolder.getValue().size() - 1));
        }
    }

    private void removeItem(E item) {

    }

    private void adjustInnerWidgetSizes() {
        if (inlineInput) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    int maxRowWidth = containerBox.getElement().getClientWidth();
                    int rowWidth = 0;
                    for (CFolderItem<E> w : selectorFolder.getComponents()) {
                        rowWidth += w.asWidget().getElement().getOffsetWidth();
                        if (rowWidth > maxRowWidth) {
                            rowWidth = w.asWidget().getElement().getOffsetWidth();
                        }
                    }
                    int proposedInputWidth = maxRowWidth - rowWidth;
                    int effectiveInputWidth = (proposedInputWidth < minInputBoxWidth ? maxRowWidth : proposedInputWidth) - 2;
                    inputTextBox.getElement().getStyle().setWidth(effectiveInputWidth, Unit.PX);
                    if (SelectorFolderDecorator.this.isAttached()) {
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
            if (SelectorFolderDecorator.this.isAttached()) {
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
