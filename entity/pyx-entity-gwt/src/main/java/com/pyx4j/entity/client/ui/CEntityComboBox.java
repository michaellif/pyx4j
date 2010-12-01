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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ReferenceDataManager;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.INativeComboBox;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class CEntityComboBox<E extends IEntity> extends CComboBox<E> {

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

    public CEntityComboBox() {
        this(null);
    }

    public CEntityComboBox(String title) {
        this(title, (NotInOptionsPolicy) null);
    }

    public CEntityComboBox(String title, Class<E> entityClass) {
        this(title, (NotInOptionsPolicy) null);
        this.criteria = new EntityQueryCriteria<E>(entityClass);
    }

    public CEntityComboBox(String title, NotInOptionsPolicy policy) {
        super(title, policy);
    }

    public E meta() {
        return this.criteria.meta();
    }

    public EntityQueryCriteria<E> addCriterion(Criterion criterion) {
        if (optionsLoaded) {
            throw new RuntimeException();
        }
        return this.criteria.add(criterion);
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
                log.debug("filtered {}", this);
            } else {
                optFiltered.addAll(opt);
            }
            if (comparator != null) {
                Collections.sort(optFiltered, comparator);
                log.debug("sorted {}", this);
            }
            super.setOptions(optFiltered);
        }
    }

    public void resetOptions() {
        if ((optionsLoaded) || (criteria != null)) {
            optionsLoaded = false;
        }
    }

    /**
     * Should fire when component is displayed ?
     */
    @Override
    public INativeComboBox<E> initNativeComponent() {
        if ((getNativeComponent() == null) && (criteria != null)) {
            retriveOptions(null);
        }
        return super.initNativeComponent();
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
        if ((optionsLoaded) || (criteria == null)) {
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
                    log.debug("loaded {} {}", result.size(), CEntityComboBox.this);
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
                            public String getValidationMessage(CEditableComponent<E> component, E value) {
                                return "Reference data unavailable";
                            }

                            @Override
                            public boolean isValid(CEditableComponent<E> component, E value) {
                                return !isUnavailable;
                            }
                        };
                    }
                    addValueValidator(unavailableValidator);
                    setOptions(null);
                }
            };
            isLoading = true;
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
        }
    }

    @Override
    public void setValueByItemName(final String name) {
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
            retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    for (E o : opt) {
                        if (getItemName(o).equals(name)) {
                            setValue(o);
                            break;
                        }
                    }
                }
            });
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
