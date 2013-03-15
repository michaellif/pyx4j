/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingext;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.ExternalTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.internal.BillTester;
import com.propertyvista.domain.financial.billing.Bill;

public class ExternalBillingTest extends ExternalTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenatio() {
        setSysDate("15-Sep-2011");
        createLease("01-Oct-2011", "31-Aug-2012");

//        approveApplication(false);
        activateLease();

        postExternalCharge("1000.00", "charge 1", "10-Sep-2011", "1-Oct-2011");
        postExternalCharge("100.00", "charge 2", "15-Sep-2011", "1-Oct-2011");
        postExternalCharge("-200.00", "refund 1", "20-Sep-2011", "1-Nov-2011");

        postExternalPayment("1000.00", "payment", "E-Check");

        setSysDate("17-Sep-2011");
        Bill bill = runExternalBilling();

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.External).
        billingPeriodStartDate("1-Oct-2011").
        billingPeriodEndDate("31-Oct-2011").
        numOfProductCharges(3).
        recurringFeatureCharges("900.00").
        taxes("0.00").
        totalDueAmount("-100.00");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }
}
