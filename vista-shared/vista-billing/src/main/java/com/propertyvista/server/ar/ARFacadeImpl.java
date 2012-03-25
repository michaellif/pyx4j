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
package com.propertyvista.server.ar;

import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.PaymentRecord;

public class ARFacadeImpl implements ARFacade {

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        PaymentManager.postPayment(paymentRecord);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord) {
        PaymentManager.rejectPayment(paymentRecord);
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {

    }

}
