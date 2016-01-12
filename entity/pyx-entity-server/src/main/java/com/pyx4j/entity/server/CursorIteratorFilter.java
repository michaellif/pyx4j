/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 12, 2015
 * @author vlads
 */
package com.pyx4j.entity.server;

import java.util.NoSuchElementException;

import com.pyx4j.commons.Filter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;

public final class CursorIteratorFilter<E extends IEntity> implements ICursorIterator<E> {

    private final ICursorIterator<E> unfiltered;

    private final int maxResults;

    private final Filter<E> filter;

    private int returnedResultsCount = 0;

    private E next;

    protected CursorIteratorFilter(final ICursorIterator<E> unfiltered, int maxResults, Filter<E> filter) {
        this.unfiltered = unfiltered;
        this.maxResults = maxResults;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }
        if (maxResults > 0 && returnedResultsCount >= maxResults) {
            return false;
        }

        while (unfiltered.hasNext()) {
            E ent = unfiltered.next();
            if (filter.accept(ent)) {
                next = ent;
                break;
            }
        }
        return (next != null);
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            returnedResultsCount++;
            return next;
        } finally {
            next = null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodedCursorReference() {
        return unfiltered.encodedCursorReference();
    }

    @Override
    public void close() {
        unfiltered.close();
    }

}
