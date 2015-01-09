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
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.core.criterion;

import java.io.Serializable;
import java.util.Vector;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.core.IObject;

public class RangeCriterion implements Criterion {

    private static final long serialVersionUID = 1L;

    ///Not final because of GWT
    @GWTSerializable
    private Vector<PropertyCriterion> filters;

    @GWTSerializable
    protected RangeCriterion() {
    }

    public RangeCriterion(IObject<?> member, Serializable fromValue, Serializable toValue) {
        filters = new Vector<>();
        filters.add(PropertyCriterion.ge(member, fromValue));
        filters.add(PropertyCriterion.le(member, toValue));
    }

    public Vector<? extends Criterion> getFilters() {
        return filters;
    }

    public String getPropertyPath() {
        return filters.get(0).getPropertyPath();
    }

    public Serializable getFromValue() {
        return filters.get(0).getValue();
    }

    public Serializable getToValue() {
        return filters.get(1).getValue();
    }
}
