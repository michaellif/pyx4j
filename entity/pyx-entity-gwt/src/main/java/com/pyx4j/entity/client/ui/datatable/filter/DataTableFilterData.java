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
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public class DataTableFilterData {

    @I18n
    public static enum Operators {

        is,

        isNot,

        like,
//        
// TODO ? These criterias aren't supported by DB search engine currently, so postpone implementation ?          
//        contains,
//        doesNotContain,
//        beginsWith,
//        endsWith,
//        
        lessThan,

        greaterThan,

        earlierThan,

        laterThan;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    private final String path;

    private final Operators operand;

    private final Serializable value;

    public DataTableFilterData(String memberPath, Operators operand, Serializable value) {
        this.path = memberPath;
        this.operand = operand;
        this.value = value;
    }

    public DataTableFilterData(Path memberPath, Operators operand, Serializable value) {
        this(memberPath.toString(), operand, value);
    }

    public String getMemberPath() {
        return path;
    }

    public Operators getOperand() {
        return operand;
    }

    public Serializable getValue() {
        return value;
    }

    public boolean isFilterOK() {
        return (getMemberPath() != null && getOperand() != null && (getValue() != null || (getOperand().equals(Operators.is) || getOperand().equals(
                Operators.isNot))));
    }

    public PropertyCriterion convertToPropertyCriterion() {
        PropertyCriterion criterion = null;
        if (isFilterOK()) {
            switch (getOperand()) {
            case is:
                criterion = new PropertyCriterion(getMemberPath(), Restriction.EQUAL, getValue());
                break;
            case isNot:
                criterion = new PropertyCriterion(getMemberPath(), Restriction.NOT_EQUAL, getValue());
                break;
            case like:
                criterion = new PropertyCriterion(getMemberPath(), Restriction.RDB_LIKE, getValue());
                break;
            case laterThan:
            case greaterThan:
                criterion = new PropertyCriterion(getMemberPath(), Restriction.GREATER_THAN, getValue());
                break;
            case earlierThan:
            case lessThan:
                criterion = new PropertyCriterion(getMemberPath(), Restriction.LESS_THAN, getValue());
                break;
            }
        }
        return criterion;
    }

}
