/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 18, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.PaymentRecord;

class PaymentProcessPostRejected {

    private final ExecutionMonitor executionMonitor;

    PaymentProcessPostRejected(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public void cardsPostRejected() {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.in(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.ProcessingReject);
        criteria.eq(criteria.proto().billingAccount().lease().unit().building().suspended(), false);
        criteria.asc(criteria.proto().billingAccount().lease().unit().building());

        for (final PaymentRecord paymentRecord : Persistence.service().query(criteria, AttachLevel.IdOnly)) {

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() throws RuntimeException {
                        PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processReject(paymentRecord, false);
                        executionMonitor.addProcessedEvent("Reject Posted", processedPaymentRecord.amount().getValue());
                        return null;
                    }
                });

            } catch (Throwable error) {
                executionMonitor.addErredEvent("Process Reject", error);
            }

            if (executionMonitor.isTerminationRequested()) {
                break;
            }
        }
    }

}
