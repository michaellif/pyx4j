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

import com.propertyvista.common.client.ui.DefaultMoneyFormatter.ShowCurrency;
import com.propertyvista.common.domain.Money;

import com.pyx4j.forms.client.ui.CAbstractLabel;

public class CMoneyLabel extends CAbstractLabel<Money> {

    public CMoneyLabel() {
        super();
        setMoneyFormat(null, ShowCurrency.use$);
    }

    public CMoneyLabel(String title) {
        super(title);
        setMoneyFormat(null, ShowCurrency.use$);
    }

    public void setMoneyFormat(String format, ShowCurrency showCurrency) {
        setFormat(new DefaultMoneyFormatter(format, showCurrency));
    }
}
