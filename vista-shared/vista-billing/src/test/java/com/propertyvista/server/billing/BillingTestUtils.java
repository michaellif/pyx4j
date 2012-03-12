/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.pyx4j.commons.LogicalDate;

public class BillingTestUtils {

    public static LogicalDate getDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
            return new LogicalDate(formatter.parse(date));
        } catch (ParseException e) {
            throw new Error(e);
        }
    }
}
