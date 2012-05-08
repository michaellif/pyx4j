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
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.IFormat;

public class FormattableCombo<E> extends CComboBox<E> {

    private final IFormat<E> format;

    public FormattableCombo(IFormat<E> format) {
        this.format = format;
    }

    @Override
    public String getItemName(E o) {
        return format.format(o);
    };

}
