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

    private long startTime;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("Execution Time - " + (System.currentTimeMillis() - startTime) + "ms");
        super.tearDown();
    }

    public void testLeaseScenario() {
        setLeaseConditions("23-Mar-2011", "03-Aug-2011", 1);
        addParking();
        addPet();

        // RUN 1
        setSysDate("18-Mar-2011");
        setLeaseStatus(Status.Approved);
        runAndConfirmBilling();

        updateArrearsHistory();
        beginAssertArrearsSnapshot("18-Mar-2011");
        assertArrearsDebitType(DebitType.lease, "302.50", "0.0", "0.0", "0.0", "0.0");
        assertArrearsDebitType(DebitType.parking, "302.50", "0.0", "0.0", "0.0", "0.0");
        assertArrearsDebitType(DebitType.pet, "302.50", "0.0", "0.0", "0.0", "0.0");
        assertArrearsTotal("302.50", "0.0", "0.0", "0.0", "0.0");

        setSysDate("30-Mar-2011");
        updateArrearsHistory();

        assertPrevArrearsSnapshot("18-Mar-2001");
        assertPrevArrearsSnapshot("19-Mar-2001");
        assertPrevArrearsSnapshot("20-Mar-2001");
        assertPrevArrearsSnapshot("30-Mar-2001");

        setSysDate("1-Apr-2011");
        assertPrevArrearsSnapshot("31-Mar-2001");
        beginAssertArrearsSnapshot("1-Apr-2011");
        assertArrearsDebitType(DebitType.lease, "0.0", "302.50", "0.0", "0.0", "0.0");
        assertArrearsDebitType(DebitType.parking, "0.0", "302.50", "0.0", "0.0", "0.0");
        assertArrearsDebitType(DebitType.pet, "0.0", "302.50", "0.0", "0.0", "0.0");
        assertArrearsTotal("302.50", "0.0", "0.0", "0.0", "0.0");

        setSysDate("15-Apr-2011");
        updateArrearsHistory();
        assertPrevArrearsSnapshot("10-Apr-2011");
        assertPrevArrearsSnapshot("15-Apr-2011");

        // RUN 2        
        setSysDate("18-Apr-2011");
        runAndConfirmBilling();

        updateArrearsHistory();
        // TODO assert current bucket must be updated

        setSysDate("1-May-2011");

    }
}
