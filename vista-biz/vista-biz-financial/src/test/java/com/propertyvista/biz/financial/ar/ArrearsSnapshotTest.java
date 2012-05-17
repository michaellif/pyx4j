/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import static com.propertyvista.biz.financial.SysDateManager.setSysDate;

import org.junit.Ignore;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.tenant.lease.Lease.Status;

@Ignore
public class ArrearsSnapshotTest extends ArrearsSnapshotTestBase {

    public void testLeaseScenario() {
        // SET UP
        // tax is 12%
        setLeaseConditions("23-Mar-2011", "03-Aug-2011", 1); // lease $930 
        addParking(); // parking $80

        // BILLING RUN 1
        setSysDate("18-Mar-2011");
        setLeaseStatus(Status.Approved);
        runAndConfirmBilling();

        updateArrearsHistory();
        beginAssertArrearsSnapshot("18-Mar-2011");
        // TODO these values were copied from ARSunnyDayScenarioTest: I have no idea how they were calculated (part the lease month?), need to ask Michael  
        assertArrearsDebitType(DebitType.lease, "302.50", "0.00", "0.00", "0.00", "0.00");
        assertArrearsDebitType(DebitType.parking, "26.02", "0.00", "0.00", "0.00", "0.00");
        assertArrearsDebitType(DebitType.deposit, "930.30", "0.00", "0.00", "0.00", "0.00"); // TODO how deposit is calulcated???
        assertArrearsTotal("1258.82", "0.00", "0.00", "0.00", "0.00");

        // arrears are not supposed to change until the due date
        setSysDate("19-Mar-2011");
        updateArrearsHistory();

        assertPrevArrearsSnapshot("18-Mar-2011", "19-Mar-2011");

        setSysDate("22-Mar-2011");
        updateArrearsHistory();
        assertPrevArrearsSnapshot("18-Mar-2011", "22-Mar-2011");

        // the first due date has come, but the payment hasn't been received: 
        // we expect the current bucket to upgrade to the next level
        // and of course all the previous to remain the same
        setSysDate("23-Mar-2011");
        updateArrearsHistory();

        assertPrevArrearsSnapshot("18-Mar-2011", "22-Mar-2011");

        beginAssertArrearsSnapshot("23-Mar-2011");
        assertArrearsDebitType(DebitType.lease, "0.00", "302.50", "0.00", "0.00", "0.00");
        assertArrearsDebitType(DebitType.parking, "0.00", "26.02", "0.00", "0.00", "0.00");
        assertArrearsDebitType(DebitType.deposit, "0.00", "930.30", "0.00", "0.00", "0.00");
        assertArrearsTotal("0.00", "1258.82", "0.00", "0.00", "0.00");

        // here also nothing is supposed to change
        setSysDate("27-Mar-2011");
        updateArrearsHistory();

        assertPrevArrearsSnapshot("26-Mar-2011", "27-Mar-2011");

        // BILLING RUN 2        
        setSysDate("28-Mar-2011");
        runAndConfirmBilling();

        setSysDate("01-Apr-2011");
        updateArrearsHistory();

        beginAssertArrearsSnapshot("01-Apr-2011");
        assertArrearsDebitType(DebitType.lease, "1041.60", "302.50", "0.00", "0.00", "0.00");
        assertArrearsDebitType(DebitType.parking, "89.60", "26.02", "0.00", "0.00", "0.00");
        assertArrearsDebitType(DebitType.deposit, "0.00", "930.30", "0.00", "0.00", "0.00");
        assertArrearsTotal("1131.20", "1258.82", "0.00", "0.00", "0.0");

    }
}
