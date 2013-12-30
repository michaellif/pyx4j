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

import java.util.concurrent.Callable;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.FundsTransferBatch;
import com.propertyvista.operations.domain.payment.pad.FundsTransferRecord;
import com.propertyvista.operations.domain.payment.pad.FundsTransferFile;
import com.propertyvista.server.jobs.TaskRunner;

class PadFundsTransfer {

    private final ExecutionMonitor executionMonitor;

    private final FundsTransferFile padFile;

    PadFundsTransfer(ExecutionMonitor executionMonitor, FundsTransferFile padFile) {
        this.executionMonitor = executionMonitor;
        this.padFile = padFile;
    }

    void prepareEcheckFundsTransfer() {
        // We take all Queued records in this PMC
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Queued);
        criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.Echeck);
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
        MerchantAccount merchantAccount = PaymentUtils.retrieveMerchantAccount(paymentRecord);
        if ((merchantAccount == null) || (!PaymentUtils.isElectronicPaymentsSetup(merchantAccount))) {
            return false;
        }
        paymentRecord.merchantAccount().set(merchantAccount);
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        Persistence.service().retrieve(paymentRecord.billingAccount());

        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                FundsTransferBatch padBatch = FundsTransferCaledon.getPadBatch(padFile, pmc, paymentRecord.merchantAccount());
                createPadDebitRecord(padBatch, paymentRecord);
                return null;
            }
        });

        return true;
    }

    private void createPadDebitRecord(FundsTransferBatch padBatch, PaymentRecord paymentRecord) {
        FundsTransferRecord padRecord = EntityFactory.create(FundsTransferRecord.class);
        padRecord.padBatch().set(padBatch);
        padRecord.processed().setValue(Boolean.FALSE);
        padRecord.clientId().setValue(paymentRecord.billingAccount().accountNumber().getValue());
        padRecord.amount().setValue(paymentRecord.amount().getValue());
        EcheckInfo echeckInfo = paymentRecord.paymentMethod().details().cast();

        padRecord.bankId().setValue(echeckInfo.bankId().getValue());
        padRecord.branchTransitNumber().setValue(echeckInfo.branchTransitNumber().getValue());
        padRecord.accountNumber().setValue(echeckInfo.accountNo().number().getValue());

        padRecord.transactionId().setValue(PadTransactionUtils.toCaldeonTransactionId(paymentRecord.id()));

        Persistence.service().persist(padRecord);

    }
}
