/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.util.DomainUtil;

class PreauthorisedPaymentsManager {

    void createPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate) {
        ICursorIterator<BillingCycle> billingCycleIterator;
        {//TODO->Closure
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.eq(criteria.proto().targetPadGenerationDate(), runDate);
            criteria.isNull(criteria.proto().actualPadGenerationDate());
            billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingCycleIterator.hasNext()) {
                createBillingCyclePreauthorisedPayments(billingCycleIterator.next(), executionMonitor);
            }
        } finally {
            billingCycleIterator.completeRetrieval();
        }
    }

    void updateScheduledPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate) {
        ICursorIterator<BillingCycle> billingCycleIterator;
        {//TODO->Closure
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.lt(criteria.proto().targetPadGenerationDate(), runDate);
            criteria.gt(criteria.proto().targetPadExecutionDate(), runDate);
            criteria.isNull(criteria.proto().actualPadGenerationDate());
            billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingCycleIterator.hasNext()) {
                updateBillingCyclePreauthorisedPayments(billingCycleIterator.next(), executionMonitor);
            }
        } finally {
            billingCycleIterator.completeRetrieval();
        }
    }

    private void createBillingCyclePreauthorisedPayments(final BillingCycle billingCycle, final ExecutionMonitor executionMonitor) {

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {

                ICursorIterator<BillingAccount> billingAccountIterator;
                { //TODO->Closure
                    EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
                    // TODO verify with Misha
                    criteria.eq(criteria.proto().billingType(), billingCycle.billingType());
                    criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
                    billingAccountIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
                }
                try {
                    while (billingAccountIterator.hasNext()) {
                        createBillingAccountPreauthorisedPayments(billingCycle, billingAccountIterator.next(), executionMonitor);
                    }
                } finally {
                    billingAccountIterator.completeRetrieval();
                }

                billingCycle.actualPadGenerationDate().setValue(new LogicalDate(SystemDateManager.getDate()));
                Persistence.service().persist(billingCycle);

                return null;
            }
        });
    }

    private void createBillingAccountPreauthorisedPayments(BillingCycle billingCycle, BillingAccount billingAccount, ExecutionMonitor executionMonitor) {
        // Validate that PAD was not created for this account
        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().padBillingCycle(), billingCycle);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            if (Persistence.service().count(criteria) != 0) {
                throw new Error(SimpleMessageFormat.format("Pad already created for {} {}", billingAccount, billingCycle));
            }
        }

        BigDecimal currentBalance = null;

        Persistence.service().retrieve(billingAccount.lease());
        Lease lease = billingAccount.lease();
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        // Calculate Percentage with rounding, Save data in PaymentRecord
        List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal percentAmountTotal = BigDecimal.ZERO;

        for (LeaseTermTenant leaseParticipant : lease.currentTerm().version().tenants()) {
            Persistence.service().retrieveMember(leaseParticipant.leaseParticipant().preauthorizedPayments());
            for (PreauthorizedPayment pap : leaseParticipant.leaseParticipant().preauthorizedPayments()) {

                Validate.isTrue(PaymentType.schedulable().contains(pap.paymentMethod().type().getValue()));

                BigDecimal amount;

                PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
                paymentRecord.leaseTermParticipant().set(leaseParticipant);
                paymentRecord.paymentMethod().set(pap.paymentMethod());

                switch (pap.amountType().getValue()) {
                case Percent:
                    if (currentBalance == null) {
                        // Lazy currentBalance initialization
                        currentBalance = ServerSideFactory.create(ARFacade.class).getPADBalance(billingAccount, billingCycle);
                    }
                    percentTotal = percentTotal.add(pap.amount().getValue());
                    amount = DomainUtil.roundMoney(currentBalance.multiply(pap.amount().getValue()));
                    percentAmountTotal = percentAmountTotal.add(amount);
                    break;
                case Value:
                    amount = pap.amount().getValue();
                    break;
                default:
                    throw new IllegalArgumentException();
                }
                paymentRecord.amount().setValue(amount);
                paymentRecords.add(paymentRecord);
            }
        }

        // Percent rounding case of total 100%  e.g. 33% + 66%
        if (percentTotal.compareTo(BigDecimal.ONE) == 0) {
            BigDecimal unapidBalance = currentBalance.subtract(percentAmountTotal);
            // Make the fist to  pay fractions
            paymentRecords.get(0).amount().setValue(paymentRecords.get(0).amount().getValue().add(unapidBalance));
        }

        for (PaymentRecord paymentRecord : paymentRecords) {
            schedulePreAuthorizedPaymentRecord(billingCycle, billingAccount, paymentRecord);
            executionMonitor.addProcessedEvent(paymentRecord.paymentMethod().type().getStringView(), paymentRecord.amount().getValue());
        }

    }

    private void schedulePreAuthorizedPaymentRecord(BillingCycle billingCycle, BillingAccount billingAccount, PaymentRecord paymentRecord) {
        paymentRecord.padBillingCycle().set(billingCycle);
        paymentRecord.billingAccount().set(billingAccount);
        paymentRecord.targetDate().setValue(billingCycle.targetPadExecutionDate().getValue());
        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        ServerSideFactory.create(PaymentFacade.class).schedulePayment(paymentRecord);
    }

    private void updateBillingCyclePreauthorisedPayments(final BillingCycle billingCycle, final ExecutionMonitor executionMonitor) {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                ICursorIterator<BillingAccount> billingAccountIterator;
                { //TODO->Closure
                    EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
                    // TODO verify with Misha
                    criteria.eq(criteria.proto().billingType(), billingCycle.billingType());
                    criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
                    billingAccountIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
                }
                try {
                    while (billingAccountIterator.hasNext()) {
                        updateBillingAccountPreauthorisedPayments(billingCycle, billingAccountIterator.next(), executionMonitor);
                    }
                } finally {
                    billingAccountIterator.completeRetrieval();
                }
                return null;
            }
        });
    }

    private void updateBillingAccountPreauthorisedPayments(BillingCycle billingCycle, BillingAccount billingAccount, ExecutionMonitor executionMonitor) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().padBillingCycle(), billingCycle);
        criteria.eq(criteria.proto().billingAccount(), billingAccount);
        List<PaymentRecord> paymentRecords = Persistence.service().query(criteria);
        if (paymentRecords.size() == 0) {
            //Nothing to update
            return;
        }

        BigDecimal currentBalance = null;

        Persistence.service().retrieve(billingAccount.lease());
        Lease lease = billingAccount.lease();
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal percentAmountTotal = BigDecimal.ZERO;

        for (LeaseTermTenant leaseParticipant : lease.currentTerm().version().tenants()) {
            Persistence.service().retrieveMember(leaseParticipant.leaseParticipant().preauthorizedPayments());
            for (PreauthorizedPayment pap : leaseParticipant.leaseParticipant().preauthorizedPayments()) {

                Validate.isTrue(PaymentType.schedulable().contains(pap.paymentMethod().type().getValue()));

                BigDecimal amount;

                PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
                paymentRecord.leaseTermParticipant().set(leaseParticipant);
                paymentRecord.paymentMethod().set(pap.paymentMethod());

                switch (pap.amountType().getValue()) {
                case Percent:
                    if (currentBalance == null) {
                        // Lazy currentBalance initialization
                        currentBalance = ServerSideFactory.create(ARFacade.class).getPADBalance(billingAccount, billingCycle);
                    }
                    percentTotal = percentTotal.add(pap.amount().getValue());
                    amount = DomainUtil.roundMoney(currentBalance.multiply(pap.amount().getValue()));
                    percentAmountTotal = percentAmountTotal.add(amount);
                    break;
                case Value:
                    amount = pap.amount().getValue();
                    break;
                default:
                    throw new IllegalArgumentException();
                }
//                paymentRecord.amount().setValue(amount);
//                paymentRecords.add(paymentRecord);
            }
        }
//
//        // Percent rounding case of total 100%  e.g. 33% + 66%
//        if (percentTotal.compareTo(BigDecimal.ONE) == 0) {
//            BigDecimal unapidBalance = currentBalance.subtract(percentAmountTotal);
//            // Make the fist to  pay fractions
//            paymentRecords.get(0).amount().setValue(paymentRecords.get(0).amount().getValue().add(unapidBalance));
//        }
//
//        for (PaymentRecord paymentRecord : paymentRecords) {
//            schedulePreAuthorizedPaymentRecord(billingCycle, billingAccount, paymentRecord);
//            executionMonitor.addProcessedEvent(paymentRecord.paymentMethod().type().getStringView(), paymentRecord.amount().getValue());
//        }

    }
}
