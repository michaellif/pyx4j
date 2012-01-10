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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;

import com.propertyvista.domain.financial.Money;

public class CMoney extends CTextFieldBase<Money, NativeMoney> {

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
        addValueValidator(new TextBoxParserValidator<Money>());
    }

    public void setShowCurrency(boolean showCurrency) {
        this.showCurrency = showCurrency;
    }

    public boolean isShowCurrency() {
        return showCurrency;
    }

    @Override
    protected NativeMoney createWidget() {
        return new NativeMoney(this);
    }

    @Override
    public void setFormat(IFormat<Money> format) {
        this.format = format;
    }

    @Override
    public IFormat<Money> getFormat() {
        return format;
    }

    @Override
    public void onEditingStop() {
        super.onEditingStop();
//        if (isValid()) {
//            setNativeValue(getValue());
//        }
    }

    @Override
    public boolean isValueEmpty() {
        if (isWidgetCreated()) {
            if (!CommonsStringUtils.isEmpty(getWidget().getNativeText())) {
                return false;
            }
        }
        return super.isValueEmpty() || getValue().isNull();
    }

// TODO: not sure where it's better to set default currency: here or in more high level editors...
//    
//    @Override
//    public void populate(Money value) {
//        if (value != null && value.currency().isEmpty()) {
//            value.currency().set(EntityFactory.create(Currency.class));
//            value.currency().name().setValue("CAD");
//        }
//        super.populate(value);
//    }
}
