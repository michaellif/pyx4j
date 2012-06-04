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

import org.junit.Ignore;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;

@Ignore
public class PaymentsSummaryHelperTest extends PaymentsSummaryHelperTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        lease = EntityFactory.create(Lease.class);
        ServerSideFactory.create(LeaseFacade.class);

        // TODO add merchant account

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Persistence.service().setTransactionSystemTime(null);
    }

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
        makePaymentRecord(merchantAccountB, "01-May-2010", "200.00", PaymentType.CreditCard, PaymentStatus.Rejected);

        Persistence.service().setTransactionSystemTime(new LogicalDate(detectDateformat("01-Aug-2010")));

        // test        
        {
            PaymentsSummary summary = new PaymentsSummaryHelper().calculateSummary(merchantAccountA, PaymentStatus.Received, new LogicalDate(
                    detectDateformat("01-May-2010")));

            assertEquals(PaymentStatus.Received, summary.status().getValue());
            assertEquals(new LogicalDate(detectDateformat("01-Aug-2010")), summary.timestamp().getValue());

            assertEquals(new BigDecimal("250.00"), summary.cash().getValue());
            assertEquals(new BigDecimal("0.00"), summary.cheque().getValue());
            assertEquals(new BigDecimal("100.00"), summary.eCheque().getValue());
            assertEquals(new BigDecimal("0.00"), summary.eft().getValue());
            assertEquals(new BigDecimal("200.00"), summary.cc().getValue());
            assertEquals(new BigDecimal("0.00"), summary.interac().getValue());
        }

        {
            PaymentsSummary summary = new PaymentsSummaryHelper().calculateSummary(merchantAccountA, PaymentStatus.Received, new LogicalDate(
                    detectDateformat("02-May-2010")));

            assertEquals(PaymentStatus.Received, summary.status().getValue());
            assertEquals(new LogicalDate(detectDateformat("02-Aug-2010")), summary.timestamp().getValue());

            assertEquals(new BigDecimal("0.00"), summary.cash().getValue());
            assertEquals(new BigDecimal("0.00"), summary.cheque().getValue());
            assertEquals(new BigDecimal("0.00"), summary.eCheque().getValue());
            assertEquals(new BigDecimal("0.00"), summary.eft().getValue());
            assertEquals(new BigDecimal("0.00"), summary.cc().getValue());
            assertEquals(new BigDecimal("0.00"), summary.interac().getValue());
        }

    }

}
