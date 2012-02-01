/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import com.propertyvista.payment.PaymentRequest;

public class ReconsoliationTransactions {

    public static void main(String[] args) {

        PaymentRequest request;

        request = CaledonRealTimeTest.createRequest(TestData.CARD_MC1, "2015-01", 1000.0);
        request.referenceNumber().setValue("REC01");
        CaledonRealTimeTest.assertRealTimeSale(CaledonRealTimeTest.testMerchant, request, "0000");

        request = CaledonRealTimeTest.createRequest(TestData.CARD_MC1, "2015-01", 1200.0);
        request.referenceNumber().setValue("REC02");
        CaledonRealTimeTest.assertRealTimeSale(CaledonRealTimeTest.testMerchant, request, "0000");
    }

}
