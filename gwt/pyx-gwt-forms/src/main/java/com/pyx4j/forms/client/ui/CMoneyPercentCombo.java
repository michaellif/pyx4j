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
 * Created on May 29, 2014
 * @author stanp
 */
package com.pyx4j.forms.client.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;

public class CMoneyPercentCombo extends CTextFieldBase<IMoneyPercentAmount, NMoneyPercentCombo> {

    static final I18n i18n = I18n.get(CMoneyPercentCombo.class);

    @I18nContext(javaFormatFlag = true)
    private static final String defaultMoneyEditableFormat() {
        return i18n.tr("#,##0.00");
    }

    private final NumberFormat nf = NumberFormat.getFormat(defaultMoneyEditableFormat());

    @I18nContext(javaFormatFlag = true)
    private static final String defaultMoneyFormat() {
        return i18n.tr("$#,##0.00");
    }

    private final NumberFormat mf = NumberFormat.getFormat(defaultMoneyFormat());

    @I18nContext(javaFormatFlag = true)
    private static final String defaultPercent() {
        return i18n.tr("#0.00%");
    }

    private final NumberFormat pf = NumberFormat.getFormat(defaultPercent());

    private ValueType amountType;

    public CMoneyPercentCombo() {
        setFormatter(new MoneyPercentFormat());
        setParser(new MoneyPercentParser());
        setNativeComponent(new NMoneyPercentCombo(this));
    }

    public void setAmountType(ValueType type) {
        amountType = type;
        // trigger validation flow
        refresh(true);
        // also need to call formatter
        getNativeComponent().refresh();
    }

    class MoneyPercentFormat implements IFormatter<IMoneyPercentAmount, String> {

        @Override
        public String format(IMoneyPercentAmount value) {
            if (value == null) {
                return nf.format(BigDecimal.ZERO);
            }
            String result = null;
            if (amountType != null) {
                switch (amountType) {
                case Monetary:
                    if (isEditable()) {
                        result = value.amount().isNull() ? "" : mf.format(value.amount().getValue(BigDecimal.ZERO));
                        value.percent().setValue(null);
                    } else {
                        result = mf.format(value.amount().getValue(BigDecimal.ZERO));
                    }
                    break;
                case Percentage:
                    if (isEditable()) {
                        result = value.percent().isNull() ? "" : pf.format(value.percent().getValue(BigDecimal.ZERO));
                        value.amount().setValue(null);
                    } else {
                        result = pf.format(value.percent().getValue(BigDecimal.ZERO));
                    }
                    break;
                }
            }
            return result;
        }
    }

    class MoneyPercentParser implements IParser<IMoneyPercentAmount> {

        @Override
        public IMoneyPercentAmount parse(String string) throws ParseException {
            IMoneyPercentAmount value = EntityFactory.create(IMoneyPercentAmount.class);
            if (CommonsStringUtils.isEmpty(string)) {
                return value;
            }
            try {
                if (amountType == null) {
                    throw new ParseException(i18n.tr("Invalid value. Select valid type."), 0);
                }
                BigDecimal amount = null;
                switch (amountType) {
                case Monetary:
                    try {
                        // in case value has been entered in monetary format
                        amount = new BigDecimal(mf.parse(string));
                    } catch (NumberFormatException ignore) {
                        // parse as plain number
                        amount = new BigDecimal(nf.parse(string));
                    }
                    value.amount().setValue(amount.setScale(2, RoundingMode.HALF_UP));
                    value.percent().setValue(null);
                    break;
                case Percentage:
                    try {
                        // in case value has been entered in percentage format
                        amount = new BigDecimal(pf.parse(string));
                    } catch (NumberFormatException ignore) {
                        // parse as plain number
                        amount = new BigDecimal(nf.parse(string));
                    }
                    value.percent().setValue(amount.setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal("100")));
                    value.amount().setValue(null);
                }
                return value;
            } catch (NumberFormatException e) {
                throw new ParseException(i18n.tr("Invalid value. Enter valid number"), 0);
            }
        }
    }
}
