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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.forms.client.ui.CComboBox;

public class ValueLabelCombo extends CComboBox<ValueLabelWrapper> {

    public ValueLabelCombo(String label, Integer... options) {
        List<ValueLabelWrapper> wrappedOptions = new ArrayList<ValueLabelWrapper>(options.length);
        for (Integer value : options) {
            wrappedOptions.add(new ValueLabelWrapper(value, label));
        }
        setOptions(wrappedOptions);
    }
}