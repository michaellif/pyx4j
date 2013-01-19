/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.payment.pad.PadFile.FileAcknowledgmentStatus;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.server.jobs.StatisticsUtils;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    @Override
    public PadFile preparePadFile() {
        return new PadCaledon().preparePadFile();
    }

    @Override
    public PadFile sendPadFile(PadFile padFile) {
        return new PadCaledon().sendPadFile(padFile);
    }

    @Override
    public void prepareEcheckPayments(StatisticsRecord dynamicStatisticsRecord, PadFile padFile) {
        // We take all Queued records in this PMC
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Queued));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), PaymentType.Echeck));
        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                PaymentRecord paymentRecord = paymentRecordIterator.next();
                if (new PadProcessor().processPayment(paymentRecord, padFile)) {
                    StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1, paymentRecord.amount().getValue());
                } else {
                    StatisticsUtils.addFailed(dynamicStatisticsRecord, 1, paymentRecord.amount().getValue());
                }
            }
        } finally {
            paymentRecordIterator.completeRetrieval();
        }
        Persistence.service().commit();
    }

    @Override
    public PadFile recivePadAcknowledgementFile() {
        return new PadCaledon().recivePadAcknowledgementFile();
    }

    @Override
    public void processAcknowledgement(StatisticsRecord dynamicStatisticsRecord, final PadFile padFile) {
        if (!EnumSet.of(FileAcknowledgmentStatus.BatchAndTransactionReject, FileAcknowledgmentStatus.TransactionReject,
                FileAcknowledgmentStatus.BatchLevelReject, FileAcknowledgmentStatus.Accepted).contains(padFile.acknowledgmentStatus().getValue())) {
            throw new Error("Invalid pad file acknowledgmentStatus " + padFile.acknowledgmentStatus().getValue());
        }

        final String namespace = NamespaceManager.getNamespace();
        List<PadDebitRecord> rejectedRecodrs = TaskRunner.runInAdminNamespace(new Callable<List<PadDebitRecord>>() {
            @Override
            public List<PadDebitRecord> call() {
                EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                return Persistence.service().query(criteria);
            }
        });

        for (PadDebitRecord debitRecord : rejectedRecodrs) {
            new PadProcessor().acknowledgmentReject(debitRecord);
            StatisticsUtils.addFailed(dynamicStatisticsRecord, 1, debitRecord.amount().getValue());
        }

        List<PadBatch> rejectedBatch = TaskRunner.runInAdminNamespace(new Callable<List<PadBatch>>() {
            @Override
            public List<PadBatch> call() {
                EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                List<PadBatch> rejectedBatch = Persistence.service().query(criteria);
                for (PadBatch padBatch : rejectedBatch) {
                    Persistence.service().retrieveMember(padBatch.records());
                }
                return rejectedBatch;
            }
        });

        for (PadBatch padBatch : rejectedBatch) {
            new PadProcessor().aggregatedTransferRejected(padBatch);
        }

        Persistence.service().commit();

        if (rejectedBatch.size() == 0 && rejectedRecodrs.size() == 0) {
            Integer countBatchs = TaskRunner.runInAdminNamespace(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                    criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                    return Persistence.service().count(criteria);
                }
            });
            if (countBatchs > 0) {
                dynamicStatisticsRecord.message().setValue("All Accepted");
            }
        } else {
            dynamicStatisticsRecord.message().setValue("Batch Level Reject:" + rejectedBatch.size() + "; Transaction Reject:" + rejectedRecodrs.size());
        }
    }

    @Override
    public void updatePadFileAcknowledProcessingStatus(PadFile padFileId) {
        PadFile padFile = Persistence.service().retrieve(PadFile.class, padFileId.getPrimaryKey());
        if (padFile.status().getValue() != PadFile.PadFileStatus.Acknowledged) {
            throw new IllegalArgumentException(SimpleMessageFormat.format("Invalid PadFile {0} status {1}", padFile.id(), padFile.status()));
        }
        padFile.status().setValue(PadFile.PadFileStatus.AcknowledgeProcesed);

        EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
        criteria.add(PropertyCriterion.eq(criteria.proto().processed(), Boolean.FALSE));
        int unprocessedRecords = Persistence.service().count(criteria);
        if (unprocessedRecords == 0) {
            padFile.status().setValue(PadFile.PadFileStatus.Procesed);
        }
        Persistence.service().persist(padFile);
    }

    @Override
    public void updatePadFileReconciliationProcessingStatus() {
        EntityQueryCriteria<PadFile> filesCriteria = EntityQueryCriteria.create(PadFile.class);
        filesCriteria.add(PropertyCriterion.eq(filesCriteria.proto().status(), PadFile.PadFileStatus.AcknowledgeProcesed));
        for (PadFile padFile : Persistence.service().query(filesCriteria)) {
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
            criteria.add(PropertyCriterion.eq(criteria.proto().processed(), Boolean.FALSE));
            int unprocessedRecords = Persistence.service().count(criteria);
            if (unprocessedRecords == 0) {
                padFile.status().setValue(PadFile.PadFileStatus.Procesed);
                Persistence.service().persist(padFile);
            }
        }
    }

    @Override
    public PadReconciliationFile recivePadReconciliation() {
        return new PadCaledon().recivePadReconciliation();
    }

    @Override
    public void processPadReconciliation(StatisticsRecord dynamicStatisticsRecord, final PadReconciliationFile reconciliationFile) {
        final String namespace = NamespaceManager.getNamespace();

        List<PadReconciliationSummary> transactions = TaskRunner.runInAdminNamespace(new Callable<List<PadReconciliationSummary>>() {
            @Override
            public List<PadReconciliationSummary> call() throws Exception {
                EntityQueryCriteria<PadReconciliationSummary> criteria = EntityQueryCriteria.create(PadReconciliationSummary.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().reconciliationFile(), reconciliationFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccount().pmc().namespace(), namespace));

                return Persistence.service().query(criteria);
            }
        });

        if (transactions.size() == 0) {
            return;
        }

        int processed = 0;
        int returned = 0;
        int rejected = 0;
        int duplicate = 0;

        for (PadReconciliationSummary summary : transactions) {
            new PadProcessor().aggregatedTransferReconciliation(summary);

            for (PadReconciliationDebitRecord debitRecord : summary.records()) {
                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    processed++;
                    StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1, debitRecord.amount().getValue());
                    break;
                case REJECTED:
                    rejected++;
                    StatisticsUtils.addFailed(dynamicStatisticsRecord, 1, debitRecord.amount().getValue());
                    break;
                case RETURNED:
                    returned++;
                    StatisticsUtils.addFailed(dynamicStatisticsRecord, 1, debitRecord.amount().getValue());
                    break;
                case DUPLICATE:
                    duplicate++;
                    StatisticsUtils.addFailed(dynamicStatisticsRecord, 1, debitRecord.amount().getValue());
                    break;
                }
            }
        }
        Persistence.service().commit();

        StringBuilder message = new StringBuilder();
        if (processed != 0) {
            message.append("Processed:").append(processed);
        }
        if (returned != 0) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("Returned:").append(returned);
        }
        if (rejected != 0) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("Rejected:").append(rejected);
        }
        if (duplicate != 0) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("Duplicate:").append(duplicate);
        }

        dynamicStatisticsRecord.message().setValue(message.toString());
    }

    @Override
    public void createPreauthorisedPayments(StatisticsRecord dynamicStatisticsRecord, LogicalDate runDate) {
        // Find Bills 
        //For Due Date (trigger target date), go over all Bills that have specified DueDate - see if this bill not yet created preauthorised payments and create one
        Calendar c = new GregorianCalendar();
        c.setTime(runDate);

        // For now we do the same day payments.  No Policy!

        //c.add(Calendar.DAY_OF_YEAR, 4);

        LogicalDate dueDate = new LogicalDate(c.getTime());

        EntityQueryCriteria<Bill> billCriteria = EntityQueryCriteria.create(Bill.class);
        billCriteria.add(PropertyCriterion.eq(billCriteria.proto().dueDate(), dueDate));
        billCriteria.add(PropertyCriterion.eq(billCriteria.proto().billStatus(), Bill.BillStatus.Confirmed));

        ICursorIterator<Bill> billIterator = Persistence.service().query(null, billCriteria, AttachLevel.Attached);
        try {
            while (billIterator.hasNext()) {
                Bill bill = billIterator.next();
                //Check this bill is latest
                if (!ServerSideFactory.create(BillingFacade.class).isLatestBill(bill)) {
                    continue;
                }
                Persistence.service().retrieve(bill.billingAccount());
                Persistence.service().retrieve(bill.billingAccount().lease());

                // call AR facade to get current balance for dueDate
                BigDecimal currentBalance = ServerSideFactory.create(ARFacade.class).getCurrentBalance(bill.billingAccount());
                if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                Lease lease = bill.billingAccount().lease();
                Persistence.service().retrieve(lease.currentTerm().version().tenants());

                tanantLoop: for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
                    // do pre-authorized payments for main applicant for now
                    switch (tenant.role().getValue()) {
                    case Applicant:
                        LeasePaymentMethod method = PaymentUtils.retrievePreAuthorizedPaymentMethod(tenant);
                        if (method != null) {
                            createPreAuthorizedPayment(tenant, currentBalance, bill.billingAccount(), method);
                            StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1, currentBalance);
                        }
                        break tanantLoop;
                    case CoApplicant:
                        //TODO Payment split
                        break;
                    default:
                        break;
                    }

                }

            }
        } finally {
            billIterator.completeRetrieval();
        }

        Persistence.service().commit();
    }

    private void createPreAuthorizedPayment(LeaseTermParticipant leaseParticipant, BigDecimal amount, InternalBillingAccount billingAccount, LeasePaymentMethod method) {
        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.billingAccount().set(billingAccount);
        paymentRecord.leaseTermParticipant().set(leaseParticipant);
        paymentRecord.amount().setValue(amount);
        paymentRecord.paymentMethod().set(method);

        switch (method.type().getValue()) {
        case Echeck:
            ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
            ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
            break;
        case CreditCard:
            paymentRecord.targetDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
            ServerSideFactory.create(PaymentFacade.class).schedulePayment(paymentRecord);
            break;
        default:
            throw new IllegalArgumentException("Invalid PreAuthorized Payment Method:" + paymentRecord.paymentMethod().type().getStringView());
        }
    }

    @Override
    public void processScheduledPayments(StatisticsRecord dynamicStatisticsRecord, PaymentType paymentType) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Scheduled));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentType));
        criteria.add(PropertyCriterion.le(criteria.proto().targetDate(), Persistence.service().getTransactionSystemTime()));

        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                PaymentRecord paymentRecord = paymentRecordIterator.next();
                paymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);

                if (paymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                    StatisticsUtils.addFailed(dynamicStatisticsRecord, 1, paymentRecord.amount().getValue());
                } else {
                    StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1, paymentRecord.amount().getValue());
                }
            }
        } finally {
            paymentRecordIterator.completeRetrieval();
        }
        Persistence.service().commit();
    }

}
