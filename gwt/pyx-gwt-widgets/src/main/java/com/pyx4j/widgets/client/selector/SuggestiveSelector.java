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
package com.pyx4j.widgets.client.selector;

import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
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
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.AbstractDataProvider;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.css.IStyleName;

public class SuggestiveSelector<DataType> extends Composite {

    public static abstract class SuggestionsProvider<DataType> extends AbstractDataProvider<DataType> {

        public abstract void onSuggestionCriteriaChange(String newSuggestion);

    }

    public enum Styles implements IStyleName {

        SuperSelectorStyle, SuggestionsPopup;

    }

    public final static int SUGGESTIONS_PER_PAGE = 10;

    private final static int POPUP_HIDE_DELAY = 400;

    final static int DEFAULT_SUGGE_DELAY = 100;

    final SuggestionsProvider<DataType> suggestionsProvider;

    final Cell<DataType> cell;

    SuggestionsPopup<DataType> popup;

    private int popupDelay;

    private final boolean alwaysSuggest;

    private Timer popupTimer;

    boolean suggestionsInUse;

    private final TextBox inputTextBox;

    private final FlowPanel selectedItemsContainerPanel;

    private final List<SelectedItemHolder<DataType>> selectedWidgets;

    private final int minInputBoxWidth = 50;

    private final IFormatter<DataType, String> format;

    private final IParser<DataType> parser;

    private final boolean allowSame;

    private final boolean pageData;

    private boolean isFocused;

    private FlowPanel containerBox;

    private boolean inlineInput;

    private boolean isReadOnly;

    /**
     * The format will be used to parse input and convert it to stuff, and to display selected items. if convert fails it can return "null" to avoid adding an
     * item.
     */
    public SuggestiveSelector(IFormatter<DataType, String> format, IParser<DataType> parser, SuggestionsProvider<DataType> suggestionsProvider,
            Cell<DataType> cell, boolean allowSame, boolean inlineInput, boolean alwaysSuggest, boolean pageData) {

        this.format = format;
        this.parser = parser;

        this.allowSame = allowSame;
        this.inlineInput = inlineInput;
        this.pageData = pageData;
        this.isReadOnly = false;

        FlowPanel panel = new FlowPanel();
        panel.setStyleName(Styles.SuperSelectorStyle.name());

        inputTextBox = new TextBox();
        inputTextBox.getElement().getStyle().setDisplay(Display.BLOCK);
        inputTextBox.getElement().getStyle().setBorderStyle(BorderStyle.NONE);

        inputTextBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode() || KeyCodes.KEY_TAB == event.getNativeEvent().getKeyCode()) {
                    SuggestiveSelector.this.onAddItemRequest();
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
                SuggestiveSelector.this.isFocused = true;
                SuggestiveSelector.this.onFocus();
            }
        });
        inputTextBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                SuggestiveSelector.this.isFocused = false;
                if (!SuggestiveSelector.this.inlineInput) {
                    if (!selectedWidgets.isEmpty()) {
                        inputTextBox.getElement().getStyle().setDisplay(Display.NONE);
                        adjustInnerWidgetSizes();
                    }
                }
                SuggestiveSelector.this.onBlur();
            }
        });

        selectedItemsContainerPanel = new FlowPanel();
        selectedItemsContainerPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!SuggestiveSelector.this.inlineInput) {
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

        this.alwaysSuggest = alwaysSuggest;
        this.cell = cell;
        this.suggestionsProvider = suggestionsProvider;

        addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (SuggestiveSelector.this.popup != null) {
                    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_UP) {
                        SuggestiveSelector.this.popup.selectPrevious();
                    } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DOWN) {
                        SuggestiveSelector.this.popup.selectNext();
                    }
                }
            }
        }, KeyDownEvent.getType());
    }

    public SuggestiveSelector(IFormatter<DataType, String> format, IParser<DataType> parser, Cell<DataType> cell,
            SuggestionsProvider<DataType> suggestionsProvider, boolean alwaysSuggest, boolean pageData) {
        this(format, parser, suggestionsProvider, cell, false, true, alwaysSuggest, pageData);
    }

    public void setInput(String input) {
        inputTextBox.setValue(input);
        onInputChanged(input);
    }

    public void setReadOnly(boolean value) {
        isReadOnly = value;
        inputTextBox.setReadOnly(value);
        if (popup != null) {
            popup.setVisible(value);
        }
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
        addItemInternal(item, false);
        onItemAdded(item);
    }

    private void addItemInternal(DataType item, boolean readOnly) {
        if (item == null) {
            return;
        }
        if (!allowSame && getSelectedItems().contains(item)) {
            return;
        }

        SelectedItemHolder<DataType> w = new SelectedItemHolder<DataType>(this.format, this, item, readOnly);
        selectedWidgets.add(w);
        selectedItemsContainerPanel.add(w);

        adjustInnerWidgetSizes();
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

    public final void addAll(Collection<DataType> items, boolean readOnly) {
        for (DataType item : items) {
            addItemInternal(item, readOnly);
        }
    }

    protected void onAddItemRequest() {
        if (this.popup != null) {
            addItem(this.popup.getSelectedItem());
        } else {
            addItemFromInputBox();
        }
    }

    protected void onInputChanged(final String newInput) {
        if (this.popupTimer != null) {
            this.popupTimer.cancel();
        }
        this.popupTimer = new Timer() {
            @Override
            public void run() {
                onSuggestionCriteriaChange(newInput);
            }
        };
        this.popupTimer.schedule(this.popupDelay);
    }

    /**
     * Called after an item has been added.
     */
    protected void onItemAdded(DataType item) {
        setInput("");
    }

    /**
     * Called after an item has been removed. Default implementation does nothing.
     */
    protected void onItemRemoved(DataType item) {
    }

    protected void onFocus() {
        this.suggestionsInUse = false;
        if (alwaysSuggest) {
            showSuggestions();
            this.suggestionsProvider.onSuggestionCriteriaChange("");
        }
    }

    protected void onBlur() {
        hideSuggestionsWithADelay();
    }

    void hideSuggestionsWithADelay() {
        Timer delayedHide = new Timer() {
            @Override
            public void run() {
                if (!SuggestiveSelector.this.suggestionsInUse) {
                    hideSuggestions();
                }
            }
        };
        delayedHide.schedule(POPUP_HIDE_DELAY);
    }

    private void onSuggestionCriteriaChange(String newSuggestionCriteria) {
        if ("".equals(newSuggestionCriteria.trim())) {
            if (!alwaysSuggest) {
                hideSuggestions();
            }
        } else {
            showSuggestions();
            this.suggestionsProvider.onSuggestionCriteriaChange(newSuggestionCriteria);
        }
    }

    private void showSuggestions() {
        if (isReadOnly) {
            return;
        }
        if (popup == null) {
            popup = new SuggestionsPopup<DataType>(this, pageData);
            popup.setPopupPositionAndShow(new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    repostitionPopup();
                }
            });
        }
    }

    private void hideSuggestions() {
        if (this.popup != null) {
            this.popup.hide();
        }
    }

    private void repostitionPopup() {
        Point p = getPopupPostion();
        popup.setPopupPosition((int) p.getX(), (int) p.getY());
    }

    private Point getPopupPostion() {
        Element parent = this.getElement();
        popup.setWidth("" + (parent.getAbsoluteRight() - parent.getAbsoluteLeft()) + "px");
        final int left = parent.getAbsoluteLeft();
        final int top = parent.getAbsoluteBottom() + 1;
        return new Point(left, top);
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
                    if (SuggestiveSelector.this.isAttached()) {
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
            if (SuggestiveSelector.this.isAttached()) {
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
        if (this.popup != null) {
            repostitionPopup();
        }
    }

    public void setSuggestionDelay(int coundownMillis) {
        this.popupDelay = coundownMillis;
    }

}
