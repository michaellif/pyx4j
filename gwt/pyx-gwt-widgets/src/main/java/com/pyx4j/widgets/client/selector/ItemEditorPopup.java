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
 * @version $Id$
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ItemEditorPopup extends DropDownPanel implements Focusable, HasAllFocusHandlers {

    private final FocusPanel focusPanel;

    public ItemEditorPopup() {
        super();
        addStyleName(WidgetsTheme.StyleName.SuggestBoxPopup.name());

        focusPanel = new FocusPanel();
        setWidget(focusPanel);
    }

    public void show(EditableItemHolder<?> itemHolder) {
        focusPanel.setWidget(itemHolder.getEditor().asWidget());
        showRelativeTo(itemHolder);
    }

    @Override
    public void hide(boolean autoClosed) {
        focusPanel.setWidget(null);
        super.hide(autoClosed);
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
