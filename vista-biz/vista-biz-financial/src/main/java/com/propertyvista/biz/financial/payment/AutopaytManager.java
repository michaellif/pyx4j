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
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

class AutopaytManager {

    private static final I18n i18n = I18n.get(AutopaytManager.class);

    static class PreauthorizedAmount {

        LeaseTermTenant leaseTermTenant;

        AutopayAgreement preauthorizedPayment;

        BigDecimal amount;

        String notice;
    }

    List<PaymentRecord> reportPreauthorisedPayments(PreauthorizedPaymentsReportCriteria reportCriteria, ExecutionMonitor executionMonitor) {
        List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
        createPreauthorisedPayments(executionMonitor, reportCriteria.padGenerationDate, true, paymentRecords, reportCriteria);
        return paymentRecords;
    }

    void createPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate) {
        createPreauthorisedPayments(executionMonitor, runDate, false, null, null);
    }

    private void createPreauthorisedPayments(final ExecutionMonitor executionMonitor, LogicalDate runDate, boolean reportOny,
            List<PaymentRecord> resultingPaymentRecords, PreauthorizedPaymentsReportCriteria reportCriteria) {

        EntityQueryCriteria<BillingCycle> criteria;
        {//TODO->Closure
            criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.eq(criteria.proto().targetAutopayExecutionDate(), runDate);
            criteria.isNull(criteria.proto().actualAutopayExecutionDate());
            if ((reportCriteria != null) && (reportCriteria.selectedBuildings != null)) {
                criteria.in(criteria.proto().building(), reportCriteria.selectedBuildings);
            } else {
                criteria.in(criteria.proto().building().suspended(), false);
            }
        }
        // calculate total 
        {
            ICursorIterator<BillingCycle> billingCycleIterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
            try {
                long expectedTotal = 0L;
                while (billingCycleIterator.hasNext() & !executionMonitor.isTerminationRequested()) {
                    expectedTotal += Persistence.service().count(createBillingCyclePreauthorisedQueryCriteria(billingCycleIterator.next(), reportCriteria));
                }
                executionMonitor.setExpectedTotal(expectedTotal);
            } finally {
                IOUtils.closeQuietly(billingCycleIterator);
            }
        }

        // process payment records
        {
            ICursorIterator<BillingCycle> billingCycleIterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
            try {
                while (billingCycleIterator.hasNext() & !executionMonitor.isTerminationRequested()) {
                    createBillingCyclePreauthorisedPayments(billingCycleIterator.next(), executionMonitor, reportOny, resultingPaymentRecords, reportCriteria);
                }
            } finally {
                billingCycleIterator.close();
            }
        }
    }

    private void createBillingCyclePreauthorisedPayments(final BillingCycle billingCycle, final ExecutionMonitor executionMonitor, final boolean reportOny,
            final List<PaymentRecord> resultingPaymentRecords, final PreauthorizedPaymentsReportCriteria reportCriteria) {

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {

                ICursorIterator<BillingAccount> billingAccountIterator;
                { //TODO->Closure

                    EntityQueryCriteria<BillingAccount> criteria = createBillingCyclePreauthorisedQueryCriteria(billingCycle, reportCriteria);
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
                    billingCycle.actualAutopayExecutionDate().setValue(new LogicalDate(SystemDateManager.getDate()));
                    Persistence.service().persist(billingCycle);
                }

                return null;
            }
        });
    }

    private EntityQueryCriteria<BillingAccount> createBillingCyclePreauthorisedQueryCriteria(final BillingCycle billingCycle,
            final PreauthorizedPaymentsReportCriteria reportCriteria) {
        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.eq(criteria.proto().lease().unit().building(), billingCycle.building());
        criteria.eq(criteria.proto().billingType(), billingCycle.billingType());
        criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());

        if (reportCriteria != null) {
            if (reportCriteria.isLeasesOnNoticeOnly()) {
                criteria.eq(criteria.proto().lease().completion(), Lease.CompletionType.Notice);
            }
            if (reportCriteria.hasExpectedMoveOutFilter()) {
                criteria.ge(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMinExpectedMoveOut());
                criteria.le(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMaxExpectedMoveOut());
            }
        }

        criteria.asc(criteria.proto().lease().leaseId());
        return criteria;
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
            paymentRecord._assert_autopayCoveredItemsChanges().addAll(record.preauthorizedPayment.coveredItems());
            paymentRecord.notice().setValue(record.notice);
            paymentRecord.padBillingCycle().set(billingCycle);
            paymentRecord.billingAccount().set(billingAccount);
            paymentRecord.targetDate().setValue(billingCycle.targetAutopayExecutionDate().getValue());
            createNoticeMessage(paymentRecord, record.notice);
            paymentRecords.add(paymentRecord);
        }

        return paymentRecords;
    }

    /**
     * Calculate total amounts.
     */
    List<PreauthorizedAmount> calulatePapAmounts(BillingCycle billingCycle, BillingAccount billingAccount) {
        List<PreauthorizedAmount> records = new ArrayList<PreauthorizedAmount>();

        Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.Attached);
        AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(billingAccount.lease().unit().building(),
                AutoPayPolicy.class);
        if (!billingAccount.lease().status().getValue().isCurrent()
                || !AutopayAgreementMananger.isPreauthorizedPaymentsApplicableForBillingCycle(billingAccount.lease(), billingCycle, autoPayPolicy)) {
            // Do not create payments
            return records;
        }

        List<LeaseTermTenant> leaseParticipants;
        {
            EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
            criteria.eq(criteria.proto().leaseTermV().holder().lease().billingAccount(), billingAccount);
            criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
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
            List<AutopayAgreement> preauthorizedPayments;
            {
                EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
                criteria.eq(criteria.proto().tenant(), leaseParticipant.leaseParticipant().cast());
                criteria.eq(criteria.proto().isDeleted(), false);
                {
                    OrCriterion or = criteria.or();
                    or.right().le(criteria.proto().effectiveFrom(), billingCycle.billingCycleStartDate());
                    or.left().isNull(criteria.proto().effectiveFrom());
                }

                criteria.asc(criteria.proto().id());
                preauthorizedPayments = Persistence.service().query(criteria);
            }
            for (AutopayAgreement pap : preauthorizedPayments) {

                Validate.isTrue(pap.paymentMethod().type().getValue().isSchedulable());

                PreauthorizedAmount record = new PreauthorizedAmount();
                record.leaseTermTenant = leaseParticipant;
                record.preauthorizedPayment = pap;

                record.amount = BigDecimal.ZERO;
                for (AutopayAgreementCoveredItem item : pap.coveredItems()) {
                    record.amount = record.amount.add(item.amount().getValue());
                }
                records.add(record);
            }
        }
        return records;
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
