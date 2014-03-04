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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;

import com.propertyvista.domain.financial.PaymentPostingBatch;
import com.propertyvista.domain.financial.PaymentPostingBatch.PostingStatus;
import com.propertyvista.domain.property.asset.building.Building;

class MoneyInBatchManager {

    MoneyInBatchManager() {
    }

    PaymentPostingBatch createPostingBatch(Building buildingId) {
        PaymentPostingBatch batch = EntityFactory.create(PaymentPostingBatch.class);
        batch.status().setValue(PostingStatus.Created);
        batch.building().set(buildingId);
        batch.depositDetails().merchantAccount().set(PaymentUtils.retrieveMerchantAccount(buildingId));
        Persistence.service().persist(batch);
        return batch;
    }

    void cancelPostingBatch(PaymentPostingBatch paymentPostingBatchId, RunningProcess progress) {

    }

    void processPostingBatch(PaymentPostingBatch paymentPostingBatchId, RunningProcess progress) {

    }

}
