/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.misc;

import java.util.Date;

import com.pyx4j.commons.TimeUtils;

public class BusinessRules {

    public static boolean infoPageNeedPreviousAddress(java.sql.Date currentAddressMoveInDate) {
        if (currentAddressMoveInDate == null) {
            return false;
        }
        Date now = TimeUtils.today();
        @SuppressWarnings("deprecation")
        Date needPreviousAddress = TimeUtils.createDate(now.getYear() - 3, now.getMonth(), now.getDate());
        return needPreviousAddress.before(currentAddressMoveInDate);
    }

}
