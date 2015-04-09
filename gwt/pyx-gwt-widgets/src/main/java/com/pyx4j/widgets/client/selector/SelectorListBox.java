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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class SelectorListBox<E> extends AbstractSelectorWidget<E> implements HasValueChangeHandlers<Collection<E>>, IFocusWidget, IWatermarkWidget {

    protected final ArrayList<E> value;

    protected final SelectorListBoxValuePanel<E> listBox;

    private final IPickerPanel<E> picker;

    protected IOptionsGrabber<E> optionsGrabber;

    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, IFormatter<E, String> valueFormatter, IFormatter<E, SafeHtml> optionFormatter) {
        this(optionsGrabber, null, optionFormatter, valueFormatter);
    }

    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, Command addItemCommand, IFormatter<E, SafeHtml> optionFormatter,
            IFormatter<E, String> valueFormatter) {
        this(optionsGrabber, addItemCommand, optionFormatter, new SelectorListBoxValuePanel<E>(valueFormatter));
    }

    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, Command addItemCommand, IFormatter<E, SafeHtml> optionFormatter,
            ItemHolderFactory<E> itemHolderFactory) {
        this(optionsGrabber, addItemCommand, optionFormatter, new SelectorListBoxValuePanel<E>(itemHolderFactory));

    }

    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, IFormatter<E, SafeHtml> optionFormatter, ItemHolderFactory<E> itemHolderFactory) {
        this(optionsGrabber, null, optionFormatter, new SelectorListBoxValuePanel<E>(itemHolderFactory));

    }

    @SuppressWarnings("unchecked")
    public SelectorListBox(final IOptionsGrabber<E> optionsGrabber, Command addItemCommand, IFormatter<E, SafeHtml> optionFormatter,
            SelectorListBoxValuePanel<E> selectorListBoxValuePanel) {
        super(selectorListBoxValuePanel);
        this.optionsGrabber = optionsGrabber;
        listBox = (SelectorListBoxValuePanel<E>) getViewerPanel();
        listBox.setSelectorListBox(this);

        value = new ArrayList<>();

        picker = new SimplePickerPanel<E>(optionsGrabber, optionFormatter);

        listBox.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                showSuggestPicker();
            }
        });

        listBox.setAction(addItemCommand);

        addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                listBox.addStyleDependentName(WidgetsTheme.StyleDependent.focused.name());
            }
        });

        addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                listBox.removeStyleDependentName(WidgetsTheme.StyleDependent.focused.name());
            }
        });
    }

    public IOptionsGrabber<E> getOptionsGrabber() {
        return optionsGrabber;
    }

    public void setValue(Collection<E> value) {
        this.value.clear();
        if (value != null) {
            this.value.addAll(value);
        }
        listBox.showValue(this.value);
    }

    public Collection<E> getValue() {
        return new ArrayList<E>(this.value);
    }

    protected void showSuggestPicker() {
        if (getQuery() != getViewerPanel().getQuery()) {
            setQuery(getViewerPanel().getQuery());

        }
        picker.refreshOptions(getQuery(), value);
        showPickerPopup(picker);
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
        listBox.clearQueryBox();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Collection<E>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void fireValueChangeEvent() {
        ValueChangeEvent.fire(this, value);
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
            fireValueChangeEvent();
        }
    }

    public void removeItem(E item) {
        if (null != item) {
            if (this.value.contains(item)) {
                ArrayList<E> newValue = new ArrayList<>(value);
                newValue.remove(item);
                setValue(newValue);
                fireValueChangeEvent();
            }
        }
    }

    public void setAction(final Command addItemCommand) {
        listBox.setAction(addItemCommand);
    }

}