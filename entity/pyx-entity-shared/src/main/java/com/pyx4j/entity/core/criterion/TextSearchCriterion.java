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
 * Created on Jun 1, 2015
 * @author michaellif
 */
package com.pyx4j.entity.core.criterion;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.core.IObject;

public class TextSearchCriterion implements Criterion {

    private static final long serialVersionUID = 1L;

    private String textQuery;

    @GWTSerializable
    public TextSearchCriterion() {
    }

    public TextSearchCriterion(String textQuery) {
        this.textQuery = textQuery;
    }

    public String getTextQuery() {
        return textQuery;
    }

    /**
     * Translates to Like operators for unindexed property
     */
    public static Criterion translateToLike(IObject<?> member, String textQuery) {
        AndCriterion and = new AndCriterion();
        for (String str : textQuery.split(" ")) {
            if (CommonsStringUtils.isEmpty(str)) {
                continue;
            }
            and.or().like(member, str.trim() + "*").like(member, "* " + str.trim() + "*");
        }
        return and;
    }

    @Override
    public String toString() {
        return "TextSearchCriterion [textQuery=" + textQuery + "]";
    }

}
