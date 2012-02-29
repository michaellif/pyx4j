/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingPeriodsTest extends BillingTestBase {

    private String billingCycleId;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");

    public void testSequentialBillingRunWithGlobalBillingPeriodStartDate() throws ParseException {
        preloadData();
        createAgreement(new LogicalDate(formatter.parse("23-Mar-2011")), new LogicalDate(formatter.parse("3-Aug-2011")), 1);

        //==================== RUN 1 ======================//

        Bill bill = runBilling(true);

        assertEquals("Bill Sequence Number", 1, (int) bill.billSequenceNumber().getValue());
        assertTrue("Previous Bill Sequence number", bill.previousBill().billSequenceNumber().isNull());

        assertEquals("Billing Cycle Period Start Day", 1, (int) bill.billingRun().billingCycle().billingPeriodStartDay().getValue());
        assertEquals("Billing Cycle Period Run Target Day", 15, (int) bill.billingRun().billingCycle().billingRunTargetDay().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-Mar-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-Mar-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-Mar-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("31-Mar-2011")), bill.billingPeriodEndDate().getValue());

        String billingCycleId = bill.billingRun().billingCycle().id().toString();

        //==================== RUN 2 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 2, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 1, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-Apr-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("30-Apr-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-Apr-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("30-Apr-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 3 ======================//

        bill = runBilling(false);

        assertEquals("Bill Sequence Number", 3, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 2, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-May-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-May-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-May-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("31-May-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 4, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 2, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-May-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-May-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-May-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("31-May-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 4 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 5, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 4, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-June-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("30-June-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-June-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("30-June-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 5 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 6, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 5, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-July-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-July-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-Jul-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("31-July-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 7, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 6, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-Aug-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-Aug-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-Aug-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("3-Aug-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        try {
            bill = runBilling(true);
            assertTrue("Billing outside billing lease dates", false);
        } catch (Error e) {
        }

    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay() throws ParseException {
        preloadData();
        createAgreement(new LogicalDate(formatter.parse("23-Mar-2011")), new LogicalDate(formatter.parse("3-Aug-2011")), null);

        //==================== RUN 1 ======================//

        Bill bill = runBilling(true);

        assertEquals("Bill Sequence Number", 1, (int) bill.billSequenceNumber().getValue());
        assertTrue("Previous Bill Sequence number", bill.previousBill().billSequenceNumber().isNull());

        assertEquals("Billing Cycle Period Start Day", 23, (int) bill.billingRun().billingCycle().billingPeriodStartDay().getValue());
        assertEquals("Billing Cycle Period Run Target Day", 9, (int) bill.billingRun().billingCycle().billingRunTargetDay().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("23-Mar-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("22-Apr-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-Mar-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("22-Apr-2011")), bill.billingPeriodEndDate().getValue());
        String billingCycleId = bill.billingRun().billingCycle().id().toString();

        //==================== RUN 2 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 2, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 1, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("23-Apr-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("22-May-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-Apr-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("22-May-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 3 ======================//

        bill = runBilling(false);

        assertEquals("Bill Sequence Number", 3, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 2, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("23-May-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("22-Jun-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-May-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("22-Jun-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 4, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 2, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("23-May-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("22-Jun-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-May-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("22-Jun-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 4 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 5, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 4, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("23-Jun-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("22-Jul-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-Jun-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("22-Jul-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 5 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 6, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 5, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("23-Jul-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("22-Aug-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("23-Jul-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("3-Aug-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        try {
            bill = runBilling(true);
            assertTrue("Billing outside billing lease dates", false);
        } catch (Error e) {
        }
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDayOn29() throws ParseException {
        testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(29);
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDayOn30() throws ParseException {
        testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(30);
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDayOn31() throws ParseException {
        testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(31);
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(int day) throws ParseException {
        preloadData();
        createAgreement(new LogicalDate(formatter.parse(day + "-Mar-2011")), new LogicalDate(formatter.parse("3-Aug-2011")), null);

        //==================== RUN 1 ======================//

        Bill bill = runBilling(true);

        assertEquals("Bill Sequence Number", 1, (int) bill.billSequenceNumber().getValue());
        assertTrue("Previous Bill Sequence number", bill.previousBill().billSequenceNumber().isNull());

        assertEquals("Billing Cycle Period Start Day", 1, (int) bill.billingRun().billingCycle().billingPeriodStartDay().getValue());
        assertEquals("Billing Cycle Period Run Target Day", 15, (int) bill.billingRun().billingCycle().billingRunTargetDay().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-Mar-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-Mar-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse(day + "-Mar-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("31-Mar-2011")), bill.billingPeriodEndDate().getValue());
        String billingCycleId = bill.billingRun().billingCycle().id().toString();

        //==================== RUN 2 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 2, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 1, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("1-Apr-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("30-Apr-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("1-Apr-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("30-Apr-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 3 ======================//

        bill = runBilling(true);

        //==================== RUN 4 ======================//

        bill = runBilling(true);

        //==================== RUN 5 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 5, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 4, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("01-Jul-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-Jul-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("01-Jul-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("31-Jul-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        bill = runBilling(true);

        assertEquals("Bill Sequence Number", 6, (int) bill.billSequenceNumber().getValue());
        assertEquals("Previous Bill Sequence number", 5, (int) bill.previousBill().billSequenceNumber().getValue());

        assertEquals("Billing Run Start Day", new LogicalDate(formatter.parse("01-Aug-2011")), bill.billingRun().billingPeriodStartDate().getValue());
        assertEquals("Billing Run End Day", new LogicalDate(formatter.parse("31-Aug-2011")), bill.billingRun().billingPeriodEndDate().getValue());

        assertEquals("Bill Start Day", new LogicalDate(formatter.parse("01-Aug-2011")), bill.billingPeriodStartDate().getValue());
        assertEquals("Bill End Day", new LogicalDate(formatter.parse("3-Aug-2011")), bill.billingPeriodEndDate().getValue());

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 7 ======================//

        try {
            bill = runBilling(true);
            assertTrue("Billing outside billing lease dates", false);
        } catch (Error e) {
        }
    }

    private void createAgreement(LogicalDate leaseDateFrom, LogicalDate leaseDateTo, Integer billingPeriodStartDate) {
        Lease lease = leaseDataModel.getLease();

        lease.leaseFrom().setValue(leaseDateFrom);
        lease.leaseTo().setValue(leaseDateTo);

        lease.leaseFinancial().billingPeriodStartDate().setValue(billingPeriodStartDate);

        Persistence.service().persist(lease);

    }

    private Bill runBilling(boolean confirm) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLease().getPrimaryKey());
        BillingFacade.runBilling(lease);

        Bill bill = BillingFacade.getLatestBill(lease.leaseFinancial().billingAccount());
        if (confirm) {
            BillingFacade.confirmBill(bill);
        } else {
            BillingFacade.rejectBill(bill);
        }

        Persistence.service().retrieve(bill.charges());
        Persistence.service().retrieve(bill.chargeAdjustments());
        Persistence.service().retrieve(bill.leaseAdjustments());

        DataDump.dump("bill", bill);
        DataDump.dump("lease", lease);

        assertEquals("Billing Cycle Payment Frequency", PaymentFrequency.Monthly, bill.billingRun().billingCycle().paymentFrequency().getValue());

        if (billingCycleId == null) {
            billingCycleId = bill.billingRun().billingCycle().id().getValue().toString();
        } else {
            assertEquals("Billing Cycle Id", billingCycleId, bill.billingRun().billingCycle().id().getValue().toString());
        }
        assertEquals("Bill Confirmation Status", confirm ? BillStatus.Confirmed : BillStatus.Rejected, bill.billStatus().getValue());

        return bill;
    }
}
