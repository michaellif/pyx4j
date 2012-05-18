/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.TransactionHistoryDTO;

public interface ARFacade {

    void postInvoiceLineItem(InvoiceLineItem invoiceLineItem);

    void postPayment(PaymentRecord payment);

    void rejectPayment(PaymentRecord payment);

    void postNSFCharge(PaymentRecord paymentRecord);

    void postImmediateAdjustment(LeaseAdjustment adjustment);

    List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount);

    List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount);

    TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount);

    EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize);

    void updateArrearsHistory(BillingAccount billingAccount);

}
