/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.server.jobs.TaskRunner;

public class PadProcessor {

    private static final Logger log = LoggerFactory.getLogger(PadProcessor.class);

    boolean processPayment(final PaymentRecord paymentRecord, final PadFile padFile) {
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
                PadBatch padBatch = getPadBatch(padFile, pmc, paymentRecord.merchantAccount());
                createPadDebitRecord(padBatch, paymentRecord);
                return null;
            }
        });

        return true;
    }

    static PadBatch getPadBatch(PadFile padFile, Pmc pmc, MerchantAccount merchantAccount) {
        EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
        criteria.eq(criteria.proto().padFile(), padFile);
        criteria.eq(criteria.proto().pmc(), pmc);
        criteria.eq(criteria.proto().merchantAccountKey(), merchantAccount.id());
        PadBatch padBatch = Persistence.service().retrieve(criteria);
        if (padBatch == null) {
            padBatch = EntityFactory.create(PadBatch.class);
            padBatch.padFile().set(padFile);
            padBatch.pmc().set(pmc);

            padBatch.merchantTerminalId().setValue(merchantAccount.merchantTerminalId().getValue());
            padBatch.bankId().setValue(merchantAccount.bankId().getValue());
            padBatch.branchTransitNumber().setValue(merchantAccount.branchTransitNumber().getValue());
            padBatch.accountNumber().setValue(merchantAccount.accountNumber().getValue());
            padBatch.chargeDescription().setValue(merchantAccount.chargeDescription().getValue());

            padBatch.merchantAccountKey().setValue(merchantAccount.id().getValue());
            Persistence.service().persist(padBatch);
        }
        return padBatch;
    }

    private void createPadDebitRecord(PadBatch padBatch, PaymentRecord paymentRecord) {
        PadDebitRecord padRecord = EntityFactory.create(PadDebitRecord.class);
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

    void cancelAggregatedTransfer(AggregatedTransfer aggregatedTransfer) {
        Persistence.service().retrieveMember(aggregatedTransfer.rejectedBatchPayments(), AttachLevel.Attached);
        for (PaymentRecord paymentRecord : aggregatedTransfer.rejectedBatchPayments()) {
            if (paymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Queued) {
                ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
            }
        }
        aggregatedTransfer.status().setValue(AggregatedTransferStatus.Canceled);
        Persistence.service().persist(aggregatedTransfer);
    }

}
