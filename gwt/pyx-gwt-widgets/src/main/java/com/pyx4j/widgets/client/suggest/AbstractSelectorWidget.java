/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 28, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.IDebugId;

public abstract class AbstractSelectorWidget<E> extends Composite implements ISelectorWidget<E> {

    private final ISelectorValuePanel<E> viewerPanel;

    private final PickerPopup<E> pickerPopup;

    public AbstractSelectorWidget(ISelectorValuePanel<E> viewerPanel) {
        this.viewerPanel = viewerPanel;
        initWidget(viewerPanel.asWidget());
        pickerPopup = new PickerPopup<E>(this);

        viewerPanel.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    pickerPopup.moveSelectionDown();
                    break;
                case KeyCodes.KEY_UP:
                    pickerPopup.moveSelectionUp();
                    break;
                case KeyCodes.KEY_ENTER:
                case KeyCodes.KEY_TAB:
                    pickerPopup.pickSelection();
                    break;
                }
            }
        });

        viewerPanel.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                pickerPopup.refreshSuggestions();
            }
        });

    }

    @Override
    public ISelectorValuePanel<E> getViewerPanel() {
        return viewerPanel;
    }

    protected void showPickerPopup(IPickerPanel<E> pickerPanel) {
        pickerPopup.show(pickerPanel);
    }

    protected void hidePickerPopup() {
        pickerPopup.hide();
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return viewerPanel.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return viewerPanel.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return viewerPanel.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return viewerPanel.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return viewerPanel.addKeyPressHandler(handler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        viewerPanel.setEnabled(enabled);
        if (!enabled) {
            hidePickerPopup();
        }
    }

    @Override
    public boolean isEnabled() {
        return viewerPanel.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        viewerPanel.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return viewerPanel.isEditable();
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        viewerPanel.setDebugId(debugId);
    }

    @Override
    public int getTabIndex() {
        return viewerPanel.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        viewerPanel.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        viewerPanel.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        viewerPanel.setTabIndex(index);
    }

}
