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
 * Created on Jul 14, 2010
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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.entity.client.ReferenceDataManager;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.gwt.NativeSuggestBox;
import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.INativeEditableComponent;

/**
 * !! This just is working Prototype!! Mainly copy from CEntityComboBox
 */
public class CEntityTextSuggestBox<E extends IEntity> extends CSuggestBox {

    private static final Logger log = LoggerFactory.getLogger(CEntityTextSuggestBox.class);

    private final EntityQueryCriteria<E> criteria;

    private OptionsFilter<E> optionsFilter;

    private Comparator<E> comparator = null;

    private String stringViewMemberName;

    private boolean optionsLoaded;

    private boolean isLoading = false;

    private boolean isUnavailable = false;

    private final boolean useNamesComparison = false;

    public CEntityTextSuggestBox(String title, Class<E> entityClass) {
        super(title);
        this.criteria = new EntityQueryCriteria<E>(entityClass);
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
    public void setOptions(Collection options) {
        Collection<E> opt = options;
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
    public INativeEditableComponent<E> initNativeComponent() {
        if ((getNativeComponent() == null) && (criteria != null)) {
            retriveOptions(null);
        }
        return super.initNativeComponent();
    }

    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<E>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    private class OptionsReadyPropertyChangeHandler implements OptionsChangeHandler<List<E>> {

        final HandlerRegistration handlerRegistration;

        final AsyncOptionsReadyCallback<E> callback;

        OptionsReadyPropertyChangeHandler(final AsyncOptionsReadyCallback<E> callback) {
            this.callback = callback;
            this.handlerRegistration = CEntityTextSuggestBox.this.addOptionsChangeHandler(this);
        }

        @Override
        public void onOptionsChange(OptionsChangeEvent<List<E>> event) {
            handlerRegistration.removeHandler();
            callback.onOptionsReady(event.getOptions());
        }
    }

    //@Override
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        if ((optionsLoaded) || (criteria == null)) {
            // super.retriveOptions(callback);
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
                    log.debug("loaded {} {}", result.size(), CEntityTextSuggestBox.this);
                    isLoading = false;
                    isUnavailable = false;
                    setOptions(result);
                    optionsLoaded = true;
                    if (callback != null) {
                        callback.onOptionsReady(ConverterUtils.collectionAsList(getOptions()));
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    isLoading = false;
                    isUnavailable = true;
                    log.error("can't load {} {}", getTitle(), caught);
                    setOptions(null);
                }
            };
            isLoading = true;
            if (ReferenceDataManager.isCached(criteria)) {
                ReferenceDataManager.obtain(criteria, handlingCallback, true);
            } else {
                DeferredCommand.addCommand(new Command() {
                    @Override
                    public void execute() {
                        ReferenceDataManager.obtain(criteria, handlingCallback, true);
                    }
                });
            }
        }
    }

    @Override
    public String getOptionName(Object o) {
        if ((o == null) || (((IEntity) o).isNull())) {
            if (isLoading) {
                return "loading...";
            } else if (isUnavailable) {
                return "Error: Data unavailable";
            } else {
                // Get super's NULL presentation
                return super.getOptionName(null);
            }
        } else if (stringViewMemberName != null) {
            return ((IEntity) o).getMember(stringViewMemberName).getStringView();
        } else {
            return ((IEntity) o).getStringView();
        }
    }
}
