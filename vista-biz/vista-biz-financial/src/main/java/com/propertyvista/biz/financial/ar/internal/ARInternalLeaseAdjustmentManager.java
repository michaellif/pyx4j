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
package com.propertyvista.biz.financial.ar.internal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.InvoiceLineItemFactory;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

class ARInternalLeaseAdjustmentManager {

    private ARInternalLeaseAdjustmentManager() {
    }

    private static class SingletonHolder {
        public static final ARInternalLeaseAdjustmentManager INSTANCE = new ARInternalLeaseAdjustmentManager();
    }

    static ARInternalLeaseAdjustmentManager instance() {
        return SingletonHolder.INSTANCE;
    }

    void postImmediateAdjustment(LeaseAdjustment adjustment) {

        if (!LeaseAdjustment.ExecutionType.immediate.equals(adjustment.executionType().getValue())) {
            throw new IllegalArgumentException("Not an immediate adjustment");
        } else {
            InvoiceLineItem lineItem = null;
            if (ARCode.Type.AccountCharge.equals(adjustment.code().type().getValue())) {
                InvoiceAccountCharge charge = InvoiceLineItemFactory.createInvoiceAccountCharge(adjustment);
                charge.dueDate().setValue(
                        ARInternalTransactionManager.instance()
                                .getTransactionDueDate(adjustment.billingAccount(), SystemDateManager.getLogicalDate()));
                lineItem = charge;
            } else if (ARCode.Type.AccountCredit.equals(adjustment.code().type().getValue())) {
                lineItem = InvoiceLineItemFactory.createInvoiceAccountCredit(adjustment);
            } else {
                throw new IllegalArgumentException("ActionType is unknown");
            }

            lineItem.billingAccount().set(adjustment.billingAccount());
            Persistence.service().persist(lineItem);

            ARInternalTransactionManager.instance().postInvoiceLineItem(lineItem);
        }
    }
}
