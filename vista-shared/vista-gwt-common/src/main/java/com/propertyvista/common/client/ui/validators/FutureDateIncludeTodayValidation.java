/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;

public class FutureDateIncludeTodayValidation {

    public FutureDateIncludeTodayValidation(CComponent<LogicalDate> value) {
        value.addValueValidator(new FutureDateIncludeTodayValidator());
    }

    public FutureDateIncludeTodayValidation(CComponent<LogicalDate> value, final String message) {
        value.addValueValidator(new FutureDateIncludeTodayValidator(message));
    }
}
