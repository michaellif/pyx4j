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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.Email;

public class CEmail extends CFocusComponent<Email, NativeEmail> {

    protected static I18n i18n = I18n.get(CEmail.class);

    private IFormat<Email> format;

    private boolean showType;

    public CEmail() {
        this(null);
    }

    public CEmail(String title) {
        this(title, false);
    }

    public CEmail(String title, boolean showType) {
        super(title);
        setShowType(showType);
        setFormat(new EmailFormatter());
    }

    public void setShowType(boolean showType) {
        this.showType = showType;
    }

    public boolean isShowType() {
        return showType;
    }

    @Override
    protected NativeEmail createWidget() {
        NativeEmail w = new NativeEmail(this);
        return w;
    }

    public void setFormat(IFormat<Email> format) {
        this.format = format;
    }

    public IFormat<Email> getFormat() {
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
