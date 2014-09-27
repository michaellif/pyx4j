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
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IFocusGroup;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.event.shared.PasteHandler;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class SelectorListBoxValuePanel<E> extends FlowPanel implements ISelectorValuePanel, IFocusGroup, IWatermarkWidget {

    private final IFormatter<E, String> valueFormatter;

    private final TextBox queryBox;

    private Button actionButton;

    private final FlowPanel cellsPanel;

    private SelectorListBox<E> parent;

    private final GroupFocusHandler groupFocusHandler;

    public SelectorListBoxValuePanel(IFormatter<E, String> valueFormatter) {

        setStyleName(WidgetTheme.StyleName.SelectorListBoxValuePanel.name());
        addStyleName(WidgetTheme.StyleName.ListBox.name());

        this.valueFormatter = valueFormatter;
        cellsPanel = new FlowPanel();

        queryBox = new TextBox();
        cellsPanel.add(queryBox);

        this.add(cellsPanel);

        sinkEvents(Event.ONCLICK);

        groupFocusHandler = new GroupFocusHandler(this);
        groupFocusHandler.addFocusable(queryBox);

        addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                setFocus(true);
            }
        }, ClickEvent.getType());

    }

    public void showValue(Collection<E> value) {
        this.cellsPanel.clear();
        if (value.size() > 0) {
            for (E item : value) {
                if (item != null) {
                    this.cellsPanel.add(new ItemHolder<E>(this, item, valueFormatter.format(item)));
                }
            }
        }

        cellsPanel.add(queryBox);
        queryBox.setText("");
    }

    @Override
    public String getQuery() {
        return queryBox.getText();
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
        queryBox.setText(value);
    }

    public final void removeItem(E item) {
        this.parent.removeItem(item);
    }

    public void setParent(SelectorListBox<E> parent) {
        this.parent = parent;
    }

    public void setAction(Command command) {
        if (actionButton != null) {
            remove(actionButton);
        }
        if (command == null) {
            cellsPanel.getElement().getStyle().setMarginRight(0, Unit.PX);
        } else {
            actionButton = new Button(ImageFactory.getImages().addAction(), command) {

                @Override
                protected void onAttach() {
                    super.onAttach();
                    cellsPanel.getElement().getStyle().setMarginRight(actionButton.getOffsetWidth(), Unit.PX);

                }

            };
            actionButton.setEnabled(isEditable() && isEnabled());

            actionButton.getElement().getStyle().setRight(0, Unit.PX);
            actionButton.getElement().getStyle().setTop(0, Unit.PX);
            actionButton.getElement().getStyle().setPosition(Position.ABSOLUTE);

            add(actionButton);
            groupFocusHandler.addFocusable(actionButton);

        }
    }

    @Override
    public GroupFocusHandler getGroupFocusHandler() {
        return groupFocusHandler;
    }

}
