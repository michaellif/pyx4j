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
 * Created on Feb 2, 2010
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.events.AsyncValueChangeEvent;
import com.pyx4j.forms.client.events.AsyncValueChangeHandler;
import com.pyx4j.forms.client.events.HasAsyncValue;
import com.pyx4j.forms.client.events.HasAsyncValueChangeHandlers;
import com.pyx4j.forms.client.ui.AsyncLoadingHandler.Status;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.IValidator;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.i18n.shared.I18n;

/**
 * Two modes of operations on options: Loading from optionsDataSource or setting explicitly.
 */
public class CEntityComboBox<E extends IEntity> extends CComboBox<E>
        implements HasAsyncValue<E>, HasAsyncValueChangeHandlers<E>, IAcceptsText, AsyncOptionsReadyCallback<E> {

    private static final I18n i18n = I18n.get(CEntityComboBox.class);

    private OptionsFilter<E> optionsFilter;

    private Comparator<E> comparator = null;

    private String stringViewMemberName;

    private boolean useNamesComparison = false;

    private boolean hasAsyncValue = false;

    private final AsyncOptionLoadingDelegate<E> asyncOptionDelegate;

    private IValidator<E> unavailableValidator;

    public CEntityComboBox(Class<E> entityClass) {
        this(entityClass, (NotInOptionsPolicy) null, null);
    }

    public CEntityComboBox(Class<E> entityClass, NotInOptionsPolicy policy, EntityDataSource<E> optionsDataSource) {
        super(policy);
        this.asyncOptionDelegate = new AsyncOptionLoadingDelegate<E>(entityClass, this, optionsDataSource);
        this.unavailableValidator = new AbstractComponentValidator<E>() {
            @Override
            public BasicValidationError isValid() {
                return new BasicValidationError(getCComponent(), i18n.tr("Reference data unavailable"));
            }
        };
        // Do not load the Data here. We don't know if it is isViewable component of not.
        // Load Data on populate
        // retriveOptions(null);
    }

    public E proto() {
        return asyncOptionDelegate.proto();
    }

    public EntityQueryCriteria<E> addCriterion(Criterion criterion) {
        return asyncOptionDelegate.addCriterion(criterion);
    }

    public void resetCriteria() {
        asyncOptionDelegate.resetCriteria();
    }

    /**
     * In memory filter on top of optionsDataSource or explicitly set Options.
     *
     * Need to be called after setOptions(..) since UnFiltered options are not stored in model
     *
     * @param optionsFilter
     */
    public void setOptionsFilter(OptionsFilter<E> optionsFilter) {
        this.optionsFilter = optionsFilter;
        updateOptionsViewIfLoaded();
    }

    /**
     * This changes the display order in UI to override the order from optionsDataSource
     *
     * @param comparator
     */
    public void setOptionsComparator(Comparator<E> comparator) {
        this.comparator = comparator;
        updateOptionsViewIfLoaded();
    }

    public boolean isOptionsLoaded() {
        return asyncOptionDelegate.isOptionsLoaded();
    }

    @Override
    public void setOptions(Collection<E> opt) {
        if (((optionsFilter == null) && (comparator == null)) || (opt == null) || (opt.size() == 0)) {
            super.setOptions(opt);
        } else {
            List<E> optFiltered = new ArrayList<E>(opt.size());
            if (optionsFilter != null) {
                for (E en : opt) {
                    if (optionsFilter.acceptOption(en)) {
                        optFiltered.add(en);
                    }
                }
            } else {
                optFiltered.addAll(opt);
            }
            if (comparator != null) {
                Collections.sort(optFiltered, comparator);
            }
            super.setOptions(optFiltered);
        }
        // in case the options were set synchronously
        asyncOptionDelegate.setOptionsLoaded(true);
    }

    @Override
    protected void onReset() {
        resetOptions();
    }

    public void resetOptions() {
        asyncOptionDelegate.resetOptions();
    }

    //TODO Looks like this does not do what is expected, The actual super.options (Model) not changed
    public void refreshOptions() {
        resetOptions();
        retriveOptions(null);
        getNativeComponent().refreshOptions(); // This intended to clear list in UI.
    }

    // called when model is changed and options available, TODO refactor
    private void updateOptionsViewIfLoaded() {
        if (isViewable()) {
            return;
        }
        if (isOptionsLoaded()) {
            setOptions(getOptions());
        }
    }

    @Override
    public void retriveOptions(final AsyncOptionsReadyCallback<E> optionsReadyCallback) {
        if (isViewable()) {
            return;
        }
        if (asyncOptionDelegate.isOptionsLoaded()) {
            super.retriveOptions(optionsReadyCallback);
        } else {
            asyncOptionDelegate.retrieveOptions(optionsReadyCallback);
        }
    }

    @Override
    public void setValueByString(String name) {
        setValueByString(name, true, false);
    }

    @Override
    public void setValueByString(final String name, final boolean fireEvent, final boolean populate) {
        if (name == null && !isMandatory()) {
            setValue(null, fireEvent, populate);
        } else if (isOptionsLoaded()) {
            for (E o : getOptions()) {
                if (getItemName(o).equals(name)) {
                    setValue(o);
                    break;
                }
            }
        } else {
            hasAsyncValue = true;
            retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    for (E o : opt) {
                        if (getItemName(o).equals(name)) {
                            setValue(o, fireEvent, populate);
                            break;
                        }
                    }
                    hasAsyncValue = false;
                    AsyncValueChangeEvent.fire(CEntityComboBox.this, getValue());
                }
            });
        }
    }

    @Override
    public HandlerRegistration addAsyncValueChangeHandler(AsyncValueChangeHandler<E> handler) {
        return addHandler(handler, AsyncValueChangeEvent.getType());
    }

    @Override
    public boolean isAsyncValue() {
        return hasAsyncValue;
    }

    @Override
    public void obtainValue(final AsyncCallback<E> callback) {
        if (isAsyncValue()) {
            final HandlerRegistrationGC hrgc = new HandlerRegistrationGC();
            hrgc.add(addAsyncValueChangeHandler(new AsyncValueChangeHandler<E>() {
                @Override
                public void onAsyncChange(AsyncValueChangeEvent<E> event) {
                    callback.onSuccess(event.getValue());
                    hrgc.removeHandler();
                }
            }));
        } else {
            callback.onSuccess(getValue());
        }
    }

    @Override
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
        if (populate && (optionsFilter != null || asyncOptionDelegate.hasCriteria())) {
            // Fire options reload since optionsFilter may depend on other values in the model.
            refreshOptions();
        } else if (populate && !isOptionsLoaded() && isEditable()) {
            retriveOptions(null);
        }
    }

    @Override
    protected E preprocessValue(E value, boolean fireEvent, boolean populate) {
        if ((value != null) && (value.isNull())) {
            value = null;
        }
        if (value != null) {
            value = value.duplicate();
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    public boolean isValuesEqual(E value1, E value2) {
        if (((value1 == null) || value1.isNull()) && ((value2 == null) || value2.isNull())) {
            return true;
        } else if (isUseNamesComparison()) {
            return EqualsHelper.equals(getItemName(value1), getItemName(value2));
        } else {
            return EqualsHelper.equals(value1, value2);
        }
    }

    public void setStringViewMember(IObject<?> member) {
        stringViewMemberName = member.getFieldName();
    }

    public boolean isUseNamesComparison() {
        return useNamesComparison;
    }

    public void setUseNamesComparison(boolean useNamesComparison) {
        this.useNamesComparison = useNamesComparison;
    }

    @Override
    public String getItemName(E o) {
        if ((o == null) || (o.isNull())) {
            if (asyncOptionDelegate != null && asyncOptionDelegate.isOptStatus(Status.Loading)) {
                return "loading...";
            } else if (asyncOptionDelegate != null && asyncOptionDelegate.isOptStatus(Status.Failed)) {
                return "Error: Data unavailable";
            } else {
                // Get super's NULL presentation
                return super.getItemName(null);
            }
        } else if (stringViewMemberName != null) {
            return o.getMember(stringViewMemberName).getStringView();
        } else {
            return o.getStringView();
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(super.toString());
        b.append("\noptionsLoaded " + asyncOptionDelegate.isOptionsLoaded() + "; ");
        b.append("filtered=" + (optionsFilter != null) + "; ");
        b.append("sorted=" + (comparator != null) + "; ");
        if (getOptions() != null) {
            b.append("options=" + getOptions().size() + "; ");
        }
        return b.toString();
    }

    /** TODO MOVE to private, To be used ONLY by AsyncOptionLoadingDelegate */
    @Override
    public void onOptionsReady(List<E> opt) {
        if (isOptionsLoaded()) {
            removeComponentValidator(unavailableValidator);
        } else {
            addComponentValidator(unavailableValidator);
        }
        setOptions(opt);
    }

}
