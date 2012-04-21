/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing.print;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class BillPrintScriptlet extends JRDefaultScriptlet {

    public String formatDate(LogicalDate date) throws JRScriptletException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    public String formatDays(LogicalDate fromDate, LogicalDate toDate) throws JRScriptletException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        if (fromDate != null) {
            if (toDate != null) {
                if (fromDate.equals(toDate)) {
                    return formatter.format(fromDate);
                } else {
                    return formatter.format(fromDate) + " - " + formatter.format(toDate);
                }
            } else {
                return formatter.format(fromDate);
            }
        }
        return "";
    }

    public List<InvoicePayment> getPayments(IList<InvoiceLineItem> lineItems) {
        return BillingUtils.getLineItemsForType(lineItems, InvoicePayment.class);
    }

    public List<InvoiceLineItem> getPendingLeaseAdjustment(IList<InvoiceLineItem> lineItems) {
        return getLeaseAdjustment(lineItems, LeaseAdjustment.ExecutionType.pending);
    }

    public List<InvoiceLineItem> getImmediateLeaseAdjustment(IList<InvoiceLineItem> lineItems) {
        return getLeaseAdjustment(lineItems, LeaseAdjustment.ExecutionType.immediate);
    }

    private List<InvoiceLineItem> getLeaseAdjustment(IList<InvoiceLineItem> lineItems, LeaseAdjustment.ExecutionType type) {
        List<InvoiceLineItem> adjustments = new ArrayList<InvoiceLineItem>();
        for (InvoiceAccountCharge charge : BillingUtils.getLineItemsForType(lineItems, InvoiceAccountCharge.class)) {
            if (type.equals(charge.adjustment().executionType().getValue())) {
                adjustments.add(charge);
            }
        }
        for (InvoiceAccountCredit credit : BillingUtils.getLineItemsForType(lineItems, InvoiceAccountCredit.class)) {
            if (type.equals(credit.adjustment().executionType().getValue())) {
                adjustments.add(credit);
            }
        }
        return adjustments;
    }

}
