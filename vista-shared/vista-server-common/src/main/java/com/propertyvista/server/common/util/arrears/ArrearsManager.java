/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.arrears;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsState;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Payment;

public interface ArrearsManager {

    void processBill(Bill bill);

    void processPayment(Payment payment);

    ArrearsState arrears(Key tenant, LogicalDate asOf);
}
