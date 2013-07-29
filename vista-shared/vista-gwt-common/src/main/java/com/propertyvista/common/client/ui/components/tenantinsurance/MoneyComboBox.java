/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.tenantinsurance;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

public class MoneyComboBox extends CComboBox<BigDecimal> {

    private static final I18n i18n = I18n.get(MoneyComboBox.class);

    public static final NumberFormat CANADIAN_CURRENCY_DETAILED_FORMAT = NumberFormat.getFormat(i18n.tr("CAD #,##0.00"));

    public static final NumberFormat CANADIAN_CURRENCY_FORMAT = NumberFormat.getFormat(i18n.tr("CAD #,##0"));

    public static class MoneyComboBoxFormat implements IFormat<BigDecimal> {
        @Override
        public String format(BigDecimal value) {
            if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
                return i18n.tr("None");
            } else {
                return CANADIAN_CURRENCY_FORMAT.format(value);
            }
        }

        @Override
        public BigDecimal parse(String string) throws ParseException {
            throw new Error("this should never happen");
        }
    }

    public MoneyComboBox() {
        super(null, null, new MoneyComboBoxFormat());
    }

    @Override
    public boolean isValuesEquals(BigDecimal value1, BigDecimal value2) {
        if (value1 == null | value2 == null) {
            return value1 == value2;
        } else {
            return value1.equals(value2);
        }
    }

}
