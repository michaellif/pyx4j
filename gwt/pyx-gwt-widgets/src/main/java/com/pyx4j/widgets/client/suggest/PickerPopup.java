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
package com.pyx4j.widgets.client.suggest;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class PickerPopup<E> extends PopupPanel {

    private final ISelectorWidget<E> selectorWidget;

    private IPickerPanel<E> pickerPanel;

    public PickerPopup(final ISelectorWidget<E> parent) {
        super(true, false);
        this.selectorWidget = parent;
        addAutoHidePartner(parent.getElement());

        setPreviewingAllNativeEvents(true);
        setStyleName(WidgetTheme.StyleName.SelectionBoxPicker.name());

        final ISelectorValuePanel<E> viewerPanel = parent.getViewerPanel();

        viewerPanel.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    pickerPanel.moveSelectionDown();
                    break;
                case KeyCodes.KEY_UP:
                    pickerPanel.moveSelectionUp();
                    break;
                case KeyCodes.KEY_ENTER:
                case KeyCodes.KEY_TAB:
                    pickerPanel.pickSelection();
                    break;
                }
            }
        });

        viewerPanel.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                //if (syncInput()) {
                pickerPanel.refreshSuggestions();
                //}
            }
        });

//        private boolean syncInput() {
//            String newText = getText();
//            // check if new input has been received
//            boolean result = (!display.isSuggestionListShowing() && CommonsStringUtils.isEmpty(newText))
//                    || (text == null ? newText != null : !text.equals(newText));
//            text = newText;
//            return result;
//        }

        viewerPanel.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                parent.fireEvent(event);
            }
        });

        viewerPanel.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                pickerPanel.refreshSuggestions();
            }
        });

        viewerPanel.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                pickerPanel.pickSelection();
            }
        });
    }

    public ISelectorWidget<E> getSelectorWidget() {
        return selectorWidget;
    }

    public void show(IPickerPanel<E> pickerPanel) {
        if (isAttached()) {
            hide();
        }
        this.pickerPanel = pickerPanel;
        pickerPanel.setPickerPopup(this);
        setWidget(pickerPanel.asWidget());
        showRelativeTo(selectorWidget.asWidget());
    }

    @Override
    public void hide() {
        if (pickerPanel != null) {
            pickerPanel.setPickerPopup(null);
        }
        setWidget(null);
        super.hide();
    }

    @Override
    protected void onLoad() {
        if (getOffsetWidth() <= selectorWidget.asWidget().getOffsetWidth()) {
            setWidth((selectorWidget.asWidget().getOffsetWidth() - 2) + "px");
        } else {
            setWidth((getOffsetWidth() + 50) + "px");
        }
    };

}
