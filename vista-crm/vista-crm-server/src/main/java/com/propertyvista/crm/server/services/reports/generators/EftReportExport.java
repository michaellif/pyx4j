/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.essentials.server.services.reports.ReportExporter.ExportedReport;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.rpc.dto.reports.EftReportDataDTO;
import com.propertyvista.crm.rpc.dto.reports.EftReportRecordDTO;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class EftReportExport {

    private static final I18n i18n = I18n.get(EftReportExport.class);

    public ExportedReport createReport(EftReportDataDTO eftReportData, ReportProgressStatusHolder reportProgressStatusHolder) {
        int numOfRecords = eftReportData.eftReportRecords().size();
        String stageName = i18n.tr("Preparing Excel Spreadsheet");
        reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, 0, numOfRecords));

        ReportTableXLSXFormatter formatter = new ReportTableXLSXFormatter(true);
        EntityReportFormatter<EftReportExportModel> entityFormatter = new EntityReportFormatter<EftReportExportModel>(EftReportExportModel.class);
        entityFormatter.createHeader(formatter);

        int counter = 0;

        ExportTotals<?, EftReportExportModel> totals = eftReportData.agregateByBuildings().isBooleanTrue() ? new EftReportExportBuildingTotals()
                : new EftReportOverallTotals();

        for (EftReportRecordDTO paymentRecord : eftReportData.eftReportRecords()) {
            EftReportExportModel model = convertModel(paymentRecord);

            totals.reportTotalIfKeyChanged(formatter, model.building().getValue());

            ++counter;
            entityFormatter.reportEntity(formatter, model);

            totals.addToTotal(model.building().getValue(), model);

            if (counter % 50 == 0) {
                reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, counter, numOfRecords));
            }
        }
        totals.reportLastTotal(formatter);

        return new ExportedReport("eft-report.xlsx", formatter.getContentType(), formatter.getBinaryData());
    }

    private EftReportExportModel convertModel(EftReportRecordDTO eftReportRecord) {
        EftReportExportModel model = EntityFactory.create(EftReportExportModel.class);
        PaymentRecord paymentRecord = Persistence.secureRetrieve(PaymentRecord.class, eftReportRecord.amount_().getPrimaryKey());
        Persistence.service().retrieve(paymentRecord.preauthorizedPayment().tenant());
        Persistence.service().retrieve(paymentRecord.preauthorizedPayment().tenant().lease());
        model.targetDate().setValue(paymentRecord.targetDate().getValue());

        model.building().setValue(eftReportRecord.building().getValue());
        model.unit().setValue(eftReportRecord.unit().getValue());
        model.leaseId().setValue(eftReportRecord.leaseId().getValue());

        model.leaseStatus().setValue(paymentRecord.preauthorizedPayment().tenant().lease().status().getValue());
        model.leaseFrom().setValue(paymentRecord.preauthorizedPayment().tenant().lease().leaseFrom().getValue());
        model.leaseTo().setValue(paymentRecord.preauthorizedPayment().tenant().lease().leaseTo().getValue());
        model.expectedMoveOut().setValue(eftReportRecord.expectedMoveOut().getValue());

        model.participantId().setValue(eftReportRecord.participantId().getValue());
        model.customer().setValue(eftReportRecord.customer().getStringView());

        model.amount().setValue(eftReportRecord.amount().getValue());
        model.paymentType().setValue(eftReportRecord.paymentType().getValue());

        // TODO verify permissions
        switch (eftReportRecord.paymentType().getValue()) {
        case Echeck:
            EcheckInfo echeck = paymentRecord.paymentMethod().details().duplicate(EcheckInfo.class);
            model.bankId().setValue(echeck.bankId().getValue());
            model.transitNumber().setValue(echeck.branchTransitNumber().getValue());
            if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport)) {
                model.accountNumber().setValue(echeck.accountNo().number().getValue());
            } else {
                model.accountNumber().setValue(echeck.accountNo().obfuscatedNumber().getValue());
            }
            break;
        default:
            break;
        }

        model.paymentStatus().setValue(eftReportRecord.paymentStatus().getValue());
        model.notice().setValue(eftReportRecord.notice().getValue());

        return model;
    }
}
