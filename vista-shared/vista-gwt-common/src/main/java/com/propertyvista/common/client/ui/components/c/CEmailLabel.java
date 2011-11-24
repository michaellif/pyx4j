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
package com.propertyvista.common.client.ui.components.c;


import com.pyx4j.forms.client.ui.CAbstractLabel;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.domain.contact.Email;

public class CEmailLabel extends CAbstractLabel<Email> {

    public CEmailLabel() {
        super();
        setEmailFormat(null);
    }

    public CEmailLabel(String title) {
        super(title);
        setEmailFormat(null);
    }

    public void setEmailFormat(IFormat<Email> format) {
        setFormat(format != null ? format : new EmailFormatter());
    }
}
