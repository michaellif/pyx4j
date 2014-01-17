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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormat;
import com.pyx4j.i18n.shared.I18n;

public class CIntegerField extends CNumberField<Integer> {

    private static final I18n i18n = I18n.get(CIntegerField.class);

    public CIntegerField() {
        super();
        setFormat(new IFormat<Integer>() {

            @Override
            public String format(Integer value) {
                if (value == null) {
                    return "";
                } else {
                    return getNumberFormat().format(value);
                }
            }

            @Override
            public Integer parse(String string) throws ParseException {
                if (CommonsStringUtils.isEmpty(string)) {
                    return null; // empty value case
                }
                try {
                    return new Double(getNumberFormat().parse(string)).intValue();
                } catch (NumberFormatException e) {
                    throw new ParseException(i18n.tr("Should be an Integer"), 0);
                }
            }

        });
    }

    @Override
    protected String dataTypeName() {
        return i18n.tr("integer");
    }

    @Override
    boolean isInRange(Integer value, Integer from, Integer to) {
        if (value == null) {
            return false;
        }
        return value >= from && value <= to;
    }

}
