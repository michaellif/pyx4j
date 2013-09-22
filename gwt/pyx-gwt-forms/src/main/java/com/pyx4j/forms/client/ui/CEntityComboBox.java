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
package com.pyx4j.forms.client.ui;

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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.events.AsyncValueChangeEvent;
import com.pyx4j.forms.client.events.AsyncValueChangeHandler;
import com.pyx4j.forms.client.events.HasAsyncValue;
import com.pyx4j.forms.client.events.HasAsyncValueChangeHandlers;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.i18n.shared.I18n;

public class CEntityComboBox<E extends IEntity> extends CComboBox<E> implements HasAsyncValue<E>, HasAsyncValueChangeHandlers<E>, IAcceptText {

    private static final I18n i18n = I18n.get(CEntityComboBox.class);

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

    private HandlerRegistration referenceDataManagerHandlerRegistration;

    private TerminableHandlingCallback optionHandlingCallback = null;

    public CEntityComboBox(Class<E> entityClass) {
        this(null, entityClass, (NotInOptionsPolicy) null, null);
    }

    public CEntityComboBox(String title, Class<E> entityClass) {
        this(title, entityClass, (NotInOptionsPolicy) null, null);
    }

    public CEntityComboBox(String title, Class<E> entityClass, NotInOptionsPolicy policy, EntityDataSource<E> optionsDataSource) {
        super(title, policy);
        this.criteria = new EntityQueryCriteria<E>(entityClass);
        this.optionsDataSource = optionsDataSource;
        retriveOptions(null);
    }

    public E proto() {
        return this.criteria.proto();
    }

    public EntityQueryCriteria<E> addCriterion(Criterion criterion) {
        return this.criteria.add(criterion);
    }

    public void resetCriteria() {
        this.criteria.resetCriteria();
    }

    public void setOptionsFilter(OptionsFilter<E> optionsFilter) {
        this.optionsFilter = optionsFilter;
        setOptions(getOptions());
    }

    public void setOptionsComparator(Comparator<E> comparator) {
        this.comparator = comparator;
        setOptions(getOptions());
    }

    public boolean isOptionsLoaded() {
        return this.optionsLoaded;
    }

    @Override
    public void setOptions(Collection<E> opt) {
        optionsLoaded = true;
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

    @Override
    protected void onReset() {
        resetOptions();
    }

    public void resetOptions() {
        if (optionsLoaded) {
            optionsLoaded = false;
        }
    }

    public void refreshOptions() {
        resetOptions();
        retriveOptions(null);
        getWidget().refreshOptions();
    }

    private class TerminableHandlingCallback implements AsyncCallback<List<E>> {
        private final AsyncOptionsReadyCallback<E> callback;

        private boolean cancelled = false;

        public TerminableHandlingCallback(AsyncOptionsReadyCallback<E> callback) {
            this.callback = callback;
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public void onSuccess(List<E> result) {
            if (cancelled) {
                return;
            }
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
            if (cancelled) {
                return;
            }
            isLoading = false;
            isUnavailable = true;
            log.error("can't load {} {}", getTitle(), caught);
            if (unavailableValidator == null) {
                unavailableValidator = new EditableValueValidator<E>() {

                    @Override
                    public ValidationError isValid(CComponent<E> component, E value) {
                        return !isUnavailable ? null : new ValidationError(component, i18n.tr("Reference data unavailable"));
                    }
                };
            }
            addValueValidator(unavailableValidator);
            setOptions(null);
        }
    }

    @Override
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        if (isViewable()) {
            return;
        }
        if (optionsLoaded) {
            super.retriveOptions(callback);
        } else {
            if (isLoading && (optionHandlingCallback != null)) {
                // Second or any other sequential call cancels previous callback
                optionHandlingCallback.cancel();
            }
            isLoading = true;
            optionHandlingCallback = new TerminableHandlingCallback(callback);
            if (optionsDataSource != null) {
                optionsDataSource.obtain(criteria, new AsyncCallback<EntitySearchResult<E>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        optionHandlingCallback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(EntitySearchResult<E> result) {
                        optionHandlingCallback.onSuccess(result.getData());
                    }
                });
            } else {
                if (ReferenceDataManager.isCached(criteria)) {
                    ReferenceDataManager.obtain(criteria, optionHandlingCallback, true);
                } else {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        // keep current criteria and callback reference as they can change by the time the command executes
                        final TerminableHandlingCallback handlingCallback = optionHandlingCallback;

                        final EntityQueryCriteria<E> crit = criteria.iclone();

                        @Override
                        public void execute() {
                            ReferenceDataManager.obtain(crit, handlingCallback, true);
                        }
                    });
                }
                registerDataChangeHandler();
            }

        }
    }

    private void registerDataChangeHandler() {
        if (referenceDataManagerHandlerRegistration == null) {
            referenceDataManagerHandlerRegistration = ReferenceDataManager.addValueChangeHandler(new ValueChangeHandler<Class<E>>() {
                @Override
                public void onValueChange(ValueChangeEvent<Class<E>> event) {
                    if ((criteria.getEntityClass() == event.getValue()) || (IEntity.class == event.getValue())) {
                        resetOptions();
                    }
                }
            });
        }
    }

    @Override
    @Deprecated
    public void setValueByItemName(final String name) {
        setValueByString(name);
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
        if (populate && optionsFilter != null || (criteria != null && criteria.getFilters() != null && !criteria.getFilters().isEmpty())) {
            // Fire options reload since optionsFilter may depend on other values in the model.
            refreshOptions();
        }
    }

    @Override
    protected E preprocessValue(E value, boolean fireEvent, boolean populate) {
        if ((value != null) && (value.isNull())) {
            value = null;
        }
        return value;
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
