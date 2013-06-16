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
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

//TODO Preauthorised rename to Preauthorized
class PreauthorisedPaymentsManager {

    private static final I18n i18n = I18n.get(PreauthorisedPaymentsManager.class);

    private static class PreauthorizedAmount {

        LeaseTermTenant leaseTermTenant;

        PreauthorizedPayment preauthorizedPayment;

        BigDecimal amount;

        String notice;
    }

    List<PaymentRecord> reportPreauthorisedPayments(LogicalDate runDate, List<Building> selectedBuildings) {
        List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
        createPreauthorisedPayments(new ExecutionMonitor(), runDate, true, paymentRecords, selectedBuildings);
        return paymentRecords;
    }

    void createPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate) {
        createPreauthorisedPayments(executionMonitor, runDate, false, null, null);
    }

    private void createPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate, boolean reportOny,
            List<PaymentRecord> resultingPaymentRecords, List<Building> selectedBuildings) {
        ICursorIterator<BillingCycle> billingCycleIterator;
        {//TODO->Closure
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.eq(criteria.proto().targetPadGenerationDate(), runDate);
            criteria.isNull(criteria.proto().actualPadGenerationDate());
            if (selectedBuildings != null) {
                criteria.in(criteria.proto().building(), selectedBuildings);
            }
            billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingCycleIterator.hasNext()) {
                createBillingCyclePreauthorisedPayments(billingCycleIterator.next(), executionMonitor, reportOny, resultingPaymentRecords);
            }
        } finally {
            billingCycleIterator.close();
        }
    }

    void updateScheduledPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate) {
        ICursorIterator<BillingCycle> billingCycleIterator;
        {//TODO->Closure
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.lt(criteria.proto().targetPadGenerationDate(), runDate);
            criteria.gt(criteria.proto().targetPadExecutionDate(), runDate);
            criteria.isNotNull(criteria.proto().actualPadGenerationDate());
            criteria.asc(criteria.proto().building().propertyCode());
            billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingCycleIterator.hasNext()) {
                updateBillingCyclePreauthorisedPayments(billingCycleIterator.next(), executionMonitor);
            }
        } finally {
            billingCycleIterator.close();
        }
    }

    private void createBillingCyclePreauthorisedPayments(final BillingCycle billingCycle, final ExecutionMonitor executionMonitor, final boolean reportOny,
            final List<PaymentRecord> resultingPaymentRecords) {

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {

                ICursorIterator<BillingAccount> billingAccountIterator;
                { //TODO->Closure
                    EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
                    criteria.eq(criteria.proto().lease().unit().building(), billingCycle.building());
                    criteria.eq(criteria.proto().billingType(), billingCycle.billingType());
                    criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
                    criteria.asc(criteria.proto().lease().leaseId());
                    billingAccountIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
                }
                try {
                    while (billingAccountIterator.hasNext()) {
                        List<PaymentRecord> records = createBillingAccountPreauthorisedPayments(billingCycle, billingAccountIterator.next(), executionMonitor,
                                reportOny);
                        if (resultingPaymentRecords != null) {
                            resultingPaymentRecords.addAll(records);
                        }
                    }
                } finally {
                    billingAccountIterator.close();
                }

                if (!reportOny) {
                    billingCycle.actualPadGenerationDate().setValue(new LogicalDate(SystemDateManager.getDate()));
                    Persistence.service().persist(billingCycle);
                }

                return null;
            }
        });
    }

    private List<PaymentRecord> createBillingAccountPreauthorisedPayments(BillingCycle billingCycle, BillingAccount billingAccount,
            ExecutionMonitor executionMonitor, boolean reportOny) {
        // Validate that PAD was not created for this account
        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().padBillingCycle(), billingCycle);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            if (Persistence.service().count(criteria) != 0) {
                throw new Error(SimpleMessageFormat.format("Pad already created for {} {}", billingAccount, billingCycle));
            }
        }

        List<PaymentRecord> paymentRecords = calulatePreauthorizedPayment(billingCycle, billingAccount);
        for (PaymentRecord paymentRecord : paymentRecords) {
            if (!reportOny) {
                ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
                ServerSideFactory.create(PaymentFacade.class).schedulePayment(paymentRecord);
            }
            executionMonitor.addProcessedEvent(paymentRecord.paymentMethod().type().getStringView(), paymentRecord.amount().getValue());
        }
        return paymentRecords;
    }

    public List<PaymentRecord> calulatePreauthorizedPayment(BillingCycle billingCycle, BillingAccount billingAccount) {
        List<PreauthorizedAmount> records = calulatePapAmounts(billingCycle, billingAccount);

        List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
        for (PreauthorizedAmount record : records) {

            PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
            paymentRecord.amount().setValue(record.amount);
            paymentRecord.leaseTermParticipant().set(record.leaseTermTenant);
            paymentRecord.paymentMethod().set(record.preauthorizedPayment.paymentMethod());
            paymentRecord.preauthorizedPayment().set(record.preauthorizedPayment);
            paymentRecord.notice().setValue(record.notice);
            paymentRecord.padBillingCycle().set(billingCycle);
            paymentRecord.billingAccount().set(billingAccount);
            paymentRecord.targetDate().setValue(billingCycle.targetPadExecutionDate().getValue());
            createNoticeMessage(paymentRecord, record.notice);
            paymentRecords.add(paymentRecord);
        }

        return paymentRecords;
    }

    /**
     * Calculate Percentage with rounding.
     */
    private List<PreauthorizedAmount> calulatePapAmounts(BillingCycle billingCycle, BillingAccount billingAccount) {
        List<PreauthorizedAmount> records = new ArrayList<PreauthorizedAmount>();
        List<LeaseTermTenant> leaseParticipants;
        {
            EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
            criteria.eq(criteria.proto().leaseTermV().holder().lease().billingAccount(), billingAccount);
            criteria.isCurrent(criteria.proto().leaseTermV());
            criteria.asc(criteria.proto().leaseParticipant().participantId());
            leaseParticipants = Persistence.service().query(criteria);

            // Make Applicant first 
            for (int i = 0; i < leaseParticipants.size(); i++) {
                LeaseTermTenant leaseParticipant = leaseParticipants.get(i);
                if (leaseParticipant.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                    if (i != 0) {
                        leaseParticipants.remove(i);
                        leaseParticipants.add(0, leaseParticipant);
                    }
                    break;
                }
            }
        }

        for (LeaseTermTenant leaseParticipant : leaseParticipants) {
            List<PreauthorizedPayment> preauthorizedPayments;
            {
                EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
                criteria.eq(criteria.proto().tenant(), leaseParticipant.leaseParticipant().cast());
                criteria.eq(criteria.proto().isDeleted(), false);

                {
                    OrCriterion or = criteria.or();
                    or.right().ge(criteria.proto().expiring(), billingCycle.targetPadGenerationDate());
                    or.left().isNull(criteria.proto().expiring());
                }
                {
                    OrCriterion or = criteria.or();
                    or.right().le(criteria.proto().effectiveFrom(), billingCycle.targetPadExecutionDate());
                    or.left().isNull(criteria.proto().effectiveFrom());
                }

                criteria.asc(criteria.proto().id());
                preauthorizedPayments = Persistence.service().query(criteria);
            }
            for (PreauthorizedPayment pap : preauthorizedPayments) {

                Validate.isTrue(pap.paymentMethod().type().getValue().isSchedulable());

                PreauthorizedAmount record = new PreauthorizedAmount();
                record.leaseTermTenant = leaseParticipant;
                record.preauthorizedPayment = pap;

                record.amount = BigDecimal.ZERO;
                for (PreauthorizedPaymentCoveredItem item : pap.coveredItems()) {
                    record.amount = record.amount.add(item.amount().getValue());
                }
                records.add(record);
            }
        }
        return records;
    }

    private void updateBillingCyclePreauthorisedPayments(final BillingCycle billingCycle, final ExecutionMonitor executionMonitor) {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                ICursorIterator<BillingAccount> billingAccountIterator;
                { //TODO->Closure
                    EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
                    criteria.eq(criteria.proto().lease().unit().building(), billingCycle.building());
                    criteria.eq(criteria.proto().billingType(), billingCycle.billingType());

                    criteria.eq(criteria.proto().payments().$().padBillingCycle(), billingCycle);
                    billingAccountIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
                }
                try {
                    while (billingAccountIterator.hasNext()) {
                        updateBillingAccountPreauthorisedPayments(billingCycle, billingAccountIterator.next(), executionMonitor);
                    }
                } finally {
                    billingAccountIterator.close();
                }
                return null;
            }
        });
    }

    private void updateBillingAccountPreauthorisedPayments(BillingCycle billingCycle, BillingAccount billingAccount, ExecutionMonitor executionMonitor) {
        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().padBillingCycle(), billingCycle);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            criteria.in(criteria.proto().paymentStatus(), PaymentStatus.Scheduled, PaymentStatus.PendingAction);
            if (Persistence.service().count(criteria) == 0) {
                //Nothing to update
                return;
            }
        }

        boolean hasPendingAction = false;
        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().padBillingCycle(), billingCycle);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            criteria.in(criteria.proto().paymentStatus(), PaymentStatus.PendingAction);
            hasPendingAction = (Persistence.service().count(criteria) != 0);

        }
        boolean electronicPaymentsNotSetup = !PaymentUtils.isElectronicPaymentsSetup(billingAccount);

        List<PreauthorizedAmount> records = calulatePapAmounts(billingCycle, billingAccount);

        for (PreauthorizedAmount record : records) {

            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().padBillingCycle(), billingCycle);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            criteria.eq(criteria.proto().preauthorizedPayment(), record.preauthorizedPayment);
            criteria.in(criteria.proto().paymentStatus(), PaymentStatus.Scheduled, PaymentStatus.PendingAction);

            PaymentRecord paymentRecord = Persistence.service().retrieve(criteria);

            if ((paymentRecord != null)
                    && (electronicPaymentsNotSetup || hasPendingAction || (paymentRecord.amount().getValue().compareTo(record.amount) != 0))) {
                paymentRecord.amount().setValue(record.amount);
                createNoticeMessage(paymentRecord, record.notice);
                ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
                PaymentRecord paymentRecordUpdated = ServerSideFactory.create(PaymentFacade.class).schedulePayment(paymentRecord);
                if ((paymentRecord.amount().getValue().compareTo(record.amount) != 0)
                        || (!paymentRecordUpdated.paymentStatus().equals(paymentRecord.paymentStatus()))) {
                    executionMonitor.addProcessedEvent(paymentRecord.paymentMethod().type().getStringView(), paymentRecord.amount().getValue());
                }
            }
        }
    }

    private void createNoticeMessage(PaymentRecord paymentRecord, String calulationsNotice) {
        StringBuilder m = new StringBuilder();
        if (!PaymentUtils.isElectronicPaymentsSetup(paymentRecord.billingAccount())) {
            m.append(i18n.tr("No active merchantAccount found to process the payment."));
        }
        if (calulationsNotice != null) {
            if (m.length() > 0) {
                m.append("\n");
            }
            m.append(calulationsNotice);
        }
        paymentRecord.notice().setValue(m.toString());
    }
}
