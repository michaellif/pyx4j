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
package com.propertyvista.common.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.propertyvista.common.client.ui.validators.DefaultMoneyValidator;
import com.propertyvista.portal.domain.Money;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class CMoney extends CEditableComponent<Money, NativeMoney> {

    private static I18n i18n = I18nFactory.getI18n(CMoney.class);

    private IFormat<Money> format;

    private EditableValueValidator<Money> validator;

    private boolean showCurrency = true;

    public CMoney() {
        super();
        setDefaultValidation();
    }

    public CMoney(boolean showCurrency) {
        super();
        setDefaultValidation();
        setShowCurrency(showCurrency);
    }

    public CMoney(String title) {
        super(title);
        setDefaultValidation();
    }

    public CMoney(String title, boolean showCurrency) {
        super(title);
        setDefaultValidation();
        setShowCurrency(showCurrency);
    }

    public void setShowCurrency(boolean showCurrency) {
        this.showCurrency = showCurrency;
    }

    public boolean isShowCurrency() {
        return showCurrency;
    }

    private void setDefaultValidation() {
        setFormat(new DefaultMoneyFormatter());
        validator = new DefaultMoneyValidator();
        addValueValidator(validator);
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
