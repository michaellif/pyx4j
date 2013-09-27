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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.financial.payment.CreditCardTransactionResponse;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.server.jobs.TaskRunner;

class TenantSurePayments {

    private static final Logger log = LoggerFactory.getLogger(TenantSurePayments.class);

    private static final I18n i18n = I18n.get(TenantSurePayments.class);

    private static final String EXECUTION_MONITOR_SECTION_NAME = "TenantSurePreauthorizedPayment";

    private static String tenantSureMerchantTerminalId() {
        return ServerSideFactory.create(Vista2PmcFacade.class).getTenantSureMerchantTerminalId();
    }

    static InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return ServerSideFactory.create(PaymentMethodFacade.class).retrieveInsurancePaymentMethod(tenantId);
    }

    static InsurancePaymentMethod savePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        return ServerSideFactory.create(PaymentMethodFacade.class).persistInsurancePaymentMethod(paymentMethod, tenantId);
    }

    /**
     * Day of payment, The actual payment for days that are not present in given month will happen at the end of month.
     * 
     * @param inceptionDate
     * @return 1-31
     * 
     * @see InsuranceTenantSureTransaction#paymentDue
     */
    static int calulatePaymentDay(LogicalDate inceptionDate) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(inceptionDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    static LogicalDate getNextPaymentDate(TenantSureInsurancePolicy insuranceTenantSure) {
        // Get last transaction
        EntityQueryCriteria<InsuranceTenantSureTransaction> criteria = EntityQueryCriteria.create(InsuranceTenantSureTransaction.class);
        criteria.eq(criteria.proto().insurance(), insuranceTenantSure);
        criteria.eq(criteria.proto().status(), InsuranceTenantSureTransaction.TransactionStatus.Cleared);
        criteria.desc(criteria.proto().paymentDue());

        InsuranceTenantSureTransaction transaction = Persistence.service().retrieve(criteria);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(transaction.paymentDue().getValue());

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int paymentDay = insuranceTenantSure.paymentDay().getValue();
        if (paymentDay > daysInMonth) {
            paymentDay = daysInMonth;
        }
        cal.set(Calendar.DAY_OF_MONTH, paymentDay);

        return new LogicalDate(cal.getTime());
    }

    static InsuranceTenantSureTransaction preAuthorization(InsuranceTenantSureTransaction transaction) {
        BigDecimal amount = transaction.amount().getValue();
        String referenceNumber = transaction.id().getStringView();
        String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).preAuthorization(amount, tenantSureMerchantTerminalId(),
                ReferenceNumberPrefix.TenantSure, referenceNumber, (CreditCardInfo) transaction.paymentMethod().details().cast());
        transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
        transaction.transactionDate().setValue(SystemDateManager.getDate());
        return transaction;
    }

    static void preAuthorizationReversal(InsuranceTenantSureTransaction transaction) {
        String referenceNumber = transaction.id().getStringView();
        ServerSideFactory.create(CreditCardFacade.class).preAuthorizationReversal(tenantSureMerchantTerminalId(), ReferenceNumberPrefix.TenantSure,
                referenceNumber, (CreditCardInfo) transaction.paymentMethod().details().cast());
    }

    static void compleateTransaction(InsuranceTenantSureTransaction transaction) {
        BigDecimal amount = transaction.amount().getValue();
        String referenceNumber = transaction.id().getStringView();
        String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).completion(amount, tenantSureMerchantTerminalId(),
                ReferenceNumberPrefix.TenantSure, referenceNumber, (CreditCardInfo) transaction.paymentMethod().details().cast());
        transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
        transaction.transactionDate().setValue(SystemDateManager.getDate());
    }

    public static void performOutstandingPayment(final TenantSureInsurancePolicy insuranceTenantSure) {
        final LogicalDate dueDate = getNextPaymentDate(insuranceTenantSure);
        InsuranceTenantSureTransaction transaction = TaskRunner.runAutonomousTransation(new Callable<InsuranceTenantSureTransaction>() {
            @Override
            public InsuranceTenantSureTransaction call() {
                return makePaymentTransaction(insuranceTenantSure, dueDate);
            }
        });
        if (transaction.status().getValue() != InsuranceTenantSureTransaction.TransactionStatus.Cleared) {
            throw new UserRuntimeException(i18n.tr("Credit Card payment failed"));
        }
    }

    static void processPayments(ExecutionMonitor executionMonitor, final LogicalDate dueDate) {
        Collection<Integer> paymentDays = new ArrayList<Integer>();
        // Calculate all payment days for end of month
        {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(dueDate);
            int paymentDay = cal.get(Calendar.DAY_OF_MONTH);
            paymentDays.add(paymentDay);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (paymentDay == daysInMonth) {
                // Last day of the month -> process all the days not present in this month
                for (int dayOfMonth = daysInMonth + 1; dayOfMonth <= 31; dayOfMonth++) {
                    paymentDays.add(dayOfMonth);
                }
            }

        }

        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        OrCriterion or = criteria.or();
        or.right().eq(criteria.proto().expiryDate(), dueDate);
        or.left().isNull(criteria.proto().expiryDate());
        criteria.eq(criteria.proto().status(), TenantSureStatus.Active);
        criteria.in(criteria.proto().paymentDay(), paymentDays);
        ICursorIterator<TenantSureInsurancePolicy> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                final TenantSureInsurancePolicy insuranceTenantSure = iterator.next();
                if (!isPaymentMadeForPaymentDue(insuranceTenantSure, dueDate)) {

                    InsuranceTenantSureTransaction transaction = TaskRunner.runAutonomousTransation(new Callable<InsuranceTenantSureTransaction>() {
                        @Override
                        public InsuranceTenantSureTransaction call() {
                            return makePaymentTransaction(insuranceTenantSure, dueDate);
                        }
                    });

                    if (transaction.status().getValue() == InsuranceTenantSureTransaction.TransactionStatus.Cleared) {
                        executionMonitor.addProcessedEvent(//@formatter:off
                                EXECUTION_MONITOR_SECTION_NAME,
                                transaction.amount().getValue(),
                                SimpleMessageFormat.format("PreAuthorized payment for insurance certificate {0} was cleared", insuranceTenantSure.certificate().insuranceCertificateNumber().getValue())
                        );//@formatter:on
                    } else if (transaction.status().getValue() == InsuranceTenantSureTransaction.TransactionStatus.PaymentRejected) {
                        executionMonitor.addFailedEvent(//@formatter:off
                                EXECUTION_MONITOR_SECTION_NAME,
                                transaction.amount().getValue(),
                                SimpleMessageFormat.format("PreAuthorized payment for insurance certificate {0} was rejected", insuranceTenantSure.certificate().insuranceCertificateNumber().getValue())
                        );//@formatter:on
                    } else {
                        executionMonitor.addErredEvent(//@formatter:off
                                EXECUTION_MONITOR_SECTION_NAME,
                                transaction.amount().getValue(),
                                SimpleMessageFormat.format("PreAuthorized payment for insurance certificate {0} is neither cleared nor rejected", insuranceTenantSure.certificate().insuranceCertificateNumber().getValue())
                        );//@formatter:on
                    }

                }
            }
        } finally {
            iterator.close();
        }
    }

    static boolean isPaymentMadeForPaymentDue(TenantSureInsurancePolicy insuranceTenantSure, LogicalDate dueDate) {
        // Get last transaction
        EntityQueryCriteria<InsuranceTenantSureTransaction> criteria = EntityQueryCriteria.create(InsuranceTenantSureTransaction.class);
        criteria.eq(criteria.proto().insurance(), insuranceTenantSure);
        criteria.eq(criteria.proto().status(), InsuranceTenantSureTransaction.TransactionStatus.Cleared);
        criteria.eq(criteria.proto().paymentDue(), dueDate);

        return Persistence.service().exists(criteria);
    }

    private static int getMonth(LogicalDate date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    static BigDecimal getMonthlyPayable(TenantSureInsurancePolicy insuranceTenantSure, LogicalDate dueDate) {
        if (getMonth(insuranceTenantSure.inceptionDate().getValue()) == getMonth(dueDate)) {
            return insuranceTenantSure.totalAnniversaryFirstMonthPayable().getValue();
        } else {
            return insuranceTenantSure.totalMonthlyPayable().getValue();
        }
    }

    static InsuranceTenantSureTransaction makePaymentTransaction(TenantSureInsurancePolicy insuranceTenantSure, LogicalDate dueDate) {
        InsuranceTenantSureTransaction transaction = EntityFactory.create(InsuranceTenantSureTransaction.class);
        transaction.insurance().set(insuranceTenantSure);
        transaction.paymentMethod().set(getPaymentMethod(insuranceTenantSure.client().tenant()));
        transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Draft);
        transaction.amount().setValue(getMonthlyPayable(insuranceTenantSure, dueDate));

        transaction.paymentDue().setValue(dueDate);
        Persistence.service().persist(transaction);

        Persistence.service().commit();

        transaction.transactionDate().setValue(SystemDateManager.getDate());

        try {
            String referenceNumber = transaction.id().getStringView();

            CreditCardTransactionResponse response = ServerSideFactory.create(CreditCardFacade.class).realTimeSale(transaction.amount().getValue(),
                    tenantSureMerchantTerminalId(), ReferenceNumberPrefix.TenantSure, referenceNumber,
                    (CreditCardInfo) transaction.paymentMethod().details().cast());

            if (response.success().getValue()) {
                transaction.transactionAuthorizationNumber().setValue(response.authorizationNumber().getValue());
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Cleared);
            } else if (ServerSideFactory.create(CreditCardFacade.class).isNetworkError(response.code().getValue())) {
                transaction.transactionAuthorizationNumber().setValue(response.code().getValue());
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.PaymentError);
            } else {
                transaction.transactionAuthorizationNumber().setValue(response.code().getValue());
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.PaymentRejected);
            }

        } catch (Throwable e) {
            log.error("TenantSure payment error", e);
            transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.PaymentError);
        }

        Persistence.service().persist(transaction);
        Persistence.service().commit();

        if (transaction.status().getValue() == InsuranceTenantSureTransaction.TransactionStatus.PaymentRejected) {
            ServerSideFactory.create(TenantSureFacade.class).startCancellationDueToSkippedPayment(insuranceTenantSure.client().tenant());
        }

        return transaction;
    }

}
