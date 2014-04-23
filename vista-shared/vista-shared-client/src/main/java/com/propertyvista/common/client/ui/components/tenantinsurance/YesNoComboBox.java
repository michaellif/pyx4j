/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.tenantinsurance;

import java.util.Arrays;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.i18n.shared.I18n;

public class YesNoComboBox extends CComboBox<Boolean> {

    private static final I18n i18n = I18n.get(YesNoComboBox.class);

    public YesNoComboBox() {
        super(null, new IFormatter<Boolean>() {
            @Override
            public String format(Boolean value) {
                if (value == null) {
                    return i18n.tr("Unknown");
                } else {
                    return value == true ? i18n.tr("Yes") : i18n.tr("No");
                }
            }

        });
        setOptions(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
    }

    @Override
    public boolean isValuesEquals(Boolean value1, Boolean value2) {
        if (value1 == null | value2 == null) {
            return value1 == value2;
        } else {
            return value1.equals(value2);
        }
    }

}
