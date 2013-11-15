/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.domain.pmc.payment.CustomerCreditCheckTransaction;
import com.propertyvista.server.jobs.TaskRunner;

class ScreeningPayments {

    private static final I18n i18n = I18n.get(ScreeningPayments.class);

    private static final Logger log = LoggerFactory.getLogger(ScreeningPayments.class);

    private static String merchantTerminalId() {
        return ServerSideFactory.create(Vista2PmcFacade.class).getVistaMerchantTerminalId();
    }

    static Key preAuthorization(PmcEquifaxInfo equifaxInfo) {
        final CustomerCreditCheckTransaction transaction = EntityFactory.create(CustomerCreditCheckTransaction.class);
        transaction.status().setValue(CustomerCreditCheckTransaction.TransactionStatus.Draft);
        transaction.transactionDate().setValue(SystemDateManager.getDate());
        transaction.pmc().set(VistaDeployment.getCurrentPmc());

        AbstractEquifaxFee fee = ServerSideFactory.create(Vista2PmcFacade.class).getEquifaxFee();
        switch (equifaxInfo.reportType().getValue()) {
        case FullCreditReport:
            transaction.amount().setValue(fee.fullCreditReportPerApplicantFee().getValue());
            break;
        case RecomendationReport:
            transaction.amount().setValue(fee.recommendationReportPerApplicantFee().getValue());
            break;
        default:
            throw new IllegalArgumentException();
        }

        if (transaction.amount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        transaction.paymentMethod().set(equifaxInfo.paymentMethod());
        if (equifaxInfo.paymentMethod().isNull()) {
            throw new UserRuntimeException(i18n.tr("Credit Card is not setup for Credit Check payments"));
        }

        TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(transaction);
                Persistence.service().commit();
                Persistence.service().retrieve(transaction.paymentMethod());
                return null;
            }
        });

        // Do authorization
        try {
            String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).preAuthorization(merchantTerminalId(),
                    transaction.amount().getValue(), ReferenceNumberPrefix.EquifaxScreening, transaction.getPrimaryKey().toString(),
                    (CreditCardInfo) transaction.paymentMethod().details().cast());

            transaction.status().setValue(CustomerCreditCheckTransaction.TransactionStatus.Authorized);
            transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
            transaction.transactionDate().setValue(SystemDateManager.getDate());

        } catch (Throwable e) {
            log.error("Error", e);
            transaction.status().setValue(CustomerCreditCheckTransaction.TransactionStatus.Rejected);
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed"));
            }
        } finally {

            TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
                @Override
                public Void call() {
                    Persistence.service().persist(transaction);
                    Persistence.service().commit();
                    return null;
                }
            });
        }

        return transaction.getPrimaryKey();
    }

    static void preAuthorizationReversal(final Key transactionId) {
        final CustomerCreditCheckTransaction transaction = TaskRunner.runInOperationsNamespace(new Callable<CustomerCreditCheckTransaction>() {
            @Override
            public CustomerCreditCheckTransaction call() {
                CustomerCreditCheckTransaction transaction = Persistence.service().retrieve(CustomerCreditCheckTransaction.class, transactionId);
                Persistence.service().retrieve(transaction.paymentMethod());
                return transaction;
            }
        });

        try {
            ServerSideFactory.create(CreditCardFacade.class).preAuthorizationReversal(merchantTerminalId(), ReferenceNumberPrefix.EquifaxScreening,
                    transaction.getPrimaryKey().toString(), (CreditCardInfo) transaction.paymentMethod().details().cast());
            transaction.transactionDate().setValue(SystemDateManager.getDate());
            transaction.status().setValue(CustomerCreditCheckTransaction.TransactionStatus.Reversal);

            TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
                @Override
                public Void call() {
                    Persistence.service().persist(transaction);
                    Persistence.service().commit();
                    return null;
                }
            });

        } catch (Throwable e) {
            log.error("Error", e);
        }
    }

    static void compleateTransaction(final Key transactionId) {
        final CustomerCreditCheckTransaction transaction = TaskRunner.runInOperationsNamespace(new Callable<CustomerCreditCheckTransaction>() {
            @Override
            public CustomerCreditCheckTransaction call() {
                CustomerCreditCheckTransaction transaction = Persistence.service().retrieve(CustomerCreditCheckTransaction.class, transactionId);
                Persistence.service().retrieve(transaction.paymentMethod());
                return transaction;
            }
        });

        try {
            String authorizationNumber = ServerSideFactory.create(CreditCardFacade.class).completion(merchantTerminalId(), transaction.amount().getValue(),
                    ReferenceNumberPrefix.EquifaxScreening, transaction.getPrimaryKey().toString(),
                    (CreditCardInfo) transaction.paymentMethod().details().cast());
            transaction.transactionAuthorizationNumber().setValue(authorizationNumber);
            transaction.transactionDate().setValue(SystemDateManager.getDate());
            transaction.status().setValue(CustomerCreditCheckTransaction.TransactionStatus.Cleared);
        } catch (Throwable e) {
            log.error("Error", e);
            transaction.status().setValue(CustomerCreditCheckTransaction.TransactionStatus.PaymentRejected);
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card payment failed"));
            }
        } finally {

            TaskRunner.runAutonomousTransation(VistaNamespace.operationsNamespace, new Callable<Void>() {
                @Override
                public Void call() {
                    Persistence.service().persist(transaction);
                    Persistence.service().commit();
                    return null;
                }
            });
        }
    }
}
