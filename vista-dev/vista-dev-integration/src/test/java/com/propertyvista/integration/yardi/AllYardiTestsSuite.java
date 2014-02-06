/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ //
//
        PaymentPostingCreditCardYardiTest.class,//
        PaymentPostingCheckYardiTest.class,//
        PaymentBatchSingleBuildingCreditCardYardiTest.class, //
        PaymentBatchSingleBuildingEcheckYardiTest.class, //
        PaymentBatchThreeBuildingYardiTest.class, //
        PreauthorizedPaymentChangeReviewYardiTest.class, //
        PreauthorizedPaymentProcessYardiTest.class, //
        PreauthorizedPaymentRenewTest.class, //
        YardiLeaseChargesTest.class, //
        YardiLeaseImportTest.class, //
        YardiLeaseImportVersionsTest.class, //
        YardiLeaseLifecycleTest.class, //
        YardiLeasePriceEstimatorTest.class, //
        YardiUnitTransferTest.class //
})
public class AllYardiTestsSuite {

}
