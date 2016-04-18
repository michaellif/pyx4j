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
 * Created on Sep 9, 2014
 * @author arminea
 */
package com.pyx4j.widgets.client.selector;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IFocusGroup;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.event.shared.PasteEvent;
import com.pyx4j.widgets.client.event.shared.PasteHandler;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class SelectorListBoxValuePanel<E> extends FocusPanel implements ISelectorValuePanel, IFocusGroup, IWatermarkWidget {

    private final FlowPanel contentPanel;

    private final QueryBox queryBox;

    private ImageButton actionButton;

    private final FlowPanel itemsPanel;

    private SelectorListBox<E> selectorListBox;

    private final GroupFocusHandler groupFocusHandler;

    private ItemHolderFactory<E> itemHolderFactory;

    private ItemEditorPopup itemEditorPopup;

    public SelectorListBoxValuePanel(ItemHolderFactory<E> itemHolderFactory) {
        this();
        this.itemHolderFactory = itemHolderFactory;
    }

    public SelectorListBoxValuePanel(final IFormatter<E, String> valueFormatter) {
        this();
        this.itemHolderFactory = new ItemHolderFactory<E>() {

            @Override
            public ItemHolder<E> createItemHolder(E item, SelectorListBoxValuePanel<E> selectorListBoxValuePanel) {
                return new ItemHolder<E>(item, valueFormatter);
            }
        };

    }

    public SelectorListBoxValuePanel(final IFormatter<E, String> valueFormatter, final IFormatter<E, String> tooltipFormatter) {
        this();
        this.itemHolderFactory = new ItemHolderFactory<E>() {

            @Override
            public ItemHolder<E> createItemHolder(E item, SelectorListBoxValuePanel<E> selectorListBoxValuePanel) {
                return new ItemHolder<E>(item, valueFormatter, tooltipFormatter);
            }
        };

    }

    private SelectorListBoxValuePanel() {
        setStyleName(WidgetsTheme.StyleName.SelectorListBoxValuePanel.name());
        addStyleName(WidgetsTheme.StyleName.ListBox.name());

        contentPanel = new FlowPanel();
        setWidget(contentPanel);

        itemsPanel = new FlowPanel();

        queryBox = new QueryBox();
        itemsPanel.add(queryBox);

        contentPanel.add(itemsPanel);

        groupFocusHandler = new GroupFocusHandler(this);
        groupFocusHandler.addFocusable(queryBox);
        groupFocusHandler.addFocusable(this);

        itemEditorPopup = new ItemEditorPopup();
        groupFocusHandler.addFocusable(itemEditorPopup);

        sinkEvents(Event.ONMOUSEDOWN);

        super.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                queryBox.setFocus(true);
            }
        });
    }

    public void enableQuery(boolean enable) {
        queryBox.setVisible(enable);
    }

    public void showValue(Collection<E> value) {

        for (int i = itemsPanel.getWidgetCount() - 2; i >= 0; i--) {
            itemsPanel.remove(i);
        }

        if (value.size() > 0) {
            for (E item : value) {
                if (item != null) {
                    final ItemHolder<E> itemHolder = itemHolderFactory.createItemHolder(item, this);
                    itemHolder.setSelectorListBoxValuePanel(this);
                    this.itemsPanel.insert(itemHolder, itemsPanel.getWidgetCount() - 1);
                    if (itemHolder instanceof EditableItemHolder) {
                        if (((EditableItemHolder<E>) itemHolder).isEditorShownOnAttach()) {
                            ((EditableItemHolder<E>) itemHolder).showEditor();
                        }
                    }
                }
            }
        }

        clearQueryBox();
    }

    public void clearQueryBox() {
        queryBox.setValue("");
    }

    @Override
    public String getQuery() {
        return queryBox.getValue();
    }

    @Override
    public void setEnabled(boolean enabled) {
        queryBox.setEnabled(enabled);
        actionButton.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return queryBox.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        queryBox.setEditable(editable);

    }

    @Override
    public boolean isEditable() {
        return queryBox.isEditable();
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        queryBox.setDebugId(debugId);
    }

    @Override
    public int getTabIndex() {
        return queryBox.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        queryBox.setAccessKey(key);

    }

    @Override
    public void setFocus(boolean focused) {
        queryBox.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        queryBox.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return groupFocusHandler.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return groupFocusHandler.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return queryBox.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return queryBox.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return queryBox.addKeyPressHandler(handler);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return queryBox.addValueChangeHandler(handler);
    }

    @Override
    public void setWatermark(String watermark) {
        queryBox.setWatermark(watermark);

    }

    @Override
    public String getWatermark() {
        return queryBox.getWatermark();
    }

    public HandlerRegistration addPasteHandler(PasteHandler handler) {
        return queryBox.addPasteHandler(handler);
    }

    public void setText(String value) {
        queryBox.setValue(value);
    }

    public final void removeItem(E item) {
        this.selectorListBox.removeItem(item);
    }

    public void setSelectorListBox(SelectorListBox<E> selectorListBox) {
        this.selectorListBox = selectorListBox;
    }

    public SelectorListBox<E> getSelectorListBox() {
        return selectorListBox;
    }

    public void setAction(final Command command) {
        if (actionButton != null) {
            remove(actionButton);
        }
        if (command == null) {
            itemsPanel.getStyle().setMarginRight(0, Unit.PX);
        } else {
            actionButton = new ImageButton(ImageFactory.getImages().addButton(), new Command() {

                @Override
                public void execute() {
                    selectorListBox.hidePickerPopup();
                    command.execute();
                }

            }) {

                @Override
                protected void onAttach() {
                    super.onAttach();
                    itemsPanel.getStyle().setMarginRight(actionButton.getOffsetWidth(), Unit.PX);

                }

            };
            actionButton.setEnabled(isEditable() && isEnabled());

            actionButton.getStyle().setRight(0, Unit.PX);
            actionButton.getStyle().setTop(0, Unit.PX);
            actionButton.getStyle().setPosition(Position.ABSOLUTE);

            contentPanel.add(actionButton);
            groupFocusHandler.addFocusable(actionButton);

        }
    }

    @Override
    public GroupFocusHandler getGroupFocusHandler() {
        return groupFocusHandler;
    }

    public ItemEditorPopup getItemEditorPopup() {
        return itemEditorPopup;
    }

    class QueryBox extends StringBox {
        public QueryBox() {
            addKeyUpHandler(new KeyUpHandler() {

                @Override
                public void onKeyUp(KeyUpEvent event) {
                    updateTextBox(getTextBoxWidget().getText(), true, null);
                }
            });

            addPasteHandler(new PasteHandler() {

                @Override
                public void onPaste(PasteEvent event) {
                    updateTextBox(getTextBoxWidget().getText(), true, null);
                }
            });
        }
    }

}
