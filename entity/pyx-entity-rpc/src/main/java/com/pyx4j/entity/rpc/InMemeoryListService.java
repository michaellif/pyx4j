/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Filter;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityCriteriaFilter;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;

public class InMemeoryListService<E extends IEntity> implements AbstractListService<E> {

    private final Collection<E> values;

    public InMemeoryListService(Collection<E> values) {
        this.values = values;
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<E>> callback, EntityListCriteria<E> criteria) {
        try {
            EntitySearchResult<E> r = new EntitySearchResult<E>();
            List<E> sorted = new Vector<E>(values);

            if ((criteria.getSorts() != null) && (!criteria.getSorts().isEmpty())) {
                ListIterator<Sort> sorts = criteria.getSorts().listIterator(criteria.getSorts().size());
                while (sorts.hasPrevious()) {
                    Collections.sort(sorted, createComparator(sorts.previous()));
                }
            }

            Filter<E> f = new EntityCriteriaFilter<E>(criteria);
            int offset = 0;
            if (criteria.getPageSize() > 0) {
                offset = criteria.getPageSize() * criteria.getPageNumber();
            }
            int cnt = 0;
            for (E dto : sorted) {
                if (f.accept(dto)) {
                    if (cnt >= offset) {
                        if ((criteria.getPageSize() < 0) || (r.getData().size() < criteria.getPageSize())) {
                            r.add(dto);
                        } else {
                            r.hasMoreData(true);
                        }
                    }
                    cnt++;
                }
            }
            r.setTotalRows(cnt);

            callback.onSuccess(r);
        } catch (Throwable e) {
            callback.onFailure(e);
        }
    }

    private Comparator<? super E> createComparator(final Sort sort) {
        final Path path = new Path(sort.getPropertyPath());

        return new Comparator<E>() {

            @Override
            public int compare(E paramT1, E paramT2) {
                if (sort.isDescending()) {
                    return getValue(paramT2).compareTo(getValue(paramT1));
                } else {
                    return getValue(paramT1).compareTo(getValue(paramT2));
                }
            }

            String getValue(E entity) {
                IObject<?> valueMember = entity.getMember(path);
                if (valueMember instanceof IEntity) {
                    return valueMember.getStringView();
                } else {
                    return valueMember.getStringView();
                }
            }
        };
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        callback.onFailure(new UnsupportedOperationException());
    }

}
