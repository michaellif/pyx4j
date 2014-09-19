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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.event.shared.PasteEvent;
import com.pyx4j.widgets.client.event.shared.PasteHandler;

public class SelectorListBox<E> extends AbstractSelectorWidget<E> implements HasSelectionHandlers<E>, IFocusWidget, IWatermarkWidget {

    private final ArrayList<E> value;

    private final SelectorListBoxValuePanel<E> listBox;

    private final IFormatter<E, String> valueFormatter;

    private final IFormatter<E, String[]> optionPathFormatter;

    private final IPickerPanel<E> picker;

    private Command addItemCommand;

    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, IFormatter<E, String> valueFormatter, IFormatter<E, String[]> optionPathFormatter) {
        this(optionsGrabber, null, valueFormatter, optionPathFormatter);
        addItemCommand = new Command() {
            @Override
            public void execute() {
                showEverithingPicker();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, Command addItemCommand, IFormatter<E, String> valueFormatter,
            IFormatter<E, String[]> optionPathFormatter) {
        super(new SelectorListBoxValuePanel<E>(valueFormatter));

        this.valueFormatter = valueFormatter;

        this.optionPathFormatter = optionPathFormatter;

        listBox = (SelectorListBoxValuePanel<E>) getViewerPanel();
        listBox.setParent(this);

        value = new ArrayList<>();

        picker = new TreePickerPanel<E>(optionsGrabber, valueFormatter, null);

        listBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                showSuggestPicker();
            }
        });

        listBox.addPasteHandler(new PasteHandler() {

            @Override
            public void onPaste(PasteEvent event) {
                showSuggestPicker();
            }
        });

        listBox.setAction(addItemCommand);

    }

    public void setValue(Collection<E> value) {
        this.value.clear();
        if (value != null) {
            this.value.addAll(value);
            listBox.showValue(value);
        }
    }

    public Collection<E> getValue() {
        return new ArrayList<E>(this.value);
    }

    protected void showSuggestPicker() {
        if (getQuery() != getViewerPanel().getQuery()) {
            setQuery(getViewerPanel().getQuery());
            picker.refreshOptions(getQuery(), value);
            showPickerPopup(picker);
        }
    }

    protected void showEverithingPicker() {
        picker.refreshOptions("", null);
        showPickerPopup(picker);
    }

    @Override
    protected void showPickerPopup(IPickerPanel<E> pickerPanel) {
        super.showPickerPopup(pickerPanel);
    }

    @Override
    public void resetQuery() {
        listBox.showValue(value);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<E> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    private void fireSelectionEvent(E value) {
        SelectionEvent.fire(this, value);
    }

    @Override
    public void setWatermark(String watermark) {
        listBox.setWatermark(watermark);
    }

    @Override
    public String getWatermark() {
        return listBox.getWatermark();
    }

    @Override
    public void setSelection(E item) {
        if (!value.contains(item)) {
            ArrayList<E> newValue = new ArrayList<>(value);
            newValue.add(item);
            setValue(newValue);
            fireSelectionEvent(item);
        }
    }

    public void removeItem(E item) {
        if (null != item) {
            if (this.value.contains(item)) {
                value.remove(item);
                listBox.showValue(value);
            }

        }
    }

}