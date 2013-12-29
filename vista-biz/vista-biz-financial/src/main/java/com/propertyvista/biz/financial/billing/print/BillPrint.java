/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing.print;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.dto.BillDTO;

public class BillPrint {

    public static final String title = "Bill";

    private static JasperReportModel createModel(Bill bill) {

        BillData billData = EntityFactory.create(BillData.class);
        billData.bill().set(bill);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", title);
        return new JasperReportModel(BillPrint.class.getPackage().getName() + ".Bill", Arrays.asList(new BillData[] { billData }), parameters);
    }

    public static void printBill(BillDTO bill, OutputStream pdf) {

        Persistence.service().retrieve(bill.lineItems());
        for (InvoiceLineItem lineItem : bill.lineItems()) {
            if (InvoiceProductCharge.class.isAssignableFrom(lineItem.getClass())) {
                Persistence.service().retrieve(((InvoiceProductCharge) lineItem).chargeSubLineItem());
                Persistence.service().retrieve(((InvoiceProductCharge) lineItem).adjustmentSubLineItems());
                Persistence.service().retrieve(((InvoiceProductCharge) lineItem).concessionSubLineItems());
            }
        }

        try {
            JasperReportProcessor.createReport(BillPrint.createModel(bill), JasperFileFormat.PDF, pdf);
            pdf.flush();
        } catch (FileNotFoundException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(pdf);
        }

    }
}
