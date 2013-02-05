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
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.lease.Tenant;

class TenantSurePayments {

    private static String tenantSureMerchantTerminalId() {
        return ServerSideFactory.create(Vista2PmcFacade.class).getTenantSureMerchantTerminalId();
    }

    static InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return ServerSideFactory.create(PaymentMethodFacade.class).retrieveInsurancePaymentMethod(tenantId);
    }

    static InsurancePaymentMethod updatePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        return ServerSideFactory.create(PaymentMethodFacade.class).persistInsurancePaymentMethod(paymentMethod, tenantId);
    }

    static int calulatePaymentDay(LogicalDate inceptionDate) {
        return 1;
    }

    static LogicalDate getNextPaymentDate(InsuranceTenantSure insuranceTenantSure) {
        // Get last transaction
        EntityQueryCriteria<InsuranceTenantSureTransaction> criteria = EntityQueryCriteria.create(InsuranceTenantSureTransaction.class);
        criteria.eq(criteria.proto().insurance(), insuranceTenantSure);
        criteria.eq(criteria.proto().status(), InsuranceTenantSureTransaction.TransactionStatus.Cleared);
        criteria.desc(criteria.proto().transactionDate());

        InsuranceTenantSureTransaction transaction = Persistence.service().retrieve(criteria);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(transaction.transactionDate().getValue());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.MONTH, 1);

        return new LogicalDate(cal.getTime());
    }

    static InsuranceTenantSureTransaction preAuthorization(InsuranceTenantSureTransaction transaction) {
        BigDecimal amount = transaction.amount().getValue();
        String referenceNumber = transaction.id().getStringView();
        String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).authorization(amount, tenantSureMerchantTerminalId(),
                ReferenceNumberPrefix.TenantSure, referenceNumber, (CreditCardInfo) transaction.paymentMethod().details().cast());
        transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
        transaction.transactionDate().setValue(Persistence.service().getTransactionSystemTime());
        return transaction;
    }

    static void preAuthorizationReversal(InsuranceTenantSureTransaction transaction) {
        String referenceNumber = transaction.id().getStringView();
        ServerSideFactory.create(CreditCardFacade.class).authorizationReversal(tenantSureMerchantTerminalId(), ReferenceNumberPrefix.TenantSure,
                referenceNumber, (CreditCardInfo) transaction.paymentMethod().details().cast());
    }

    public static void compleateTransaction(InsuranceTenantSureTransaction transaction) {
        BigDecimal amount = transaction.amount().getValue();
        String referenceNumber = transaction.id().getStringView();
        String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).completion(amount, tenantSureMerchantTerminalId(),
                ReferenceNumberPrefix.TenantSure, referenceNumber, (CreditCardInfo) transaction.paymentMethod().details().cast());
        transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
        transaction.transactionDate().setValue(Persistence.service().getTransactionSystemTime());
    }
}
