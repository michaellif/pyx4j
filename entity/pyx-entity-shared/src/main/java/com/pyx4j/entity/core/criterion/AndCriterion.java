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
 * Created on Nov 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.core.criterion;

import java.util.Collection;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;

/**
 * For usage examples @see com.pyx4j.entity.rdb.QueryRDBTestCase#testCriterionAnd()
 */
public class AndCriterion extends FiltersBuilder implements Criterion {

    private static final long serialVersionUID = 1L;

    //N.B. this is should final; but it is not to enable GWT serialization 
    private Vector<Criterion> filters;

    public AndCriterion() {
        this(new Vector<Criterion>());
    }

    AndCriterion(Vector<Criterion> filters) {
        this.filters = filters;
    }

    public AndCriterion(Criterion... criterions) {
        this();
        for (Criterion criterion : criterions) {
            add(criterion);
        }
    }

    //N.B. this is hack to avoid adding 'final' to filters; To enable GWT serialization
    @SuppressWarnings("unused")
    private final void setFilters() {
        this.filters = null;
    }

    public AndCriterion add(Criterion criterion) {
        this.filters.add(criterion);
        return this;
    }

    public AndCriterion addAll(Collection<Criterion> filters) {
        this.filters.addAll(filters);
        return this;
    }

    @Override
    protected FiltersBuilder addCriterion(Criterion criterion) {
        return add(criterion);
    }

    public Vector<Criterion> getFilters() {
        return filters;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.filters != null) {
            hashCode += this.filters.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AndCriterion)) {
            return false;
        } else {
            return EqualsHelper.equals(this.filters, ((AndCriterion) o).filters);
        }
    }
}
