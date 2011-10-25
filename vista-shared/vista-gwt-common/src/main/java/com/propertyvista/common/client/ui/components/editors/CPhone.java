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
package com.propertyvista.common.client.ui.components.editors;


import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.Phone;

public class CPhone extends CEditableComponent<Phone, NativePhone> {

    protected static I18n i18n = I18n.get(CPhone.class);

    private IFormat<Phone> format;

    private boolean showType;

    private boolean showExtention;

    public CPhone() {
        this(null);
    }

    public CPhone(String title) {
        this(title, false, true);
    }

    public CPhone(String title, boolean showType, boolean showExtention) {
        super(title);
        setShowType(showType);
        setShowExtention(showExtention);
        setFormat(new PhoneFormatter());
    }

    public void setShowType(boolean showType) {
        this.showType = showType;
    }

    public boolean isShowType() {
        return showType;
    }

    public boolean isShowExtention() {
        return showExtention;
    }

    public void setShowExtention(boolean showExtention) {
        this.showExtention = showExtention;
    }

    @Override
    protected NativePhone createWidget() {
        NativePhone w = new NativePhone(this);
        return w;
    }

    public void setFormat(IFormat<Phone> format) {
        this.format = format;
    }

    public IFormat<Phone> getFormat() {
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
