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
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.ImageFactory;

public class SelectorTextBox<E> extends AbstractSelectorWidget<E> implements HasValueChangeHandlers<E>, IWatermarkWidget {

    private E value;

    private final SelectorTextBoxValuePanel<E> textBox;

    private final IFormatter<E, String> valueFormatter;

    private final IFormatter<E, SafeHtml> optionFormatter;

    private final IPickerPanel<E> picker;

    @SuppressWarnings("unchecked")
    public SelectorTextBox(final IOptionsGrabber<E> optionsGrabber, IFormatter<E, String> valueFormatter, IFormatter<E, SafeHtml> optionFormatter) {
        super(new SelectorTextBoxValuePanel<E>(valueFormatter));
        this.valueFormatter = valueFormatter;
        this.optionFormatter = optionFormatter;
        textBox = (SelectorTextBoxValuePanel<E>) getViewerPanel();

        picker = new SimplePickerPanel<E>(optionsGrabber, optionFormatter);

        textBox.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                showSuggestPicker();
            }
        });

        textBox.setAction(new Command() {
            @Override
            public void execute() {

                showEverithingPicker();
            }
        }, ImageFactory.getImages().action());

    }

    public void setValue(E value) {
        this.value = value;
        if (value == null) {
            textBox.setValue(null);
        } else {
            textBox.setValue(valueFormatter.format(value));
        }
    }

    public E getValue() {
        return value;
    }

    @Override
    public void setSelection(E value) {
        setValue(value);
        fireValueChangeEvent(value);
    }

    protected void showSuggestPicker() {
        if (getQuery() != getViewerPanel().getQuery()) {
            setQuery(getViewerPanel().getQuery());
            picker.refreshOptions(getQuery(), null);
            showPickerPopup(picker);
        }
    }

    protected void showEverithingPicker() {
        picker.refreshOptions("", null);
        showPickerPopup(picker);
    }

    @Override
    protected void showPickerPopup(IPickerPanel<E> pickerPanel) {
        value = null;
        super.showPickerPopup(pickerPanel);
    }

    @Override
    public void resetQuery() {
        textBox.showValue(value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void fireValueChangeEvent(E value) {
        ValueChangeEvent.fire(this, value);
    }

    @Override
    public void setWatermark(String watermark) {
        textBox.setWatermark(watermark);
    }

    @Override
    public String getWatermark() {
        return textBox.getWatermark();
    }

}