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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadFile.FileAcknowledgmentStatus;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessFacadeImpl.class);

    private static final String EXECUTION_MONITOR_SECTION_NAME_PROCESSED = "PaymentProcessProcessed";

    private static final String EXECUTION_MONITOR_SECTION_NAME_REJECTED = "PaymentProcessRejected";

    private static final String EXECUTION_MONITOR_SECTION_NAME_RETURNED = "PaymentProcessReturned";

    private static final String EXECUTION_MONITOR_SECTION_NAME_DUPLICATE = "PaymentProcessDuplicate";

    private static final String EXECUTION_MONITOR_SECTION_NAME_ERRED = "PaymentProcessErred";

    @Override
    public PadFile preparePadFile() {
        return new PadCaledon().preparePadFile();
    }

    @Override
    public PadFile sendPadFile(PadFile padFile) {
        return new PadCaledon().sendPadFile(padFile);
    }

    @Override
    public void prepareEcheckPayments(ExecutionMonitor executionMonitor, PadFile padFile) {
        // We take all Queued records in this PMC
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Queued));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), PaymentType.Echeck));
        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                PaymentRecord paymentRecord = paymentRecordIterator.next();
                if (new PadProcessor().processPayment(paymentRecord, padFile)) {
                    executionMonitor.addProcessedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_PROCESSED,
                            paymentRecord.amount().getValue(),
                            SimpleMessageFormat.format("eCheck Payment processed")
                    );//@formatter:on
                } else {
                    executionMonitor.addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_REJECTED,
                            paymentRecord.amount().getValue(),
                            SimpleMessageFormat.format("eCheck Payment was rejected")
                    );//@formatter:on
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
    public void processAcknowledgement(final ExecutionMonitor executionMonitor, final PadFile padFile) {
        if (!EnumSet.of(FileAcknowledgmentStatus.BatchAndTransactionReject, FileAcknowledgmentStatus.TransactionReject,
                FileAcknowledgmentStatus.BatchLevelReject, FileAcknowledgmentStatus.Accepted).contains(padFile.acknowledgmentStatus().getValue())) {
            throw new Error("Invalid pad file acknowledgmentStatus " + padFile.acknowledgmentStatus().getValue());
        }

        final String namespace = NamespaceManager.getNamespace();
        List<PadDebitRecord> rejectedRecodrs = TaskRunner.runInOperationsNamespace(new Callable<List<PadDebitRecord>>() {
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
            executionMonitor.addFailedEvent(//@formatter:off
                    EXECUTION_MONITOR_SECTION_NAME_REJECTED,
                    debitRecord.amount().getValue(),
                    SimpleMessageFormat.format("Pad Debit Record was rejected")
            );//@formatter:on
        }

        List<PadBatch> rejectedBatch = TaskRunner.runInOperationsNamespace(new Callable<List<PadBatch>>() {
            @Override
            public List<PadBatch> call() {
                EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                List<PadBatch> rejectedBatch = Persistence.service().query(criteria);
                for (PadBatch padBatch : rejectedBatch) {
                    Persistence.service().retrieveMember(padBatch.records());
                    executionMonitor.addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_REJECTED,
                            padBatch.batchAmount().getValue(),
                            SimpleMessageFormat.format("Pad Batch was rejected")
                    );//@formatter:on
                }
                return rejectedBatch;
            }
        });

        for (PadBatch padBatch : rejectedBatch) {
            new PadProcessor().aggregatedTransferRejected(padBatch);
        }

        Persistence.service().commit();

        if (rejectedBatch.size() == 0 && rejectedRecodrs.size() == 0) {
            Integer countBatchs = TaskRunner.runInOperationsNamespace(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                    criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                    return Persistence.service().count(criteria);
                }
            });
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
    public void processPadReconciliation(ExecutionMonitor executionMonitor, final PadReconciliationFile reconciliationFile) {
        final String namespace = NamespaceManager.getNamespace();

        List<PadReconciliationSummary> transactions = TaskRunner.runInOperationsNamespace(new Callable<List<PadReconciliationSummary>>() {
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
                    executionMonitor.addProcessedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_PROCESSED,
                            debitRecord.amount().getValue(),
                            SimpleMessageFormat.format("Pad Reconcilliation Debit Record was processed")
                    );//@formatter:on
                    break;
                case REJECTED:
                    rejected++;
                    executionMonitor.addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_REJECTED,
                            debitRecord.amount().getValue(),
                            SimpleMessageFormat.format("Pad Reconcilliation Debit Record was rejected")
                    );//@formatter:on
                    break;
                case RETURNED:
                    returned++;
                    executionMonitor.addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_RETURNED,
                            debitRecord.amount().getValue(),
                            SimpleMessageFormat.format("Pad Reconcilliation Debit Record was returned")
                    );//@formatter:on
                    break;
                case DUPLICATE:
                    duplicate++;
                    executionMonitor.addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME_DUPLICATE,
                            debitRecord.amount().getValue(),
                            SimpleMessageFormat.format("Pad Reconcilliation Debit Record is a duplicate, not processed")
                    );//@formatter:on
                    break;
                }
            }
        }
        Persistence.service().commit();
    }

    @Override
    public void createPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate runDate) {
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
                createPreauthorisedPayment(billIterator.next(), executionMonitor);
            }
        } finally {
            billIterator.completeRetrieval();
        }

    }

    private void createPreauthorisedPayment(final Bill bill, final ExecutionMonitor executionMonitor) {
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, PaymentException>() {

                @Override
                public Void execute() throws PaymentException {
                    //Check this bill is latest
                    if (!ServerSideFactory.create(BillingFacade.class).isLatestBill(bill)) {
                        return null;
                    }
                    Persistence.service().retrieve(bill.billingAccount());
                    Persistence.service().retrieve(bill.billingAccount().lease());

                    // call AR facade to get current balance for dueDate
                    BigDecimal currentBalance = ServerSideFactory.create(ARFacade.class).getCurrentBalance(bill.billingAccount());
                    if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
                        return null;
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
                                executionMonitor.addProcessedEvent(//@formatter:off
                                        EXECUTION_MONITOR_SECTION_NAME_PROCESSED,
                                        currentBalance,
                                        SimpleMessageFormat.format("Preauthorized payment created")
                                );//@formatter:on
                            }
                            break tanantLoop;
                        case CoApplicant:
                            //TODO Payment split
                            break;
                        default:
                            break;
                        }

                    }
                    return null;
                }
            });
        } catch (PaymentException e) {
            log.error("Preauthorised payment creation failed", e);
            executionMonitor.addErredEvent(EXECUTION_MONITOR_SECTION_NAME_ERRED, e);
        }
    }

    private void createPreAuthorizedPayment(LeaseTermParticipant leaseParticipant, BigDecimal amount, InternalBillingAccount billingAccount,
            LeasePaymentMethod method) throws PaymentException {
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
    public void processScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Scheduled));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentType));
        criteria.add(PropertyCriterion.le(criteria.proto().targetDate(), Persistence.service().getTransactionSystemTime()));

        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                processScheduledPayment(paymentRecordIterator.next(), executionMonitor);
            }
        } finally {
            paymentRecordIterator.completeRetrieval();
        }
    }

    private void processScheduledPayment(final PaymentRecord paymentRecord, final ExecutionMonitor executionMonitor) {
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, PaymentException>() {

                @Override
                public Void execute() throws PaymentException {
                    PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);

                    if (processedPaymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                        executionMonitor.addFailedEvent(//@formatter:off
                                EXECUTION_MONITOR_SECTION_NAME_REJECTED,
                                processedPaymentRecord.amount().getValue(),
                                SimpleMessageFormat.format("Payment was rejected")
                        );//@formatter:on
                    } else {
                        executionMonitor.addProcessedEvent(//@formatter:off
                                EXECUTION_MONITOR_SECTION_NAME_PROCESSED,
                                processedPaymentRecord.amount().getValue(),
                                SimpleMessageFormat.format("Payment was processed")
                        );//@formatter:on
                    }
                    return null;
                }
            });
        } catch (PaymentException e) {
            log.error("Preauthorised payment creation failed", e);
            executionMonitor.addErredEvent(EXECUTION_MONITOR_SECTION_NAME_ERRED, e);
        }
    }

}
