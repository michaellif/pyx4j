/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 4, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.PaymentPostingBatch;
import com.propertyvista.domain.financial.PaymentPostingBatch.PostingStatus;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.property.asset.building.Building;

class MoneyInBatchManager {

    MoneyInBatchManager() {
    }

    PaymentPostingBatch createPostingBatch(Building buildingId) {
        PaymentPostingBatch postingBatch = EntityFactory.create(PaymentPostingBatch.class);
        postingBatch.status().setValue(PostingStatus.Created);
        postingBatch.building().set(buildingId);
        postingBatch.depositDetails().merchantAccount().set(PaymentUtils.retrieveMerchantAccount(buildingId));
        Persistence.service().persist(postingBatch);
        return postingBatch;
    }

    void cancelPostingBatch(final PaymentPostingBatch paymentPostingBatchId, final RunningProcess progress) {
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                PaymentPostingBatch postingBatch = Persistence.service().retrieve(PaymentPostingBatch.class, paymentPostingBatchId.getPrimaryKey());
                Validate.isEquals(PostingStatus.Created, postingBatch.status().getValue(), "Processed batch can't be canceled");
                postingBatch.status().setValue(PostingStatus.Canceled);
                Persistence.service().persist(postingBatch);

                EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
                criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Submitted);
                criteria.eq(criteria.proto().batch(), paymentPostingBatchId);
                progress.progressMaximum.set(Persistence.service().count(criteria));

                for (PaymentRecord paymentRecord : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
                    progress.progress.incrementAndGet();
                    ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
                }

                return null;
            }
        });
    }

    void processPostingBatch(final PaymentPostingBatch paymentPostingBatchId, final RunningProcess progress) {
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                PaymentPostingBatch postingBatch = Persistence.service().retrieve(PaymentPostingBatch.class, paymentPostingBatchId.getPrimaryKey());
                Validate.isEquals(PostingStatus.Created, postingBatch.status().getValue(), "Processed batch can't be posted");
                postingBatch.status().setValue(PostingStatus.Posted);
                postingBatch.depositDetails().merchantAccount().set(PaymentUtils.retrieveMerchantAccount(postingBatch.building()));
                Persistence.service().persist(postingBatch);

                EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
                criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Submitted);
                criteria.eq(criteria.proto().batch(), paymentPostingBatchId);
                criteria.asc(criteria.proto().billingAccount().lease().unit().building());
                progress.progressMaximum.set(Persistence.service().count(criteria));

                // TODO unify RunningProcess  and ExecutionMonitor
                final ExecutionMonitor executionMonitor = new ExecutionMonitor() {
                    @Override
                    protected void onEventAdded() {
                        progress.progress.set(this.getProcessed().intValue());
                    }
                };

                new PaymentBatchPosting().processPayments(criteria, false, executionMonitor);

                // Allow to post batch again
                if (executionMonitor.getErred() > 0) {
                    throw new UserRuntimeException(executionMonitor.getTextMessages());
                }

                return null;
            }
        });

    }

}
