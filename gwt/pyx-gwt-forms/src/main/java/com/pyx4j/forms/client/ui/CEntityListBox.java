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

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

// TODO - this is a logical copy of CEntityComboBox; a generic base class may make sense
public class CEntityListBox<E extends IEntity> extends CListBox<E> {

    private static final Logger log = LoggerFactory.getLogger(CEntityListBox.class);

    private static final I18n i18n = I18n.get(CEntityListBox.class);

    private EntityQueryCriteria<E> criteria;

    private OptionsFilter<E> optionsFilter;

    private Comparator<E> comparator = null;

    private boolean optionsLoaded;

    private String stringViewMemberName;

    private boolean isLoading = false;

    private boolean isUnavailable = false;

    private EditableValueValidator<List<E>> unavailableValidator;

    private EntityDataSource<E> optionsDataSource;

    private TerminableHandlingCallback optionHandlingCallback;

    private HandlerRegistration referenceDataManagerHandlerRegistration;

    public CEntityListBox() {
        super();
    }

    public CEntityListBox(SelectionMode mode) {
        super(mode);
    }

    public CEntityListBox(Class<E> entityClass) {
        this(entityClass, SelectionMode.SINGLE_PANEL);
    }

    public CEntityListBox(Class<E> entityClass, SelectionMode mode) {
        this(mode);
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

    public void setOptionsFilter(OptionsFilter<E> optionsFilter) {
        this.optionsFilter = optionsFilter;
        setOptions(getOptions());
    }

    public void setOptionsComparator(Comparator<E> comparator) {
        this.comparator = comparator;
        setOptions(getOptions());
    }

    /** the expected functionality is not implemented */
    public void setOptionsDataSource(EntityDataSource<E> optionsDataSource) {
        this.optionsDataSource = optionsDataSource;
    }

    public boolean isOptionsLoaded() {
        return this.optionsLoaded;
    }

    @Override
    public void setOptions(List<E> opt) {
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

    @Override
    protected void onValuePropagation(List<E> value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
        if (populate && optionsFilter != null || (criteria != null && criteria.getFilters() != null && !criteria.getFilters().isEmpty())) {
            // Fire options reload since optionsFilter may depend on other values in the model.
            refreshOptions();
        }
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
                unavailableValidator = new EditableValueValidator<List<E>>() {

                    @Override
                    public ValidationError isValid(CComponent<List<E>> component, List<E> value) {
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
                //TODO  optionsDataSource.obtain(criteria, handlingCallback, true);
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

    public void setStringViewMember(IObject<?> member) {
        stringViewMemberName = member.getFieldName();
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

}
