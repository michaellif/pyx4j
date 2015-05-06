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
 * Created on Jan 8, 2015
 * @author arminea
 * @version $Id$
 */
package com.pyx4j.widgets.client.selector;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractListCrudService;

public abstract class SingleWordSuggestOptionsGrabber<E extends IEntity> implements IOptionsGrabber<E> {

    protected final AbstractListCrudService<E> service;

    protected List<E> filtered;

    public SingleWordSuggestOptionsGrabber(AbstractListCrudService<E> service) {
        this.service = service;
        this.filtered = new LinkedList<E>();
    }

    protected abstract int evaluate(E item, String suggestion);

    protected void filter(Vector<E> result, String suggestion) {
        filtered = new LinkedList<E>();
        if ("".equals(suggestion)) {
            filtered.addAll(result);
        } else {
            for (E item : result) {
                if (evaluate(item, suggestion) > 0) {
                    filtered.add(item);
                }
            }
        }
    }

    @Override
    public com.pyx4j.widgets.client.selector.IOptionsGrabber.SelectType getSelectType() {
        return SelectType.Single;
    }

}
