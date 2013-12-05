/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.i18n.shared.I18n;

public class CDoubleField extends CNumberField<Double> {

    private static final I18n i18n = I18n.get(CDoubleField.class);

    public CDoubleField() {
        super();
        setFormat(new IFormat<Double>() {

            @Override
            public String format(Double value) {
                if (value == null) {
                    return "";
                } else {
                    return getNumberFormat().format(value);
                }
            }

            @Override
            public Double parse(String string) throws ParseException {
                if (CommonsStringUtils.isEmpty(string)) {
                    return null; // empty value case
                }
                try {
                    return getNumberFormat().parse(string);
                } catch (NumberFormatException e) {
                    throw new ParseException(i18n.tr("Should Be A Numeric Value"), 0);
                }
            }

        });
    }

    @Override
    boolean isInRange(Double value, Double from, Double to) {
        if (value == null) {
            return false;
        }
        return value >= from && value <= to;
    }

}
