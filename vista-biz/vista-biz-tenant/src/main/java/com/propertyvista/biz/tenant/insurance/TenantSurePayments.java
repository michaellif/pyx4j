/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.lease.Tenant;

class TenantSurePayments {

    private static String tenantSureMerchantTerminalId() {
        if (VistaDeployment.isVistaProduction()) {
            //TODO
            throw new Error("Not Implemented");
        } else {
            return "BIRCHWT6";
        }

    }

    static InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return ServerSideFactory.create(PaymentMethodFacade.class).retrieveInsurancePaymentMethod(tenantId);
    }

    static InsurancePaymentMethod updatePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        return ServerSideFactory.create(PaymentMethodFacade.class).persistInsurancePaymentMethod(tenantSureMerchantTerminalId(), paymentMethod, tenantId);
    }

    static InsuranceTenantSureTransaction preAuthorization(InsuranceTenantSureTransaction transaction) {
        BigDecimal amount = transaction.amount().getValue();
        String referenceNumber = transaction.id().getStringView();
        String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).authorization(amount, tenantSureMerchantTerminalId(), referenceNumber,
                (CreditCardInfo) transaction.paymentMethod().details().cast());
        transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
        transaction.transactionDate().setValue(Persistence.service().getTransactionSystemTime());
        return transaction;
    }

    public static void compleateTransaction(InsuranceTenantSureTransaction transaction) {
        // TODO Auto-generated method stub

    }
}
