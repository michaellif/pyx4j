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
 * Created on Nov 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable.filter;

import java.io.Serializable;

import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

public class DataTableFilterData {

    private final String path;

    private final Restriction restriction;

    private final Serializable value;

    public DataTableFilterData(String memberPath, Restriction restriction, Serializable value) {
        this.path = memberPath;
        this.restriction = restriction;
        this.value = value;
    }

    public DataTableFilterData(Path memberPath, Restriction operand, Serializable value) {
        this(memberPath.toString(), operand, value);
    }

    public String getMemberPath() {
        return path;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public Serializable getValue() {
        return value;
    }

    public boolean isFilterOK() {
        return (getMemberPath() != null && getRestriction() != null && (getValue() != null || (getRestriction().equals(Restriction.EQUAL) || getRestriction()
                .equals(Restriction.NOT_EQUAL))));
    }

    public PropertyCriterion convertToPropertyCriterion() {
        PropertyCriterion criterion = null;
        if (isFilterOK()) {
            criterion = new PropertyCriterion(getMemberPath(), getRestriction(), getValue());
        }
        return criterion;
    }
}
