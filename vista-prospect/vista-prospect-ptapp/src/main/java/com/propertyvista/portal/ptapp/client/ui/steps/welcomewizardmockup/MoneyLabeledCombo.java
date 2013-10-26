/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.NotImplementedException;

public class MoneyLabeledCombo extends CComboBox<BigDecimal> {

    public MoneyLabeledCombo(final String label, BigDecimal... options) {
        super(null, null, new IFormat<BigDecimal>() {

            @Override
            public String format(BigDecimal value) {
                if (value != null) {
                    return Utils.formatMoney(value) + " " + label;
                } else {
                    return "";
                }
            }

            @Override
            public BigDecimal parse(String string) throws ParseException {
                throw new NotImplementedException();
            }
        });
        setOptions(Arrays.asList(options));
    }

}
