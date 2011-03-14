/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.validators;

import java.util.Date;

import com.pyx4j.commons.TimeUtils;

public class ValidationUtils {

    public static boolean isOlderThen18(final Date bithday) {
        return TimeUtils.isOlderThen(bithday, 18);
//        if (bithday == null) {
//            return false;
//        } else {
//            Date now = new Date();
//            @SuppressWarnings("deprecation")
//            Date y18 = TimeUtils.createDate(now.getYear() - 18, now.getMonth(), now.getDay());
//            return bithday.before(y18);
//        }
    }

}
