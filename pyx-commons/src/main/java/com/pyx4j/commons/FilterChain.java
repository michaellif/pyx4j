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
 * Created on Jan 13, 2016
 * @author vlads
 */
package com.pyx4j.commons;

import java.util.ArrayList;
import java.util.Collection;

public final class FilterChain<E> implements Filter<E> {

    private final Collection<Filter<E>> filters;

    public FilterChain(Filter<E> filter1, Filter<E> filter2) {
        this.filters = new ArrayList<>();
        this.filters.add(filter1);
        this.filters.add(filter2);
    }

    @Override
    public boolean accept(E input) {
        for (Filter<E> filter : filters) {
            if (filter != null && !filter.accept(input)) {
                return false;
            }
        }
        return true;
    }

}
