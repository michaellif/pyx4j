/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.util.List;

import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.Lease;

public class ARInvoiceManager {

    static void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {

        invoiceLineItem.posted().setValue(true);

        if (invoiceLineItem.isAssignableFrom(InvoiceCredit.class)) {
            ARCreditDebitLinkManager.consumeCredit((InvoiceCredit) invoiceLineItem);
        } else if (invoiceLineItem.isAssignableFrom(InvoiceDebit.class)) {
            ARCreditDebitLinkManager.coverDebit((InvoiceDebit) invoiceLineItem);
        }

//        Persistence.service().persist(invoiceLineItem);

    }

    static List<InvoiceLineItem> getInvoiceLineItems(Lease lease) {
        //TODO
        return null;
    }

    static List<InvoiceLineItem> getNotCoveredDebitInvoiceLineItems(Lease lease) {
        //TODO
        return null;
    }

    static List<InvoiceLineItem> getNotConsumedCreditInvoiceLineItems(Lease lease) {
        //TODO
        return null;
    }

}
