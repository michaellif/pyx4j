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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.PaymentRecord;

public class PadProcessor {

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
