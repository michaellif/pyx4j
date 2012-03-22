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
package com.propertyvista.server.billing.print;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.billing._InvoiceLineItem;
import com.propertyvista.domain.financial.billing._InvoiceProductCharge;
import com.propertyvista.server.billing.BillingUtils;

public class BillPrintScriptlet extends JRDefaultScriptlet {

    public String formatDate(LogicalDate date) throws JRScriptletException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    public String formatDay(LogicalDate date) throws JRScriptletException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        return formatter.format(date);
    }

    public List<_InvoiceProductCharge> getServiceCharges(IList<_InvoiceLineItem> lineItems) {
        List<_InvoiceProductCharge> charges = BillingUtils.getLineItemsForType(lineItems, _InvoiceProductCharge.class);
        List<_InvoiceProductCharge> filteredCharges = new ArrayList<_InvoiceProductCharge>();
        for (_InvoiceProductCharge charge : charges) {
            if (BillingUtils.isService(charge.chargeSubLineItem().billableItem().item().product())) {
                filteredCharges.add(charge);
            }
        }
        return filteredCharges;
    }

    public List<_InvoiceProductCharge> getFeatureRecurringCharges(IList<_InvoiceLineItem> lineItems) {
        List<_InvoiceProductCharge> charges = BillingUtils.getLineItemsForType(lineItems, _InvoiceProductCharge.class);
        List<_InvoiceProductCharge> filteredCharges = new ArrayList<_InvoiceProductCharge>();
        for (_InvoiceProductCharge charge : charges) {
            if (BillingUtils.isRecurringFeature(charge.chargeSubLineItem().billableItem().item().product())) {
                filteredCharges.add(charge);
            }
        }
        return filteredCharges;
    }

    public List<_InvoiceProductCharge> getFeatureOneTimeCharges(IList<_InvoiceLineItem> lineItems) {
        List<_InvoiceProductCharge> charges = BillingUtils.getLineItemsForType(lineItems, _InvoiceProductCharge.class);
        List<_InvoiceProductCharge> filteredCharges = new ArrayList<_InvoiceProductCharge>();
        for (_InvoiceProductCharge charge : charges) {
            if (BillingUtils.isOneTimeFeature(charge.chargeSubLineItem().billableItem().item().product())) {
                filteredCharges.add(charge);
            }
        }
        return filteredCharges;
    }

}
