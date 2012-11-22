/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ExternalBillingFacade;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;

public class ExternalTestBase extends FinancialTestBase {

    // this method is a duplicate of ReceivableFacade#postTransaction(TransactionIO) to avoid circular package dependency;
    // please keep this up to date
    public void postExternalCharge(String amount, String description, String fromDate, String toDate) {
        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);
        charge.amount().setValue(new BigDecimal(amount));
        charge.description().setValue(description);
        charge.fromDate().setValue(FinancialTestsUtils.getDate(fromDate));
        charge.toDate().setValue(FinancialTestsUtils.getDate(toDate));
        charge.taxTotal().setValue(new BigDecimal("0.00"));

        if (ServerSideFactory.create(ExternalBillingFacade.class).reconcileCharge(charge, retrieveLease().leaseId().getValue())) {
            Persistence.service().persist(charge);
        }
    }
}
