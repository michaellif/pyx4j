/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.components;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

public class CurrentBalanceFormat implements IFormat<BigDecimal> {

    private static final I18n i18n = I18n.get(CurrentBalanceFormat.class);

    @Override
    public String format(BigDecimal money) {
        return NumberFormat.getFormat(i18n.tr("$#,##0.00")).format(money.abs()) + (money.compareTo(BigDecimal.ZERO) < 0 ? " CR" : "");
    }

    @Override
    public BigDecimal parse(String string) throws ParseException {
        // TODO this is not used
        return null;
    }

}
