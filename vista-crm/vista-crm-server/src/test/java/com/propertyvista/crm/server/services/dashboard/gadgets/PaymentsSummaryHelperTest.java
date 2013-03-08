/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import static com.pyx4j.gwt.server.DateUtils.detectDateformat;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.server.common.util.PaymentsSummaryHelper;

public class PaymentsSummaryHelperTest extends PaymentsSummaryHelperTestBase {

    public void testCalculateSummary() {
        // initialize
        makePaymentRecord(merchantAccountA, "01-May-2010", "50.00", PaymentType.Cash, PaymentStatus.Received);
        makePaymentRecord(merchantAccountA, "01-May-2010", "200.00", PaymentType.Cash, PaymentStatus.Received);

        makePaymentRecord(merchantAccountA, "01-May-2010", "50.00", PaymentType.CreditCard, PaymentStatus.Received);
        makePaymentRecord(merchantAccountA, "01-May-2010", "100.00", PaymentType.CreditCard, PaymentStatus.Received);
        makePaymentRecord(merchantAccountA, "01-May-2010", "50.00", PaymentType.CreditCard, PaymentStatus.Received);

        makePaymentRecord(merchantAccountA, "01-May-2010", "100.00", PaymentType.Echeck, PaymentStatus.Received);

        // add some clutter
        makePaymentRecord(merchantAccountA, "01-May-2010", "100.00", PaymentType.Cash, PaymentStatus.Canceled);
        makePaymentRecord(merchantAccountA, "01-May-2010", "200.00", PaymentType.Echeck, PaymentStatus.Rejected);

        makePaymentRecord(merchantAccountB, "01-May-2010", "200.00", PaymentType.Echeck, PaymentStatus.Rejected);
        makePaymentRecord(merchantAccountB, "01-May-2010", "200.00", PaymentType.CreditCard, PaymentStatus.Received);
        makePaymentRecord(merchantAccountB, "01-May-2010", "200.00", PaymentType.CreditCard, PaymentStatus.Rejected);

        SystemDateManager.setDate(new LogicalDate(detectDateformat("01-Aug-2010")));

        // test        
        {
            PaymentsSummary summary = new PaymentsSummaryHelper().calculateSummary(merchantAccountA, PaymentStatus.Received, new LogicalDate(
                    detectDateformat("01-May-2010")));

            assertEquals(PaymentStatus.Received, summary.status().getValue());
            assertEquals(new LogicalDate(detectDateformat("01-May-2010")), summary.snapshotDay().getValue());

            assertEquals(new BigDecimal("250.00"), summary.cash().getValue());
            assertEquals(new BigDecimal("0.00"), summary.check().getValue());
            assertEquals(new BigDecimal("100.00"), summary.eCheck().getValue());
            assertEquals(new BigDecimal("0.00"), summary.eft().getValue());
            assertEquals(new BigDecimal("200.00"), summary.cc().getValue());
            assertEquals(new BigDecimal("0.00"), summary.interac().getValue());
        }

        {
            PaymentsSummary summary = new PaymentsSummaryHelper().calculateSummary(merchantAccountA, PaymentStatus.Received, new LogicalDate(
                    detectDateformat("02-May-2010")));

            assertEquals(PaymentStatus.Received, summary.status().getValue());
            assertEquals(new LogicalDate(detectDateformat("02-May-2010")), summary.snapshotDay().getValue());

            assertEquals(new BigDecimal("0.00"), summary.cash().getValue());
            assertEquals(new BigDecimal("0.00"), summary.check().getValue());
            assertEquals(new BigDecimal("0.00"), summary.eCheck().getValue());
            assertEquals(new BigDecimal("0.00"), summary.eft().getValue());
            assertEquals(new BigDecimal("0.00"), summary.cc().getValue());
            assertEquals(new BigDecimal("0.00"), summary.interac().getValue());
        }

    }

}
