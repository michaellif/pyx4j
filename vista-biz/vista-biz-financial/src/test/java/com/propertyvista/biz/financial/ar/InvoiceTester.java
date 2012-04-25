/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;

import com.propertyvista.biz.financial.Tester;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.InvoiceDTO;

public class InvoiceTester extends Tester {

    private final InvoiceDTO invoice;

    public InvoiceTester(Lease lease) {
        invoice = ARFinancialTransactionManager.getInvoice(lease);
    }

    public InvoiceTester currentAmount(BigDecimal amount, int index) {
        assertEquals("Total current amount", amount, invoice.agingBuckets().get(index).current().getValue());
        return this;
    }

}
