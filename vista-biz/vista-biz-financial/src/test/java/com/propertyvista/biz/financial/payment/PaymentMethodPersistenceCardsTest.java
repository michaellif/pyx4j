/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.test.mock.MockConfig;

public class PaymentMethodPersistenceCardsTest extends PaymentMethodPersistenceTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        VistaSystemsSimulationConfig.getConfiguration().useCardServiceSimulator().setValue(Boolean.FALSE);
    }

    @Override
    protected void tearDown() throws Exception {
        VistaSystemsSimulationConfig.getConfiguration().useCardServiceSimulator().setValue(Boolean.TRUE);
        super.tearDown();
    }

    @Override
    protected MockConfig createMockConfig() {
        MockConfig config = super.createMockConfig();
        config.useCaledonMerchantAccounts = true;
        return config;
    }

    public void testPersistPaymentMethodCreditCard() throws PaymentException {
        testPersistPaymentMethod(PaymentType.CreditCard);
    }

    public void testUpdatePaymentMethodCreditCard() throws PaymentException {
        testUpdatePaymentMethod(PaymentType.CreditCard);
    }

}
