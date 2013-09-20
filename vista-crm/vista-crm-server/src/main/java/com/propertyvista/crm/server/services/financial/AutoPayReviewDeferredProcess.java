/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapsHolderDTO;

public class AutoPayReviewDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final ReviewedPapsHolderDTO acceptedReviews;

    private final AtomicInteger progress;

    public AutoPayReviewDeferredProcess(ReviewedPapsHolderDTO acceptedReviews) {
        this.acceptedReviews = acceptedReviews;
        progress = new AtomicInteger();
        progress.set(0);
        acceptedReviews.acceptedReviewedPaps().size();

    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                for (ReviewedPapDTO preauthorizedPaymentChanges : acceptedReviews.acceptedReviewedPaps()) {
                    ServerSideFactory.create(PaymentMethodFacade.class).persitPreauthorizedPaymentReview(preauthorizedPaymentChanges);
                    progress.addAndGet(1);
                    if (canceled) {
                        break;
                    }

                }
                return null;
            }
        });
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(acceptedReviews.acceptedReviewedPaps().size());
        return status;
    }

}
