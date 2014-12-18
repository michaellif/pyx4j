/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2014
 * @author stanp
 */
package com.propertyvista.common.client.ui.components.c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.events.HasNativeValueChangeHandlers;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.forms.client.events.NativeValueChangeHandler;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.CFocusComponent;

import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;

public class CProvinceComboBox extends CFocusComponent<String, NProvinceComboBox> implements HasOptionsChangeHandlers<List<ISOProvince>>,
        HasNativeValueChangeHandlers<String> {

    private final List<ISOProvince> options = new ArrayList<>();

    public CProvinceComboBox() {
        super();

        NProvinceComboBox nativeComboBox = new NProvinceComboBox(this);
        nativeComboBox.refreshOptions();
        setNativeComponent(nativeComboBox);
    }

    @Override
    public boolean isValueEmpty() {
        return CommonsStringUtils.isEmpty(getValue());
    }

    public String convertOption(ISOProvince prov) {
        return prov == null ? "" : prov.name;
    }

    public String formatValue(String value) {
        return value == null ? "" : value;
    }

    public String parseValue(String text) {
        return text == null ? "" : text;
    }

    public void setTextMode(boolean textMode) {
        getNativeComponent().setTextMode(textMode);
    }

    public void setCountry(ISOCountry country) {
        List<ISOProvince> opts = ISOProvince.forCountry(country);
        if (opts == null || opts.size() == 0) {
            setTextMode(true);
            setValue(null);
        } else {
            setTextMode(false);
            setOptions(opts);
        }
    }

    public void retrieveOptions(final AsyncOptionsReadyCallback<ISOProvince> optionsReadyCallback) {
        if (isViewable()) {
            return;
        } else if (optionsReadyCallback != null) {
            optionsReadyCallback.onOptionsReady(getOptions());
        }
    }

    public List<ISOProvince> getOptions() {
        return options;
    }

    public List<String> getConvertedOptions() {
        List<String> result = new ArrayList<>();
        for (ISOProvince o : options) {
            result.add(convertOption(o));
        }
        return result;
    }

    public void setOptions(Collection<ISOProvince> opt) {
        options.clear();
        if (opt != null) {
            options.addAll(opt);
        }

        getNativeComponent().refreshOptions();
        OptionsChangeEvent.fire(this, getOptions());
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<ISOProvince>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addNativeValueChangeHandler(NativeValueChangeHandler<String> handler) {
        return addHandler(handler, NativeValueChangeEvent.getType());
    }
}
