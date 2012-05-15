/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.propertyvista.biz.financial.AbstractProcessor;
import com.propertyvista.domain.financial.billing.InvoiceLatePaymentFee;

public class BillingLatePaymentFeeProcessor extends AbstractProcessor {

    private final Billing billing;

    BillingLatePaymentFeeProcessor(Billing billing) {
        this.billing = billing;
    }

    public void createLatePaymentFeeItem() {
        // TODO Auto-generated method stub
        // InvoiceLatePaymentFee
    }

}
