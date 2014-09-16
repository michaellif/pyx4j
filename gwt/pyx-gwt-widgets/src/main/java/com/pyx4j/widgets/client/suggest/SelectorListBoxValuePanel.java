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
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.event.shared.PasteHandler;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class SelectorListBoxValuePanel<E> extends FlowPanel implements ISelectorValuePanel, IWatermarkWidget {

    private final IFormatter<E, String> valueFormatter;

    private final TextBox textBox;

    private Button actionButton;

    private final FlowPanel cellsPanel;

    private SelectorListBox<E> parent;

    public SelectorListBoxValuePanel(IFormatter<E, String> valueFormatter) {

        setStyleName(WidgetTheme.StyleName.SelectorListBoxValuePanel.name());
        addStyleName(WidgetTheme.StyleName.ListBox.name());

        this.valueFormatter = valueFormatter;
        cellsPanel = new FlowPanel();

        textBox = new TextBox();
        cellsPanel.add(textBox);

        this.add(cellsPanel);

        addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                setFocus(true);
            }
        }, ClickEvent.getType());
    }

    public void showValue(Collection<E> value) {
        this.cellsPanel.clear();
        for (E item : value) {
            this.cellsPanel.add(new ItemHolder<E>(this, item, valueFormatter.format(item)));
        }
        cellsPanel.add(textBox);
        textBox.setText("");
        textBox.setFocus(true);
    }

    @Override
    public String getQuery() {
        return textBox.getText();
    }

    @Override
    public void setEnabled(boolean enabled) {
        textBox.setEnabled(enabled);
        actionButton.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return textBox.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        textBox.setEditable(editable);

    }

    @Override
    public boolean isEditable() {
        return textBox.isEditable();
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        textBox.setDebugId(debugId);
    }

    @Override
    public int getTabIndex() {
        return textBox.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        textBox.setAccessKey(key);

    }

    @Override
    public void setFocus(boolean focused) {
        textBox.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        textBox.setTabIndex(index);

    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return textBox.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return textBox.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBox.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBox.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textBox.addKeyPressHandler(handler);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return textBox.addValueChangeHandler(handler);
    }

    @Override
    public void setWatermark(String watermark) {
        textBox.setWatermark(watermark);

    }

    @Override
    public String getWatermark() {
        return textBox.getWatermark();
    }

    public HandlerRegistration addPasteHandler(PasteHandler handler) {
        return null;
    }

    public void setText(String value) {
        textBox.setText(value);
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

        }
    }

}
