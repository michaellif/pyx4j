/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import java.io.ByteArrayOutputStream;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.print.BillPrint;
import com.propertyvista.domain.financial.billing.Bill;

public class BillPrintDeferredProcess implements IDeferredProcess {

    private final static I18n i18n = I18n.get(BillPrintDeferredProcess.class);

    private static final long serialVersionUID = -6125308101533877962L;

    protected volatile boolean canceled;

    private boolean compleate;

    private final Bill bill;

    public BillPrintDeferredProcess(Bill bill) {
        this.bill = bill;
    }

    @Override
    public void execute() {
        if (!compleate) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                BillPrint.printBill(BillingUtils.createBillDto(bill), bos);
                Downloadable billPrint = new Downloadable(bos.toByteArray(), Downloadable.getContentType(DownloadFormat.PDF));
                String fileName = "Bill.pdf";
                if (!canceled) {
                    billPrint.save(fileName);
                }
            } finally {
                IOUtils.closeQuietly(bos);
            }

        }
        compleate = true;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse response = new DeferredReportProcessProgressResponse();

        if (compleate) {
            response.setCompleted();
            // TODO Bill.pdf - to more readable for end user
            response.setDownloadLink(System.currentTimeMillis() + "/" + "Bill.pdf");
        } else {
            response.setMessage(i18n.tr("Creating Bill..."));
            response.setProgress(0);
        }
        return response;

    }

}
