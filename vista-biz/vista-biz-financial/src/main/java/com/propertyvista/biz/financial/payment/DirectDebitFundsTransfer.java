/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
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
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount;
import com.propertyvista.server.jobs.TaskRunner;

class DirectDebitFundsTransfer {

    private final ExecutionMonitor executionMonitor;

    private final PadFile padFile;

    private final Pmc pmc;

    private final BigDecimal directBankingFee;

    private final VistaMerchantAccount vistaMerchantAccount;

    DirectDebitFundsTransfer(ExecutionMonitor executionMonitor, PadFile padFile) {
        this.executionMonitor = executionMonitor;
        this.padFile = padFile;
        this.pmc = VistaDeployment.getCurrentPmc();
        this.directBankingFee = ServerSideFactory.create(Vista2PmcFacade.class).getPaymentFees().directBankingFee().getValue();

        vistaMerchantAccount = TaskRunner.runInOperationsNamespace(new Callable<VistaMerchantAccount>() {
            @Override
            public VistaMerchantAccount call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(VistaMerchantAccount.class));
            }
        });
        if ((vistaMerchantAccount == null || vistaMerchantAccount.merchantTerminalId().isNull())) {
            throw new UserRuntimeException("Vista MerchantAccount is not setup");
        }
    }

    void prepareDirectDebitFundsTransfer() {
        // We take all Queued records in this PMC
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Queued);
        criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.DirectBanking);
        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                final PaymentRecord paymentRecord = paymentRecordIterator.next();
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        if (addRecordToBatch(paymentRecord)) {
                            executionMonitor.addProcessedEvent("Processed amount", paymentRecord.amount().getValue());
                        } else {
                            executionMonitor.addFailedEvent("No Merchant Account", paymentRecord.amount().getValue());
                        }
                        return null;
                    }

                });
                // If there are error we may create new run again.
            }
        } finally {
            paymentRecordIterator.close();
        }
    }

    private boolean addRecordToBatch(final PaymentRecord paymentRecord) {
        final MerchantAccount merchantAccount = PaymentUtils.retrieveMerchantAccount(paymentRecord);
        if ((merchantAccount == null) || (!PaymentUtils.isElectronicPaymentsSetup(merchantAccount))) {
            return false;
        }
        paymentRecord.merchantAccount().set(merchantAccount);
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PadBatch padBatch = PadProcessor.getPadBatch(padFile, pmc, merchantAccount);
                updatePadDebitRecord(padBatch, paymentRecord);
                return null;
            }

        });

        return true;
    }

    private void updatePadDebitRecord(PadBatch padBatch, PaymentRecord paymentRecord) {
        EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
        criteria.eq(criteria.proto().padBatch(), padBatch);
        PadDebitRecord padRecord = Persistence.service().retrieve(criteria);
        if (padRecord == null) {
            padRecord = EntityFactory.create(PadDebitRecord.class);
            padRecord.padBatch().set(padBatch);
            padRecord.processed().setValue(Boolean.FALSE);
            padRecord.clientId().setValue("vista");
            padRecord.amount().setValue(BigDecimal.ZERO);
            padRecord.transactionId().setValue(PadTransactionUtils.toCaldeonTransactionId(padBatch.id()));

            padRecord.bankId().setValue(vistaMerchantAccount.bankId().getValue());
            padRecord.branchTransitNumber().setValue(vistaMerchantAccount.branchTransitNumber().getValue());
            padRecord.accountNumber().setValue(vistaMerchantAccount.accountNumber().getValue());
        }

        BigDecimal transactionAmount;
        if (paymentRecord.amount().getValue().compareTo(directBankingFee) > 0) {
            transactionAmount = paymentRecord.amount().getValue().subtract(directBankingFee);
            executionMonitor.addInfoEvent("Fee amount", null, directBankingFee);
        } else {
            transactionAmount = new BigDecimal("0.01");
            executionMonitor.addInfoEvent("Fee amount", null, paymentRecord.amount().getValue().subtract(transactionAmount));
        }
        padRecord.amount().setValue(padRecord.amount().getValue().add(transactionAmount));

        Persistence.service().persist(padRecord);
    }
}
