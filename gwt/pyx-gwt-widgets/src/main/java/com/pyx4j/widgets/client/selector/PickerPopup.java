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
 * Created on Jul 20, 2010
 * @author michaellif
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;

import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class PickerPopup<E> extends DropDownPanel implements Focusable, HasAllFocusHandlers {

    private final ISelectorWidget<E> selectorWidget;

    private IPickerPanel<E> pickerPanel;

    private final FocusPanel focusPanel;

    public PickerPopup(final ISelectorWidget<E> parent) {
        super();
        this.selectorWidget = parent;
        addStyleName(WidgetsTheme.StyleName.SuggestBoxPopup.name());

        focusPanel = new FocusPanel();

        setWidget(focusPanel);

    }

    public ISelectorWidget<E> getSelectorWidget() {
        return selectorWidget;
    }

    public void show(IPickerPanel<E> pickerPanel) {
        if (this.pickerPanel != null) {
            this.pickerPanel.setPickerPopup(null);
        }
        this.pickerPanel = pickerPanel;
        pickerPanel.setPickerPopup(this);
        focusPanel.setWidget(pickerPanel.asWidget());
        showRelativeTo(selectorWidget.asWidget());
    }

    @Override
    public void hide(boolean autoClosed) {
        this.pickerPanel = null;
        if (pickerPanel != null) {
            pickerPanel.setPickerPopup(null);
        }
        focusPanel.setWidget(null);
        selectorWidget.resetQuery();
        super.hide(autoClosed);
    }

    public void moveSelectionDown() {
        if (pickerPanel != null) {
            pickerPanel.moveSelectionDown();
        }
    }

    public void moveSelectionUp() {
        if (pickerPanel != null) {
            pickerPanel.moveSelectionUp();
        }
    }

    public void pickSelection() {
        if (pickerPanel != null && pickerPanel.getSelection() != null) {
            //Wait for focus to be set on selectorWidget.
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    selectorWidget.setFocus(true);
                    hide();
                }
            });
            selectorWidget.setSelection(pickerPanel.getSelection());
        }
    }

    @Override
    protected void onLoad() {
        setWidth(selectorWidget.asWidget().getOffsetWidth() + "px");
    }

    @Override
    public int getTabIndex() {
        return focusPanel.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        focusPanel.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        focusPanel.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        focusPanel.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return focusPanel.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return focusPanel.addBlurHandler(handler);
    }
}
