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
 * Created on Jan 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterIterator<E> implements Iterator<E> {

    private final Iterator<E> iterator;

    private final Filter<E> filter;

    private E next;

    private boolean hasNext = false;

    public FilterIterator(java.util.Iterator<E> iterator, Filter<E> filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        if (hasNext) {
            return true;
        } else {
            return getNext();
        }
    }

    @Override
    public E next() {
        if (!hasNext) {
            if (!getNext()) {
                throw new NoSuchElementException();
            }
        }
        hasNext = false;
        return next;
    }

    private boolean getNext() {
        while (this.iterator.hasNext()) {
            E object = this.iterator.next();
            if (this.filter.accept(object)) {
                this.next = object;
                this.hasNext = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove() {
        if (hasNext) {
            throw new IllegalStateException();
        }
        this.iterator.remove();
    }

}
