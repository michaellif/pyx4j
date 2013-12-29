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
package com.pyx4j.entity.core.criterion;

import java.util.List;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Filter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;

/**
 * Simple in memory criteria Filter
 */
public class EntityCriteriaFilter<E extends IEntity> implements Filter<E> {

    EntityQueryCriteria<E> criteria;

    public EntityCriteriaFilter(EntityQueryCriteria<E> criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean accept(E input) {
        if ((criteria.getFilters() != null) && (!criteria.getFilters().isEmpty())) {
            return accept(input, criteria.getFilters());
        } else {
            return true;
        }
    }

    private boolean accept(E input, List<Criterion> filters) {
        boolean accept = true;
        for (Criterion cr : criteria.getFilters()) {
            if (cr instanceof PropertyCriterion) {
                accept = accept(input, (PropertyCriterion) cr);
            } else if (cr instanceof OrCriterion) {
                accept = (accept(input, ((OrCriterion) cr).getFiltersLeft()) || accept(input, ((OrCriterion) cr).getFiltersRight()));
            } else {
                throw new RuntimeException("Unsupported Operator " + cr.getClass());
            }
            if (!accept) {
                return false;
            }
        }
        return true;
    }

    private boolean accept(E input, PropertyCriterion propertyCriterion) {
        IObject<?> valueMember = input.getMember(new Path(propertyCriterion.getPropertyPath()));
        Object value;
        if (valueMember instanceof IEntity) {
            value = valueMember;
        } else {
            value = valueMember.getValue();
        }
        switch (propertyCriterion.getRestriction()) {
        case NOT_EQUAL:
            return !EqualsHelper.equals(value, propertyCriterion.getValue());
        case EQUAL:
            return EqualsHelper.equals(value, propertyCriterion.getValue());
        case RDB_LIKE:
            return wildCardMatches((String) (propertyCriterion.getValue()), value);
        default:
            throw new RuntimeException("Unsupported Operator " + propertyCriterion.getRestriction());
        }
    }

    private static boolean hasLikeValue(String pattern) {
        return pattern.contains(String.valueOf(PropertyCriterion.WILDCARD_CHAR));
    }

    private boolean wildCardMatches(String pattern, Object value) {
        if (value == null) {
            return false;
        } else if (hasLikeValue(pattern)) {
            return value.toString().matches(pattern.replace(String.valueOf(PropertyCriterion.WILDCARD_CHAR), ".*?") + ".*");
        } else {
            return value.toString().matches(".*?" + pattern + ".*");
        }
    }
}
