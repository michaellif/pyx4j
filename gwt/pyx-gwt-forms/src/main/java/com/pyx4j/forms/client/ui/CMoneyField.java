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
 * Created on Jan 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.i18n.shared.I18n;

public class CMoneyField extends CTextFieldBase<BigDecimal, NTextBox<BigDecimal>> {

    private static final I18n i18n = I18n.get(CMoneyField.class);

    public CMoneyField() {
        super();
        setFormat(new MoneyFormat());
        addValueValidator(new TextBoxParserValidator<BigDecimal>());
        setWatermark(i18n.tr("$0.00"));
    }

    @Override
    protected NTextBox<BigDecimal> createWidget() {
        return new NTextBox<BigDecimal>(this);
    }

    class MoneyFormat implements IFormat<BigDecimal> {

        private final NumberFormat nf;

        MoneyFormat() {
            nf = NumberFormat.getFormat(i18n.tr("#,##0.00"));
        }

        @Override
        public String format(BigDecimal value) {
            if (value == null) {
                return "";
            } else {
                return "$" + nf.format(value);
            }
        }

        @Override
        public BigDecimal parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                string = string.replaceAll("[,$]+", "");
                // f and d are parsed by Double but we want to show error (VISTA-996)
                string = string.replaceAll("[fd]+", "a");
                return new BigDecimal(string);
            } catch (NumberFormatException e) {
                throw new ParseException(i18n.tr("Invalid money format. Enter valid number"), 0);
            }
        }

    }
}