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
 * Created on May 10, 2014
 * @author stanp
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.AsyncLoadingHandler.Status;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;

public class AsyncOptionLoadingDelegate<E extends IEntity> {

    private final AsyncOptionsReadyCallback<E> delegator;

    private AsyncLoadingHandler optionLoadingHandler;

    private final EntityDataSource<E> optionsDataSource;

    private final EntityQueryCriteria<E> criteria;

    private boolean optionsLoaded = false;

    public AsyncOptionLoadingDelegate(Class<E> entityClass, AsyncOptionsReadyCallback<E> delegator, EntityDataSource<E> optionsDataSource) {
        this.delegator = delegator;
        this.criteria = new EntityQueryCriteria<E>(entityClass);
        this.optionsDataSource = optionsDataSource == null ? ReferenceDataManager.<E> getDataSource() : optionsDataSource;
        // register option data change handler in case options change on the source
        this.optionsDataSource.addDataChangeHandler(new ValueChangeHandler<Class<E>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Class<E>> event) {
                resetOptions();
            }
        });

    }

    public void retrieveOptions(final AsyncOptionsReadyCallback<E> optionsReadyCallback) {
        if (optionLoadingHandler != null) {
            optionLoadingHandler.cancel();
        }
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                // clone current criteria as it may change by the time the command executes
                optionLoadingHandler = optionsDataSource.obtain(criteria.iclone(), new AsyncCallback<EntitySearchResult<E>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        delegator.onOptionsReady(null);
                    }

                    @Override
                    public void onSuccess(EntitySearchResult<E> result) {
                        setOptionsLoaded(true);
                        delegator.onOptionsReady(result.getData());
                        if (optionsReadyCallback != null) {
                            optionsReadyCallback.onOptionsReady(result.getData());
                        }
                    }

                });
            }
        });
    }

    public boolean isOptStatus(AsyncLoadingHandler.Status status) {
        return optionLoadingHandler == null ? (status == Status.LoadNotRequested) : optionLoadingHandler.isStatus(status);
    }

    public void setOptionsLoaded(boolean loaded) {
        optionsLoaded = loaded;
    }

    public boolean isOptionsLoaded() {
        return optionsLoaded;
    }

    // Do not process old requests when forms are detached of criteria was changed.
    public void resetOptions() {
        if (optionLoadingHandler != null) {
            optionLoadingHandler.cancel();
        }
        setOptionsLoaded(false);
    }

    public E proto() {
        return criteria.proto();
    }

    public EntityQueryCriteria<E> addCriterion(Criterion criterion) {
        resetOptions();
        return criteria.add(criterion);
    }

    public void resetCriteria() {
        resetOptions();
        criteria.resetCriteria();
    }

    public boolean hasCriteria() {
        return criteria.hasCriteria();
    }
}
