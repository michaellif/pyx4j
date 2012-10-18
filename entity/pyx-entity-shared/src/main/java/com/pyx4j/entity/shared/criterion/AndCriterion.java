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
package com.pyx4j.entity.shared.criterion;

import java.util.Vector;

/**
 * For usage examples @see com.pyx4j.entity.rdb.QueryRDBTestCase#testCriterionOr()
 */
public class AndCriterion extends FiltersBuilder implements Criterion {

    private static final long serialVersionUID = 1L;

    private Vector<Criterion> filters;

    public AndCriterion() {
    }

    public AndCriterion(Criterion... criterions) {
        for (Criterion criterion : criterions) {
            add(criterion);
        }
    }

    public AndCriterion add(Criterion criterion) {
        if (filters == null) {
            filters = new Vector<Criterion>();
        }
        filters.add(criterion);
        return this;
    }

    @Override
    protected FiltersBuilder addCriterion(Criterion criterion) {
        return add(criterion);
    }

    public Vector<Criterion> getFilters() {
        return filters;
    }

}
