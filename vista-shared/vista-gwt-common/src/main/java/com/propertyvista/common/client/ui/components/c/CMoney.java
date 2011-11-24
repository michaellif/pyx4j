/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import com.pyx4j.forms.client.ui.CFocusComponent;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.domain.financial.Money;

public class CMoney extends CFocusComponent<Money, NativeMoney> {

    private IFormat<Money> format;

    private boolean showCurrency = true;

    public CMoney() {
        this(null);
    }

    public CMoney(String title) {
        this(title, true);
    }

    public CMoney(String title, boolean showCurrency) {
        super(title);
        setShowCurrency(showCurrency);
        setFormat(new MoneyFormatter());
    }

    public void setShowCurrency(boolean showCurrency) {
        this.showCurrency = showCurrency;
    }

    public boolean isShowCurrency() {
        return showCurrency;
    }

    @Override
    protected NativeMoney createWidget() {
        NativeMoney w = new NativeMoney(this);
        return w;
    }

    public void setFormat(IFormat<Money> format) {
        this.format = format;
    }

    public IFormat<Money> getFormat() {
        return format;
    }

    @Override
    public void onEditingStop() {
        super.onEditingStop();
        if (isValid()) {
            setNativeValue(getValue());
        }
    }
}
