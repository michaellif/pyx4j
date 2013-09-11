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
 * Created on Sep 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.formatters;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

public class PercentageFormat implements IFormat<BigDecimal> {

    private static final I18n i18n = I18n.get(PercentageFormat.class);

    private final NumberFormat nf;

    public PercentageFormat() {
        this("#.##");
    }

    public PercentageFormat(String pattern) {
        nf = NumberFormat.getFormat(pattern);
    }

    @Override
    public String format(BigDecimal value) {
        if (value == null) {
            return "";
        } else {
            return nf.format(value.multiply(new BigDecimal("100"))) + "%";
        }
    }

    @Override
    public BigDecimal parse(String string) throws ParseException {
        if (CommonsStringUtils.isEmpty(string)) {
            return null; // empty value case
        }
        try {
            string = string.replaceAll("[,%]+", "");
            return new BigDecimal(string).divide(new BigDecimal("100"));
        } catch (NumberFormatException e) {
            throw new ParseException(i18n.tr("Invalid money format. Enter valid number"), 0);
        }
    }

}