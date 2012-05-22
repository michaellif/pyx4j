/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.AbstractLeaseAdjustmentProcessor;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class ARLeaseAdjustmentProcessor extends AbstractLeaseAdjustmentProcessor {

    void postImmediateAdjustment(LeaseAdjustment adjustment) {

        if (!LeaseAdjustment.ExecutionType.immediate.equals(adjustment.executionType().getValue())) {
            throw new ARException("Not an immediate adjustment");
        } else {
            InvoiceLineItem lineItem = null;
            if (LeaseAdjustmentReason.ActionType.charge.equals(adjustment.reason().actionType().getValue())) {
                lineItem = createCharge(adjustment);
            } else if (LeaseAdjustmentReason.ActionType.credit.equals(adjustment.reason().actionType().getValue())) {
                lineItem = createCredit(adjustment);
            } else {
                throw new ARException("ActionType is unknown");
            }

            lineItem.billingAccount().set(adjustment.billingAccount());
            lineItem.consumed().setValue(false);
            Persistence.service().persist(lineItem);

            ARTransactionManager.postInvoiceLineItem(lineItem);

        }

    }

}
