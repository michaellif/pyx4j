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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ReferenceDataManager;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CListBox;

public class CEntityListBox<E extends IEntity> extends CListBox<E> {

    private static final Logger log = LoggerFactory.getLogger(CEntityListBox.class);

    private EntityQueryCriteria<E> criteria;

    private OptionsFilter<E> optionsFilter;

    private Comparator<E> comparator = null;

    private boolean optionsLoaded;

    public CEntityListBox() {
        super();
    }

    public CEntityListBox(String title, Layout layout) {
        super(title, layout);
    }

    public CEntityListBox(String title, Layout layout, Class<E> entityClass) {
        this(title, layout);
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
    public void setOptions(List<E> opt) {
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
            }
            if (comparator != null) {
                Collections.sort(optFiltered, comparator);
            }
            super.setOptions(optFiltered);
        }
    }

    @Override
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        if ((optionsLoaded) || (criteria == null)) {
            super.retriveOptions(callback);
        } else {
            final AsyncCallback<List<E>> handlingCallback = new AsyncCallback<List<E>>() {
                @Override
                public void onSuccess(List<E> result) {
                    optionsLoaded = true;
                    setOptions(result);
                    callback.onOptionsReady(getOptions());
                }

                @Override
                public void onFailure(Throwable caught) {
                    log.error("can't load {} {}", getTitle(), caught);
                }
            };
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    ReferenceDataManager.obtain(criteria, handlingCallback, true);
                }
            });
        }
    }

    @Override
    public String getItemName(E o) {
        if (o == null) {
            // Get super's NULL presentation
            return super.getItemName(null);
        } else {
            return o.getStringView();
        }
    }

}
