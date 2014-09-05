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
 * Created on Jul 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.IFocusWidget;

public class SelectorTextBox<E> extends AbstractSelectorWidget<E> implements HasSelectionHandlers<E>, IFocusWidget {

    private E value;

    private final SelectorTextBoxValuePanel<E> textBox;

    private final OptionsGrabber<E> optionsGrabber;

    private final IFormatter<E, String> valueFormatter;

    private final IFormatter<E, String[]> optionPathFormatter;

    public SelectorTextBox(final OptionsGrabber<E> optionsGrabber, IFormatter<E, String> valueFormatter, IFormatter<E, String[]> optionPathFormatter) {

        this.optionsGrabber = optionsGrabber;
        this.valueFormatter = valueFormatter;
        this.optionPathFormatter = optionPathFormatter;

        textBox = new SelectorTextBoxValuePanel<E>();
        setViewerPanel(textBox);

    }

    @Override
    public void setValue(E value) {
        this.value = value;
        if (value == null) {
            textBox.setText("");
        } else {
            textBox.setText(valueFormatter.format(value));
            getPickerPopup().hide();
            fireSuggestionEvent(value);
        }
    }

    @Override
    public E getValue() {
        return value;
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        addSelectionHandler(new SelectionHandler<E>() {
            @Override
            public void onSelection(SelectionEvent<E> event) {
                NativeEvent nativeEvent = Document.get().createChangeEvent();
                ChangeEvent.fireNativeEvent(nativeEvent, null);
            }
        });
        return addDomHandler(handler, ChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<E> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            getPickerPopup().hide();
        }
    }

    private void fireSuggestionEvent(E selectedSuggestion) {
        SelectionEvent.fire(this, selectedSuggestion);
    }

    /**
     * The callback used when a user selects a {@link Suggestion}.
     */
    public static interface SuggestionCallback<E> {
        void onSuggestionSelected(E suggestion);
    }

}