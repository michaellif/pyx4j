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
 */
package com.pyx4j.forms.client.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.SimpleFormat;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.context.UserMoneyFormat;

public class CMoneyField extends CTextFieldBase<BigDecimal, NTextBox<BigDecimal>> {

    static final I18n i18n = I18n.get(CMoneyField.class);

    private static final String symbol = i18n.tr("$");

    public CMoneyField() {
        super();
        setFormatter(new UserMoneyFormat());
        setParser(new MoneyParser());
        setNativeComponent(new NTextBox<BigDecimal>(this));
    }

    //TODO move to UserMoneyFormat
    public static class MoneyParser implements IParser<BigDecimal> {

        private String symbol = i18n.tr("$");

        @I18nContext(javaFormatFlag = true)
        @Override
        public BigDecimal parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                String moneyFormat = i18n.tr("#,##0.00");

                string = string.trim();
                if (string.startsWith(symbol)) {
                    string = string.substring(1);
                }
                return new BigDecimal(SimpleFormat.numberParse(string, moneyFormat).doubleValue()).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                throw new ParseException(i18n.tr("Invalid money format. Enter valid number"), 0);
            }
        }

    }
}