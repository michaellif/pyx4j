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
 * Created on Sep 4, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import java.util.Collection;

import com.google.gwt.user.client.Command;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.TextBoxBase;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class SelectorTextBoxViewerPanel<E> extends TextBoxBase implements ISelectorValuePanel<E> {

    private final ISelectorWidget<E> parent;

    public SelectorTextBoxViewerPanel(ISelectorWidget<E> parent) {
        this.parent = parent;

        setTextBoxWidget(new InputTextBox());

        setAction(new Command() {

            @Override
            public void execute() {

            }
        }, ImageFactory.getImages().action());
    }

    @Override
    public void setSelection(Collection<E> items) {
        // TODO Auto-generated method stub

    }

    class InputTextBox extends com.google.gwt.user.client.ui.TextBox {

        private final boolean focused = false;

        private String text;

        public InputTextBox() {

//            addKeyDownHandler(new KeyDownHandler() {
//
//                @Override
//                public void onKeyDown(KeyDownEvent event) {
//                    switch (event.getNativeKeyCode()) {
//                    case KeyCodes.KEY_DOWN:
//                        display.moveSelectionDown();
//                        break;
//                    case KeyCodes.KEY_UP:
//                        display.moveSelectionUp();
//                        break;
//                    case KeyCodes.KEY_ENTER:
//                    case KeyCodes.KEY_TAB:
//                        SelectorTextBox.this.setValue(display.getCurrentSelection());
//                        break;
//                    }
//                }
//            });
//
//            addKeyUpHandler(new KeyUpHandler() {
//
//                @Override
//                public void onKeyUp(KeyUpEvent event) {
//                    if (syncInput()) {
//                        refreshSuggestions();
//                    }
//                }
//            });
//
//            addValueChangeHandler(new ValueChangeHandler<String>() {
//
//                @Override
//                public void onValueChange(ValueChangeEvent<String> event) {
//                    delegateEvent(SelectorTextBox.this, event);
//                }
//            });
//
//            addFocusHandler(new FocusHandler() {
//
//                @Override
//                public void onFocus(FocusEvent event) {
//                    focused = true;
//                    refreshSuggestions();
//                }
//            });
//
//            addBlurHandler(new BlurHandler() {
//
//                @Override
//                public void onBlur(BlurEvent event) {
//                    focused = false;
//                    SelectorTextBox.this.setValue(display.getCurrentSelection());
//                }
//            });

            setStyleName(WidgetTheme.StyleName.TextBox.name());
            addStyleName(WidgetTheme.StyleName.SuggestBox.name());
            addStyleDependentName(WidgetTheme.StyleDependent.singleLine.name());

        }

        public boolean hasFocus() {
            return focused;
        }

        private boolean syncInput() {
            String newText = getText();
            // check if new input has been received
//            boolean result = (!display.isSuggestionListShowing() && CommonsStringUtils.isEmpty(newText))
//                    || (text == null ? newText != null : !text.equals(newText));
            text = newText;
//            return result;
            return false;
        }
    }
}
