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

import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.essentials.server.services.reports.ReportExporter.ExportedReport;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class EftReportExport {

    private static final I18n i18n = I18n.get(EftReportExport.class);

    public ExportedReport createReport(List<PaymentRecord> paymentRecords, ReportProgressStatusHolder reportProgressStatusHolder) {
        int numOfRecords = paymentRecords.size();
        String stageName = i18n.tr("Preparing Excel Spreadsheet");
        reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, 0, numOfRecords));

        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
        EntityReportFormatter<EftReportExportModel> entityFormatter = new EntityReportFormatter<EftReportExportModel>(EftReportExportModel.class);
        entityFormatter.createHeader(formatter);

        int counter = 0;
        for (PaymentRecord paymentRecord : paymentRecords) {
            ++counter;
            entityFormatter.reportEntity(formatter, convertModel(paymentRecord));
            if (counter % 50 == 0) {
                reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, counter, numOfRecords));
            }
        }

        return new ExportedReport("eft-report.xlsx", formatter.getContentType(), formatter.getBinaryData());
    }

    private EftReportExportModel convertModel(PaymentRecord paymentRecord) {
        EftReportExportModel model = EntityFactory.create(EftReportExportModel.class);

        model.targetDate().setValue(paymentRecord.targetDate().getValue());

        model.building().setValue(paymentRecord.preauthorizedPayment().tenant().lease().unit().building().propertyCode().getValue());
        model.unit().setValue(paymentRecord.preauthorizedPayment().tenant().lease().unit().info().number().getValue());
        model.leaseId().setValue(paymentRecord.preauthorizedPayment().tenant().lease().leaseId().getValue());

        model.leaseStatus().setValue(paymentRecord.preauthorizedPayment().tenant().lease().status().getValue());
        model.leaseFrom().setValue(paymentRecord.preauthorizedPayment().tenant().lease().leaseFrom().getValue());
        model.leaseTo().setValue(paymentRecord.preauthorizedPayment().tenant().lease().leaseTo().getValue());
        model.expectedMoveOut().setValue(paymentRecord.preauthorizedPayment().tenant().lease().expectedMoveOut().getValue());

        model.tenantId().setValue(paymentRecord.preauthorizedPayment().tenant().participantId().getValue());

        model.amount().setValue(paymentRecord.amount().getValue());
        model.paymentType().setValue(paymentRecord.paymentMethod().type().getValue());

        // TODO verify permissions
        switch (paymentRecord.paymentMethod().type().getValue()) {
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

        model.paymentStatus().setValue(paymentRecord.paymentStatus().getValue());
        model.notice().setValue(paymentRecord.notice().getValue());

        return model;
    }
}
