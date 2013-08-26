/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.pad;

import java.util.List;

import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;

import com.propertyvista.interfaces.importer.model.PadFileModel;

public class TenantPadCreateReport {

    private final ReportTableFormatter formatter;

    private final EntityReportFormatter<PadFileReportModel> entityFormatter;

    public TenantPadCreateReport() {
        formatter = new ReportTableXLSXFormatter(true);
        entityFormatter = new EntityReportFormatter<PadFileReportModel>(PadFileReportModel.class);
    }

    public void createReport(List<PadFileModel> model) {
        entityFormatter.createHeader(formatter);

        for (PadFileModel data : model) {
            PadFileReportModel reportModel = data.duplicate(PadFileReportModel.class);

            reportModel.invalid().setValue(reportModel._import().invalid().getValue());
            reportModel.message().setValue(reportModel._import().message().getValue());

            reportModel.status().setValue(reportModel._processorInformation().status().getValue());

            if (!reportModel._processorInformation().percent().isNull()) {
                reportModel.percentStored().setValue(reportModel._processorInformation().percent().getValue().doubleValue() * 100);
            }
            reportModel.actualChargeCodeAmount().setValue(reportModel._processorInformation().actualChargeCodeAmount().getValue());

            if (!reportModel.invalid().isBooleanTrue()) {
                StringBuilder amountStored = new StringBuilder();
                for (PadFileModel charge : data._processorInformation().accountCharges()) {
                    if (amountStored.length() > 0) {
                        amountStored.append(", ");
                    }
                    amountStored.append(charge.chargeCode().getValue()).append(":");
                    amountStored.append(charge._processorInformation().chargeEftAmount().getValue().toString());
                }
                reportModel.amountStored().setValue(amountStored.toString());
            }

            entityFormatter.reportEntity(formatter, reportModel);
        }
    }

    public void createDownloadable(String fileName) {
        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        d.save(fileName);
    }
}
