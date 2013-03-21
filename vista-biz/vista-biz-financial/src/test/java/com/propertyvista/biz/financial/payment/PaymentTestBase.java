/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;

import junit.framework.Assert;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.MerchantAccountDataModel;

public class PaymentTestBase extends FinancialTestBase {

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = super.getMockModelTypes();
        models.add(MerchantAccountDataModel.class);
        return models;
    }

    public void assertRpcTransientMemebers(List<LeasePaymentMethod> methods) {
        for (LeasePaymentMethod method : methods) {
            assertRpcTransientMemebers(method);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void assertRpcTransientMemebers(LeasePaymentMethod paymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo ec = paymentMethod.details().cast();
            Assert.assertNull(ec.accountNo().number().getValue());
            break;
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            Assert.assertNull(cc.card().number().getValue());
            Assert.assertNull(cc.securityCode().getValue());
            break;
        }
    }

}
