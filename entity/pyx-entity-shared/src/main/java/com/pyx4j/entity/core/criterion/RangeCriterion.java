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
 * Created on Dec 24, 2014
 * @author michaellif
 */
package com.pyx4j.entity.core.criterion;

import java.io.Serializable;
import java.util.Vector;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;

public class RangeCriterion implements Criterion {

    private static final long serialVersionUID = 1L;

    private Path propertyPath;

    private Serializable fromValue;

    private Serializable toValue;

    @GWTSerializable
    protected RangeCriterion() {
    }

    public RangeCriterion(IObject<?> member, Serializable fromValue, Serializable toValue) {
        this(member.getPath(), fromValue, toValue);
    }

    public RangeCriterion(Path path, Serializable fromValue, Serializable toValue) {
        this.propertyPath = path;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public Vector<? extends Criterion> getFilters() {
        Vector<Criterion> filters = new Vector<>();
        if (fromValue != null) {
            filters.add(new PropertyCriterion(propertyPath, Restriction.GREATER_THAN_OR_EQUAL, fromValue));
        }
        if (toValue != null) {
            filters.add(new PropertyCriterion(propertyPath, Restriction.LESS_THAN_OR_EQUAL, toValue));
        }
        return filters;
    }

    public Path getPropertyPath() {
        return propertyPath;
    }

    public Serializable getFromValue() {
        return fromValue;
    }

    public Serializable getToValue() {
        return toValue;
    }

    @Override
    public boolean isEmpty() {
        return fromValue == null && toValue == null;
    }
}
