/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 9, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;

public class LatePaymentUtils {

    public static BigDecimal latePayment(BigDecimal amount, LogicalDate dueDate, LogicalDate receivedDate) {
        //TODO YS to get detailes of types and rules for late payment fee
        return amount.multiply(new BigDecimal("0.05"));
    }

}
