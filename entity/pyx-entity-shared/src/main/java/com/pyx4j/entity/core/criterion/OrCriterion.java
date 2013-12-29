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
 * For usage examples @see com.pyx4j.entity.rdb.QueryRDBTestCase#testCriterionOr()
 */
public class OrCriterion implements Criterion {

    private static final long serialVersionUID = 1L;

    //N.B. this is should final; but it is not to enable GWT serialization 
    private Vector<Criterion> filtersLeft;

    //N.B. this is should final; but it is not to enable GWT serialization 
    private Vector<Criterion> filtersRight;

    public OrCriterion() {
        this.filtersLeft = new Vector<Criterion>();
        this.filtersRight = new Vector<Criterion>();
    }

    public OrCriterion(Criterion criterionL, Criterion criterionR) {
        this();
        left(criterionL);
        right(criterionR);
    }

    public FiltersBuilder left() {
        return new AndCriterion(filtersLeft);
    }

    public FiltersBuilder right() {
        return new AndCriterion(filtersRight);
    }

    //N.B. this is hack to avoid adding 'final' to filters; To enable GWT serialization
    @SuppressWarnings("unused")
    private final void setFilters() {
        this.filtersLeft = null;
        this.filtersRight = null;
    }

    public OrCriterion left(Criterion criterion) {
        filtersLeft.add(criterion);
        return this;
    }

    public OrCriterion addLeft(Collection<Criterion> filters) {
        filtersLeft.addAll(filters);
        return this;
    }

    public OrCriterion right(Criterion criterion) {
        filtersRight.add(criterion);
        return this;
    }

    public OrCriterion addRight(Collection<Criterion> filters) {
        filtersRight.addAll(filters);
        return this;
    }

    public Vector<Criterion> getFiltersLeft() {
        return filtersLeft;
    }

    public Vector<Criterion> getFiltersRight() {
        return filtersRight;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.filtersLeft != null) {
            hashCode += this.filtersLeft.hashCode();
        }
        hashCode *= 0x1F;
        if (this.filtersRight != null) {
            hashCode += this.filtersRight.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrCriterion)) {
            return false;
        } else {
            return EqualsHelper.equals(this.filtersLeft, ((OrCriterion) o).filtersLeft)
                    && EqualsHelper.equals(this.filtersRight, ((OrCriterion) o).filtersRight);
        }
    }
}
