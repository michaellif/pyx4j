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
 * Created on Jan 14, 2015
 * @author arminea
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.selector;

import java.util.Comparator;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.widgets.client.selector.MultyWordSuggestOptionsGrabber;

public class EntitySuggestOptionsGrabber<E extends IEntity> extends MultyWordSuggestOptionsGrabber<IEntity> {

    @SuppressWarnings("unchecked")
    public EntitySuggestOptionsGrabber(Class<E> entityClass) {

        setFormatter(new IFormatter<IEntity, String>() {
            @Override
            public String format(IEntity value) {
                return value.getStringView();
            }

        });

        setComparator(new Comparator<IEntity>() {

            @Override
            public int compare(IEntity o1, IEntity o2) {
                return o1.getStringView().compareTo(o2.getStringView());
            }

        });

        ReferenceDataManager.<IEntity> getDataSource().obtain(new EntityQueryCriteria<IEntity>((Class<IEntity>) entityClass),
                new AsyncCallback<EntitySearchResult<IEntity>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        setAllOptions(null);
                        //TODO log
                    }

                    @Override
                    public void onSuccess(EntitySearchResult<IEntity> result) {
                        setAllOptions(result.getData());
                    }

                });
    }
}