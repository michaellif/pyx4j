/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 14, 2014
 * @author VladL
 */
package com.propertyvista.common.client.ui.validators;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.security.client.ClientContext;

public class ClientBusinessRules {

    public static boolean needPreviousAddress(LogicalDate currentAddressMoveInDate, int yearsToForcingPreviousAddress) {
        if (currentAddressMoveInDate == null) {
            return false;
        }
        if (yearsToForcingPreviousAddress == 0) {
            return true;
        }

        Date now = ClientContext.getServerDate();
        @SuppressWarnings("deprecation")
        LogicalDate needPreviousAddress = new LogicalDate(now.getYear() - yearsToForcingPreviousAddress, now.getMonth(), now.getDate());
        return needPreviousAddress.before(currentAddressMoveInDate);
    }
}
