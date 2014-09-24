/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 24, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util;

import java.math.BigDecimal;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;

public class CCurrencyMoneyLabel extends CMoneyLabel {

    public CCurrencyMoneyLabel() {
        setFormatter(new MoneyFormat());
    }

    public CCurrencyMoneyLabel(String prefix) {
        setFormatter(new MoneyFormat(prefix));
    }

    public CCurrencyMoneyLabel(String prefix, String suffix) {
        setFormatter(new MoneyFormat(prefix, suffix));
    }

    public static class MoneyFormat implements IFormatter<BigDecimal, String> {

        static final I18n i18n = I18n.get(MoneyFormat.class);

        final NumberFormat nf;

        private String prefix = i18n.tr("$");

        private String suffix = "";

        @I18nContext(javaFormatFlag = true)
        public MoneyFormat() {
            nf = NumberFormat.getFormat(i18n.tr("#,##0.00"));
        }

        public MoneyFormat(String prefix) {
            this();
            this.prefix = prefix;
        }

        public MoneyFormat(String prefix, String suffix) {
            this();
            this.prefix = prefix;
            this.suffix = suffix;
        }

        @Override
        public String format(BigDecimal value) {
            if (value == null) {
                return "";
            } else {
                return prefix + nf.format(value) + suffix;
            }
        }
    }
}
