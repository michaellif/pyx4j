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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.financial.billing.internal.BillDateUtils;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;

public class BillPrintScriptlet extends JRDefaultScriptlet {

    public String formatDate(LogicalDate date) throws JRScriptletException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    public String formatDays(InvoiceLineItem lineItem) throws JRScriptletException {
        return BillDateUtils.formatDays(lineItem);
    }

    public BigDecimal getAmountWithTax(InvoiceLineItem lineItem) throws JRScriptletException {
        if (lineItem instanceof InvoiceDebit) {
            return lineItem.amount().getValue().add(((InvoiceDebit) lineItem).taxTotal().getValue());
        } else {
            return lineItem.amount().getValue();
        }
    }
}
