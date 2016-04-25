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
 * Created on Dec 22, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.io.Serializable;

import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;

public abstract class FilterEditorBase extends Composite implements IFilterEditor {

    private final IObject<?> member;

    public FilterEditorBase(IObject<?> member) {
        this.member = member;
    }

    protected IObject<?> getMember() {
        return member;
    }

    @Override
    public void onShown() {

    }

    @Override
    public void onHidden() {

    }

    protected RangeCriterion toRangeCriterion(PropertyCriterion propertyCriterion) {
        Serializable fromValue = null;
        Serializable toValue = null;
        switch (propertyCriterion.getRestriction()) {
        case EQUAL:
            fromValue = toValue = propertyCriterion.getValue();
            break;
        case GREATER_THAN:
        case GREATER_THAN_OR_EQUAL:
            fromValue = propertyCriterion.getValue();
            break;
        case LESS_THAN:
        case LESS_THAN_OR_EQUAL:
            toValue = propertyCriterion.getValue();
            break;
        default:
            throw new Error("Conversion from " + propertyCriterion + " to range unimplemented");
        }
        return new RangeCriterion(propertyCriterion.getPropertyPath(), fromValue, toValue);
    }
}
