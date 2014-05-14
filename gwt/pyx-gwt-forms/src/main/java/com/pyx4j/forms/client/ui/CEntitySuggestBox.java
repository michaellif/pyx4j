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
 * Created on Sep 29, 2010
 * @author michaellif
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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.core.EntityFactory;
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
import com.pyx4j.forms.client.validators.ComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.i18n.shared.I18n;

public class CEntitySuggestBox<E extends IEntity> extends CAbstractSuggestBox<E> implements HasAsyncValue<E>, HasAsyncValueChangeHandlers<E>, IAcceptText,
        AsyncOptionsReadyCallback<E> {

    private static final Logger log = LoggerFactory.getLogger(CEntitySuggestBox.class);

    private static final I18n i18n = I18n.get(CEntitySuggestBox.class);

    private final Class<E> entityClass;

    private OptionsFilter<E> optionsFilter;

    private Comparator<E> comparator = null;

    private String stringViewMemberName;

    private boolean hasAsyncValue = false;

    private final AsyncOptionLoadingDelegate<E> asyncOptionDelegate;

    private ComponentValidator<E> unavailableValidator;

    public CEntitySuggestBox(Class<E> entityClass) {
        super();
        this.entityClass = entityClass;
        this.asyncOptionDelegate = new AsyncOptionLoadingDelegate<E>(entityClass, this, null);
        this.unavailableValidator = new AbstractComponentValidator<E>() {
            @Override
            public FieldValidationError isValid() {
                return new FieldValidationError(getComponent(), i18n.tr("Reference data unavailable"));
            }
        };
        setFormatter(new EntitySuggestFormatter());
        setParser(new EntitySuggestParser());
        retrieveOptions(null);
    }

    public EntityQueryCriteria<E> addCriterion(Criterion criterion) {
        if (isOptionsLoaded()) {
            throw new RuntimeException();
        }
        return asyncOptionDelegate.addCriterion(criterion);
    }

    public E proto() {
        return asyncOptionDelegate.proto();
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
        asyncOptionDelegate.resetOptions();
    }

    public void retrieveOptions(final AsyncOptionsReadyCallback<E> optionsReadyCallback) {
        if (!asyncOptionDelegate.isOptionsLoaded()) {
            asyncOptionDelegate.retrieveOptions(optionsReadyCallback);
        }
    }

    @Override
    public String getOptionName(E o) {
        if (o == null || o.isNull()) {
            if (asyncOptionDelegate != null && asyncOptionDelegate.isOptStatus(Status.Loading)) {
                return "loading...";
            } else if (asyncOptionDelegate != null && asyncOptionDelegate.isOptStatus(Status.Failed)) {
                return "Error: Data unavailable";
            } else {
                // Get super's NULL presentation
                return super.getOptionName(null);
            }
        } else if (stringViewMemberName != null) {
            return o.getMember(stringViewMemberName).getStringView();
        } else {
            return o.getStringView();
        }
    }

    public void setStringViewMember(IObject<?> member) {
        stringViewMemberName = member.getFieldName();
    }

    @Override
    public void setValueByString(final String name) {
        if (name == null && !isMandatory()) {
            setValue(null);
        } else if (isOptionsLoaded()) {
            for (E o : getOptions()) {
                if (getOptionName(o).equals(name)) {
                    setValue(o);
                    break;
                }
            }
        } else {
            hasAsyncValue = true;
            retrieveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    for (E o : opt) {
                        if (getOptionName(o).equals(name)) {
                            setValue(o);
                            break;
                        }
                    }
                    hasAsyncValue = false;
                    AsyncValueChangeEvent.fire(CEntitySuggestBox.this, getValue());
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

    class EntitySuggestFormatter implements IFormatter<E, String> {

        @Override
        public String format(E value) {
            if (stringViewMemberName != null) {
                return value.getMember(stringViewMemberName).getStringView();
            } else {
                return value.getStringView();
            }
        }
    }

    class EntitySuggestParser implements IParser<E> {

        @Override
        public E parse(String string) {
            for (E option : getOptions()) {
                if (getOptionName(option).equals(string)) {
                    return option;
                }
            }
            E entity = EntityFactory.create(entityClass);
            if (stringViewMemberName != null) {
                entity.setMemberValue(stringViewMemberName, string);
            } else {
                entity.setMemberValue(entity.getEntityMeta().getToStringMemberNames().get(0), string);
            }
            return entity;
        }
    }

    @Override
    public boolean isValueEmpty() {
        if (getValue() == null || super.isValueEmpty() || getValue().isNull()) {
            return true;
        }
        return CommonsStringUtils.isEmpty(getFormatter().format(getValue()));
    }

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
