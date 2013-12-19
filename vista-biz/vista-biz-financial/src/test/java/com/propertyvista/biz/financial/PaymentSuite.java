/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.propertyvista.biz.financial.payment.CreditCardPaymentTest;
import com.propertyvista.biz.financial.payment.FundsTransferProcessErrorRecoveryTest;
import com.propertyvista.biz.financial.payment.PadPaymentChargeBaseSunnyDayScenarioTest;
import com.propertyvista.biz.financial.payment.PadPaymentFixedAmountValidationTest;
import com.propertyvista.biz.financial.payment.PadPaymentMethodCancellationTest;
import com.propertyvista.biz.financial.payment.PadPaymentPercentAmountValidationTest;
import com.propertyvista.biz.financial.payment.PadProcessingTest;
import com.propertyvista.biz.financial.payment.PadTransactionUtilsTest;
import com.propertyvista.biz.financial.payment.PaymentAcceptanceUtilsTest;
import com.propertyvista.biz.financial.payment.PaymentMethodPersistenceCardsTest;
import com.propertyvista.biz.financial.payment.PaymentMethodPersistenceEcheckTest;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentChangeReviewInternalTest;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentCyclesTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PaymentMethodPersistenceEcheckTest.class, //
        CreditCardPaymentTest.class, //
        PaymentMethodPersistenceCardsTest.class, //
        PadTransactionUtilsTest.class, //
        PaymentAcceptanceUtilsTest.class, //
        PadPaymentChargeBaseSunnyDayScenarioTest.class, //
        PadPaymentMethodCancellationTest.class, //
        PadPaymentPercentAmountValidationTest.class, //
        PadPaymentFixedAmountValidationTest.class, //
        PadProcessingTest.class, //
        FundsTransferProcessErrorRecoveryTest.class, //
        PadTransactionUtilsTest.class, //
        PreauthorizedPaymentChangeReviewInternalTest.class, //
        PreauthorizedPaymentCyclesTest.class })
public class PaymentSuite {

}
