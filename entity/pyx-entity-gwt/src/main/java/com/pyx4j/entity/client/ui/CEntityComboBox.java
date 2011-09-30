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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ReferenceDataManager;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.events.AsyncValueChangeEvent;
import com.pyx4j.forms.client.events.AsyncValueChangeHandler;
import com.pyx4j.forms.client.events.HasAsyncValue;
import com.pyx4j.forms.client.events.HasAsyncValueChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.IAcceptText;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;

public class CEntityComboBox<E extends IEntity> extends CComboBox<E> implements HasAsyncValue<E>, HasAsyncValueChangeHandlers<E>, IAcceptText {

    private static final Logger log = LoggerFactory.getLogger(CEntityComboBox.class);

    private EntityQueryCriteria<E> criteria;

    private OptionsFilter<E> optionsFilter;

    private Comparator<E> comparator = null;

    private String stringViewMemberName;

    private boolean optionsLoaded;

    private boolean isLoading = false;

    private boolean isUnavailable = false;

    private boolean useNamesComparison = false;

    private EditableValueValidator<E> unavailableValidator;

    private boolean hasAsyncValue = false;

    private EntityDataSource<E> optionsDataSource;

    public CEntityComboBox(Class<E> entityClass) {
        this(null, entityClass, (NotInOptionsPolicy) null);
    }

    public CEntityComboBox(String title, Class<E> entityClass) {
        this(title, entityClass, (NotInOptionsPolicy) null);
    }

    public CEntityComboBox(String title, Class<E> entityClass, NotInOptionsPolicy policy) {
        super(title, policy);
        this.criteria = new EntityQueryCriteria<E>(entityClass);
    }

    public E proto() {
        return this.criteria.proto();
    }

    public EntityQueryCriteria<E> addCriterion(Criterion criterion) {
        if (optionsLoaded) {
            throw new RuntimeException();
        }
        return this.criteria.add(criterion);
    }

    public void resetCriteria() {
        this.criteria.resetCriteria();
        resetOptions();
    }

    public void setOptionsFilter(OptionsFilter<E> optionsFilter) {
        this.optionsFilter = optionsFilter;
        setOptions(getOptions());
    }

    public void setOptionsComparator(Comparator<E> comparator) {
        this.comparator = comparator;
        setOptions(getOptions());
    }

    public void setOptionsDataSource(EntityDataSource<E> optionsDataSource) {
        this.optionsDataSource = optionsDataSource;
    }

    public boolean isOptionsLoaded() {
        return this.optionsLoaded;
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
    }

    public void resetOptions() {
        if (optionsLoaded) {
            optionsLoaded = false;
        }
    }

    /**
     * Should fire when component is displayed ?
     */
    @Override
    protected void onWidgetCreated() {
        super.onWidgetCreated();
        retriveOptions(null);
    }

    private class OptionsReadyPropertyChangeHandler implements OptionsChangeHandler<List<E>> {

        final HandlerRegistration handlerRegistration;

        final AsyncOptionsReadyCallback<E> callback;

        OptionsReadyPropertyChangeHandler(final AsyncOptionsReadyCallback<E> callback) {
            this.callback = callback;
            this.handlerRegistration = CEntityComboBox.this.addOptionsChangeHandler(this);
        }

        @Override
        public void onOptionsChange(OptionsChangeEvent<List<E>> event) {
            handlerRegistration.removeHandler();
            callback.onOptionsReady(event.getOptions());
        }
    }

    @Override
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        if (optionsLoaded) {
            super.retriveOptions(callback);
        } else {
            if (isLoading) {
                // Second or any other sequential call.
                if (callback != null) {
                    new OptionsReadyPropertyChangeHandler(callback);
                }
                return;
            }

            final AsyncCallback<List<E>> handlingCallback = new AsyncCallback<List<E>>() {

                @Override
                public void onSuccess(List<E> result) {
                    isLoading = false;
                    isUnavailable = false;
                    if (unavailableValidator != null) {
                        removeValueValidator(unavailableValidator);
                    }
                    setOptions(result);
                    optionsLoaded = true;
                    if (callback != null) {
                        callback.onOptionsReady(getOptions());
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    isLoading = false;
                    isUnavailable = true;
                    log.error("can't load {} {}", getTitle(), caught);
                    if (unavailableValidator == null) {
                        unavailableValidator = new EditableValueValidator<E>() {
                            @Override
                            public String getValidationMessage(CEditableComponent<E, ?> component, E value) {
                                return "Reference data unavailable";
                            }

                            @Override
                            public boolean isValid(CEditableComponent<E, ?> component, E value) {
                                return !isUnavailable;
                            }
                        };
                    }
                    addValueValidator(unavailableValidator);
                    setOptions(null);
                }
            };
            isLoading = true;
            if (optionsDataSource != null) {
                optionsDataSource.obtain(criteria, handlingCallback, true);
            } else {
                if (ReferenceDataManager.isCached(criteria)) {
                    ReferenceDataManager.obtain(criteria, handlingCallback, true);
                } else {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            ReferenceDataManager.obtain(criteria, handlingCallback, true);
                        }
                    });
                }
                ReferenceDataManager.addValueChangeHandler(new ValueChangeHandler<Class<E>>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Class<E>> event) {
                        if (criteria.getEntityClass() == event.getValue()) {
                            resetOptions();
                        }
                    }
                });
            }

        }
    }

    @Override
    @Deprecated
    public void setValueByItemName(final String name) {
        setValueByString(name);
    }

    @Override
    public void setValueByString(final String name) {
        if (name == null && !isMandatory()) {
            setValue(null);
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
                            setValue(o);
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
    public void setValue(E value) {
        if ((value != null) && (value.isNull())) {
            value = null;
        }
        super.setValue(value);
    }

    @Override
    public void populate(E value) {
        if ((value != null) && (value.isNull())) {
            value = null;
        }
        super.populate(value);

        if (optionsFilter != null || (criteria != null && !criteria.getFilters().isEmpty())) {
            // Fire options reload since optionsFilter may depend on other values in the model.
            resetOptions();
            retriveOptions(null);
        }
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || getValue().isNull();
    }

    @Override
    public boolean isValuesEquals(E value1, E value2) {
        if (((value1 == null) || value1.isNull()) && ((value2 == null) || value2.isNull())) {
            return true;
        } else if (isUseNamesComparison()) {
            return EqualsHelper.equals(getItemName(value1), getItemName(value2));
        } else {
            return super.isValuesEquals(value1, value2);
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
            if (isLoading) {
                return "loading...";
            } else if (isUnavailable) {
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
        b.append("\noptionsLoaded " + optionsLoaded + "; ");
        b.append("filtered=" + (optionsFilter != null) + "; ");
        b.append("sorted=" + (comparator != null) + "; ");
        if (getOptions() != null) {
            b.append("options=" + getOptions().size() + "; ");
        }
        return b.toString();
    }

}
