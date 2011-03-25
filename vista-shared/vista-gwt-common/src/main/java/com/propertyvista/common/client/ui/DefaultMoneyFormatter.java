/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui;

import com.google.gwt.i18n.client.NumberFormat;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.util.DomainUtil;

import com.pyx4j.forms.client.ui.IFormat;

public class DefaultMoneyFormatter implements IFormat<Money> {

    public static enum ShowCurrency {
        hide, show, use$
    }

    private final ShowCurrency showCurrency;

    private final NumberFormat numberFormat;

    public DefaultMoneyFormatter() {
        this((String) null);
    }

    public DefaultMoneyFormatter(String format) {
        this(format, ShowCurrency.hide);
    }

    public DefaultMoneyFormatter(ShowCurrency showCurrency) {
        this(null, showCurrency);
    }

    public DefaultMoneyFormatter(String format, ShowCurrency showCurrency) {
        numberFormat = NumberFormat.getFormat(format != null ? format : "#0.00");
        this.showCurrency = showCurrency;
    }

    @Override
    public String format(Money value) {
        String currency = null;
        switch (showCurrency) {
        case use$:
            currency = "$";
            break;
        case show:
            currency = (value != null && !value.currency().isNull() ? value.currency().getStringView() : "");
            break;
        case hide:
            currency = "";
            break;
        }

        String amount = (value != null && !value.amount().isNull() ? numberFormat.format(value.amount().getValue()) : "");
        return (showCurrency == ShowCurrency.use$ ? currency + amount : amount + " " + currency);
    }

    @Override
    public Money parse(String string) {
        try {
            string.replaceAll("\\s", "");
            string.replaceAll("\\D", "");
            return DomainUtil.createMoney(Double.valueOf(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}