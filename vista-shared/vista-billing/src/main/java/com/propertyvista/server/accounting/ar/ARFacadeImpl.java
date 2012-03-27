/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.accounting.ar;

import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.PaymentRecord;
import com.propertyvista.server.accounting.ar.ARFacade;

public class ARFacadeImpl implements ARFacade {

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        PaymentProcessor.postPayment(paymentRecord);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord) {
        PaymentProcessor.rejectPayment(paymentRecord);
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        LeaseAdjustmentProcessor.postImmediateAdjustment(adjustment);
    }

}
