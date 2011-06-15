/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public class FilterData {

    @Translatable
    public enum Operands {
        is,

        isNot,

        contains,
//        
// TODO ? These criterias aren't supported by DB search engine currently, so postpone implementation ?          
//        doesNotContain,
//        beginsWith,
//        endsWith,

        lessThen,

        greaterThen;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    private final String path;

    private final Operands operand;

    private final String value;

    public FilterData(String memberPath, Operands operand, String value) {
        this.path = memberPath;
        this.operand = operand;
        this.value = value;
    }

    public String getMemberPath() {
        return path;
    }

    public Operands getOperand() {
        return operand;
    }

    public String getValue() {
        return value;
    }
}
