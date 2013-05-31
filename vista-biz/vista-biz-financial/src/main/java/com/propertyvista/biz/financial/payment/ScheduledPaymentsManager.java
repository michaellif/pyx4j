/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.property.yardi.YardiPropertyConfiguration;

class ScheduledPaymentsManager {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPaymentsManager.class);

    void processScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.in(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentType));
        criteria.add(PropertyCriterion.le(criteria.proto().targetDate(), SystemDateManager.getDate()));

        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                processScheduledPayment(paymentRecordIterator.next(), executionMonitor);
            }
        } finally {
            paymentRecordIterator.close();
        }
    }

    private void processScheduledPayment(final PaymentRecord paymentRecord, final ExecutionMonitor executionMonitor) {
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, PaymentException>() {

                @Override
                public Void execute() throws PaymentException {
                    if (paymentRecord.amount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                        ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
                        executionMonitor.addProcessedEvent("Canceled");
                    } else if (!PaymentUtils.isElectronicPaymentsSetup(paymentRecord.billingAccount())) {
                        ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
                        executionMonitor.addProcessedEvent("Canceled ElectronicPayments Not Setup");
                    } else {
                        PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
                        if (processedPaymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                            executionMonitor.addFailedEvent("Rejected", processedPaymentRecord.amount().getValue(),
                                    SimpleMessageFormat.format("Payment {0} was rejected", paymentRecord.id()));
                        } else {
                            executionMonitor.addProcessedEvent("Processed", processedPaymentRecord.amount().getValue());
                        }
                    }
                    return null;
                }
            });
        } catch (PaymentException e) {
            log.error("ScheduledPayment {} processing failed", paymentRecord, e);
            executionMonitor.addErredEvent("Erred", paymentRecord.amount().getValue(), SimpleMessageFormat.format("PaymentRecord {0} ", paymentRecord.id()), e);
        }
    }

    void cancelScheduledPayments(LeasePaymentMethod paymentMethod) {
        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
        criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
        criteria.in(criteria.proto().paymentStatus(), PaymentStatus.Scheduled, PaymentStatus.PendingAction);

        for (PaymentRecord paymentRecord : Persistence.service().query(criteria)) {
            ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
        }
    }

    void cancelScheduledPayments(PreauthorizedPayment preauthorizedPayment) {
        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
        criteria.eq(criteria.proto().preauthorizedPayment(), preauthorizedPayment);
        criteria.in(criteria.proto().paymentStatus(), PaymentStatus.Scheduled, PaymentStatus.PendingAction);

        for (PaymentRecord paymentRecord : Persistence.service().query(criteria)) {
            ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
        }
    }

    void verifyYardiPaymentIntegration(ExecutionMonitor executionMonitor, LogicalDate forDate) {

        Map<String, LogicalDate> arDatesByPropertyCode = new HashMap<String, LogicalDate>();
        YardiARFacade yardiARFacade = ServerSideFactory.create(YardiARFacade.class);

        List<YardiPropertyConfiguration> configs;
        try {
            configs = yardiARFacade.getPropertyConfigurations();
        } catch (RemoteException e) {
            throw new Error(e);
        } catch (YardiServiceException e) {
            throw new Error(e);
        }

        for (YardiPropertyConfiguration propertyConfiguration : configs) {
            LogicalDate accountsReceivableDate = new LogicalDate(DateUtils.detectDateformat(propertyConfiguration.accountsReceivable().getValue()
                    .replace("/", "/01/")));
            arDatesByPropertyCode.put(propertyConfiguration.propertyID().getValue(), accountsReceivableDate);
        }

        ICursorIterator<BillingCycle> billingCycleIterator;
        {//TODO->Closure
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.eq(criteria.proto().targetPadExecutionDate(), forDate);
            billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingCycleIterator.hasNext()) {

                BillingCycle billingCycle = billingCycleIterator.next();
                if (!PaymentUtils.isElectronicPaymentsSetup(billingCycle.building())) {
                    continue;
                }
                Persistence.ensureRetrieve(billingCycle.building(), AttachLevel.Attached);

                LogicalDate arDate = arDatesByPropertyCode.get(billingCycle.building().propertyCode().getValue());

                if ((arDate == null) || (!isSameMonth(arDate, billingCycle.targetPadExecutionDate().getValue()))) {
                    executionMonitor.addErredEvent("accountsReceivable", "Unexpected Accounts Receivable Post date " + arDate + //
                            "; Property " + billingCycle.building().propertyCode().getValue());
                }

            }
        } finally {
            billingCycleIterator.close();
        }

    }

    static boolean isSameMonth(LogicalDate d1, LogicalDate d2) {
        if (d1 == null || d2 == null) {
            return false;
        } else {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(d1);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(d2);

            return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
        }
    }
}
