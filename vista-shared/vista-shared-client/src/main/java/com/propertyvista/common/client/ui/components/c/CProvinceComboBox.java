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
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.events.HasNValueChangeHandlers;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.AsyncOptionLoadingDelegate;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.CFocusComponent;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.ComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class CProvinceComboBox extends CFocusComponent<String, NProvinceComboBox> implements HasOptionsChangeHandlers<List<Province>>,
        HasNValueChangeHandlers<String>, AsyncOptionsReadyCallback<Province> {

    private static final I18n i18n = I18n.get(CProvinceComboBox.class);

    private final List<Province> options = new ArrayList<Province>();

    private final AsyncOptionLoadingDelegate<Province> asyncOptionDelegate;

    private ComponentValidator<String> unavailableValidator;

    public CProvinceComboBox() {
        super();

        NProvinceComboBox nativeComboBox = new NProvinceComboBox(this);
        nativeComboBox.refreshOptions();
        setNativeComponent(nativeComboBox);

        this.asyncOptionDelegate = new AsyncOptionLoadingDelegate<Province>(Province.class, this, null);
        this.unavailableValidator = new AbstractComponentValidator<String>() {
            @Override
            public FieldValidationError isValid() {
                return new FieldValidationError(getComponent(), i18n.tr("Reference data unavailable"));
            }
        };
    }

    @Override
    protected void setEditorValue(final String value) {
        if (isViewable() || getNativeComponent().isTextMode() || isOptionsLoaded()) {
            super.setEditorValue(value);
        } else {
            // in list editor mode load options first
            retrieveOptions(new AsyncOptionsReadyCallback<Province>() {
                @Override
                public void onOptionsReady(List<Province> opt) {
                    CProvinceComboBox.super.setEditorValue(value);
                }
            });
        }
    }

    public String convertOption(Province prov) {
        return prov == null ? "" : prov.name().getValue();
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

    public void setCountry(Country country) {
        setTextMode(false);
        resetCriteria();
        addCriterion(PropertyCriterion.eq(proto().country(), country));
        retrieveOptions(null);
    }

    public Province proto() {
        return asyncOptionDelegate.proto();
    }

    public EntityQueryCriteria<Province> addCriterion(Criterion criterion) {
        return asyncOptionDelegate.addCriterion(criterion);
    }

    public void resetCriteria() {
        asyncOptionDelegate.resetCriteria();
    }

    public boolean isOptionsLoaded() {
        return asyncOptionDelegate.isOptionsLoaded();
    }

    public void retrieveOptions(final AsyncOptionsReadyCallback<Province> optionsReadyCallback) {
        if (isViewable()) {
            return;
        } else if (!isOptionsLoaded()) {
            asyncOptionDelegate.retrieveOptions(optionsReadyCallback);
        } else if (optionsReadyCallback != null) {
            optionsReadyCallback.onOptionsReady(getOptions());
        }
    }

    public List<Province> getOptions() {
        return options;
    }

    public List<String> getConvertedOptions() {
        List<String> result = new ArrayList<>();
        for (Province o : options) {
            result.add(convertOption(o));
        }
        return result;
    }

    public void setOptions(Collection<Province> opt) {
        options.clear();
        if (opt != null) {
            options.addAll(opt);
        }
        // in case the options were set synchronously
        asyncOptionDelegate.setOptionsLoaded(true);

        getNativeComponent().refreshOptions();
        OptionsChangeEvent.fire(this, getOptions());
    }

    @Override
    public void onOptionsReady(List<Province> opt) {
        if (isOptionsLoaded()) {
            removeComponentValidator(unavailableValidator);
        } else if (!getNativeComponent().isTextMode()) {
            addComponentValidator(unavailableValidator);
        }
        setOptions(opt);
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<Province>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addNValueChangeHandler(NValueChangeHandler<String> handler) {
        return addHandler(handler, NValueChangeEvent.getType());
    }

}
