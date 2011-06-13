/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
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
