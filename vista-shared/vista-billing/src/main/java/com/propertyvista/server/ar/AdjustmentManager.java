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
package com.propertyvista.server.ar;

import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class AdjustmentManager {

    static void postImmediateAdjustment(LeaseAdjustment adjustment) {

        if (!LeaseAdjustment.ExecutionType.immediate.equals(adjustment.actionType().getValue())) {
            throw new ARException("Not an immediate adjustment");
        } else {
            if (LeaseAdjustment.ActionType.charge.equals(adjustment.actionType().getValue())) {

//            InvoicePayment invoicePayment = EntityFactory.create(InvoicePayment.class);
//            invoicePayment.paymentRecord().set(paymentRecord);
//            invoicePayment.amount().setValue(paymentRecord.amount().getValue());
//            Persistence.service().persist(invoicePayment);
//
//            Persistence.service().retrieve(paymentRecord.billingAccount());
//            Persistence.service().retrieve(paymentRecord.billingAccount().interimLineItems());
//
//            paymentRecord.billingAccount().interimLineItems().add(invoicePayment);
//
//            Persistence.service().persist(paymentRecord.billingAccount());
//            Persistence.service().commit();
            } else if (LeaseAdjustment.ActionType.credit.equals(adjustment.actionType().getValue())) {
            }
        }

    }

}
