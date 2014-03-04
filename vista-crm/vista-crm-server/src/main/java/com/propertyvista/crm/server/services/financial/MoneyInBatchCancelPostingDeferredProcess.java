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
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentPostingBatch;

public class MoneyInBatchCancelPostingDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = -1298706286344254304L;

    private static final Logger log = LoggerFactory.getLogger(MoneyInBatchCancelPostingDeferredProcess.class);

    private Throwable error;

    private final Key batchId;

    public MoneyInBatchCancelPostingDeferredProcess(Key batchId) {
        this.batchId = batchId;
    }

    @Override
    public void execute() {
        try {
            ServerSideFactory.create(PaymentFacade.class).cancelPostingBatch(EntityFactory.createIdentityStub(PaymentPostingBatch.class, batchId), progress);
        } catch (Throwable e) {
            log.error("Failed to post the batch", e);
            error = e;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        if (error != null) {
            status.setError();
            status.setMessage(error.getMessage());
        }
        return status;
    }

}
