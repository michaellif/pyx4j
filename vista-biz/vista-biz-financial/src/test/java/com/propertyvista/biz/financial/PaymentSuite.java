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

import com.propertyvista.biz.financial.payment.PadPaymentAmountValidationTest;
import com.propertyvista.biz.financial.payment.PadPaymentChargeBaseSunnyDayScenarioTest;
import com.propertyvista.biz.financial.payment.PadPaymentMethodCancellationTest;
import com.propertyvista.biz.financial.payment.PadTransactionUtilsTest;
import com.propertyvista.biz.financial.payment.PaymentMethodPersistenceEcheckTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PaymentMethodPersistenceEcheckTest.class, PadTransactionUtilsTest.class, PadPaymentChargeBaseSunnyDayScenarioTest.class,
        PadPaymentMethodCancellationTest.class, PadPaymentAmountValidationTest.class })
public class PaymentSuite {

}
