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
package com.propertyvista.common.client.ui.components;

import com.pyx4j.forms.client.ui.CAbstractLabel;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.domain.contact.Phone;

public class CPhoneLabel extends CAbstractLabel<Phone> {

    public CPhoneLabel() {
        super();
        setPhoneFormat(null);
    }

    public CPhoneLabel(String title) {
        super(title);
        setPhoneFormat(null);
    }

    public void setPhoneFormat(IFormat<Phone> format) {
        setFormat(format != null ? format : new LabelPhoneFormatter());
    }

    class LabelPhoneFormatter extends PhoneFormatter {

        @Override
        public String format(Phone value) {
            return super.format(value) + (value.extension().isNull() ? "" : " ex." + value.extension().getValue().toString());
        }
    }
}
