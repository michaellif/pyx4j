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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.i18n.shared.I18n;

public class CMoneyPercentCombo extends CTextFieldBase<IMoneyPercentAmount, NTextBox<IMoneyPercentAmount>> {

    static final I18n i18n = I18n.get(CMoneyPercentCombo.class);

    private final NumberFormat nf = NumberFormat.getFormat(i18n.tr("#,##0.00"));

    private final NumberFormat mf = NumberFormat.getFormat(i18n.tr("$#,##0.00"));

    private final NumberFormat pf = NumberFormat.getFormat(i18n.tr("#0.00%"));

    private ValueType amountType;

    public CMoneyPercentCombo() {
        setFormatter(new MoneyPercentFormat());
        setParser(new MoneyPercentParser());
        setNativeComponent(new NTextBox<IMoneyPercentAmount>(this));
    }

    @Override
    public boolean isValueEmpty() {
        return getValue() == null || getValue().isNull() || ( //
                BigDecimal.ZERO.compareTo(getValue().amount().getValue(BigDecimal.ZERO)) >= 0 && //
                BigDecimal.ZERO.compareTo(getValue().percent().getValue(BigDecimal.ZERO)) >= 0 //
                );
    }

    public void setAmountType(ValueType type) {
        amountType = type;
        setEditorValue(getValue());
    }

    @Override
    protected IMoneyPercentAmount preprocessValue(IMoneyPercentAmount value, boolean fireEvent, boolean populate) {
        if (value != null) {
            value = value.duplicate();
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    private void clearValue() {
        IMoneyPercentAmount value = getValue();
        if (value != null) {
            value.percent().setValue(BigDecimal.ZERO);
            value.amount().setValue(BigDecimal.ZERO);
        }
    }

    class MoneyPercentFormat implements IFormatter<IMoneyPercentAmount, String> {

        @Override
        public String format(IMoneyPercentAmount value) {
            if (value == null) {
                return nf.format(BigDecimal.ZERO);
            }
            String result = null;
            switch (amountType) {
            case Monetary:
                if (isEditable()) {
                    result = nf.format(value.amount().getValue(BigDecimal.ZERO));
                } else {
                    result = mf.format(value.amount().getValue(BigDecimal.ZERO));
                }
                break;
            case Percentage:
                if (isEditable()) {
                    result = nf.format(value.percent().getValue(BigDecimal.ZERO).multiply(new BigDecimal("100")));
                } else {
                    result = pf.format(value.percent().getValue(BigDecimal.ZERO));
                }
                break;
            }
            return result;
        }
    }

    class MoneyPercentParser implements IParser<IMoneyPercentAmount> {

        @Override
        public IMoneyPercentAmount parse(String string) throws ParseException {
            IMoneyPercentAmount value = getValue();
            if (CommonsStringUtils.isEmpty(string)) {
                clearValue();
                return value;
            }
            try {
                BigDecimal amount = new BigDecimal(nf.parse(string)).setScale(2, RoundingMode.HALF_UP);
                if (ValueType.Monetary.equals(amountType)) {
                    value.amount().setValue(amount);
                    value.percent().setValue(null);
                } else {
                    value.percent().setValue(amount.divide(new BigDecimal("100")));
                    value.amount().setValue(null);
                }
                return value;
            } catch (NumberFormatException e) {
                throw new ParseException(i18n.tr("Invalid value. Enter valid number"), 0);
            }
        }
    }
}
