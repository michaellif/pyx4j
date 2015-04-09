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
 * Created on Jul 11, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.selector;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.widgets.client.selector.IOptionsGrabber;
import com.pyx4j.widgets.client.selector.SelectorListBox;

public class EntitySelectorListBox<E extends IEntity> extends SelectorListBox<E> {

    private E member;

    @SuppressWarnings("unchecked")
    public EntitySelectorListBox(E member) {
        super((IOptionsGrabber<E>) new EntitySuggestOptionsGrabber<E>(member.getMeta()), new IFormatter<E, String>() {

            @Override
            public String format(E value) {
                return value.getStringView();
            }

        }, new IFormatter<E, SafeHtml>() {

            @Override
            public SafeHtml format(E value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant(SimpleMessageFormat.format("<div>{0}</div>", value.getStringView()));
                return builder.toSafeHtml();
            }
        });

        this.member = member;
    }

    @Override
    public void setValue(Collection<E> collection) {
        value.clear();
        if (collection != null) {
            setEnhancedValue(collection);
        } else {
            listBox.showValue(this.value);
        }
    }

    protected void setEnhancedValue(Collection<E> values) {
        final Collection<Key> keys = new ArrayList<Key>();
        for (E value : values) {
            keys.add(value.getPrimaryKey());
        }

        @SuppressWarnings("unchecked")
        EntityQueryCriteria<E> criteria = new EntityQueryCriteria<E>((Class<E>) this.member.getObjectClass());
        criteria.add(PropertyCriterion.in(criteria.proto().id(), keys));
        ReferenceDataManager.<E> getDataSource().obtain(criteria, new AsyncCallback<EntitySearchResult<E>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error("Can't obtain enhanced values of entities.");
            }

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                if (result != null && result.getData() != null && result.getData().size() != 0) {
                    for (Key k : keys) {
                        for (E entry : result.getData()) {
                            if (entry.getPrimaryKey().equals(k)) {
                                value.add(entry);
                            }
                        }
                    }
                }
                listBox.showValue(value);
            }
        });

    }
}