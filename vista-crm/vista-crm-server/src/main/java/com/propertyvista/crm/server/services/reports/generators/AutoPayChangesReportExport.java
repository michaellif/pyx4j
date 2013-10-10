/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.essentials.server.services.reports.ReportExporter.ExportedReport;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewChargeDetailDTO;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReportExport {

    private static final I18n i18n = I18n.get(EftReportExport.class);

    private Font redBoldFont;

    private int leaseInfoColumns = 0;

    public ExportedReport createReport(List<AutoPayReviewLeaseDTO> reviewRecords, ReportProgressStatusHolder reportProgressStatusHolder) {
        int numOfRecords = reviewRecords.size();
        String stageName = i18n.tr("Preparing Excel Spreadsheet");
        reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, 0, numOfRecords));

        ReportTableXLSXFormatter formatter = new ReportTableXLSXFormatter(true);
        formatter.setAutosize(true);

        redBoldFont = formatter.getWorkbook().createFont();
        redBoldFont.setFontHeightInPoints((short) 10);
        redBoldFont.setFontName("Arial");
        redBoldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        redBoldFont.setColor(IndexedColors.RED.getIndex());

        createHeader(formatter);

        AutoPayChangesReportExportBuildingTotals buildingsTotals = new AutoPayChangesReportExportBuildingTotals(leaseInfoColumns);

        int counter = 0;
        for (AutoPayReviewLeaseDTO review : reviewRecords) {
            ++counter;

            buildingsTotals.reportTotalIfKeyChanged(formatter, review.building().getValue());

            reportEntity(formatter, review);

            buildingsTotals.addToTotal(review.building().getValue(), review);

            if (counter % 50 == 0) {
                reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, counter, numOfRecords));
            }
        }

        buildingsTotals.reportLastTotal(formatter);

        return new ExportedReport("auto-pay-changes-report.xlsx", formatter.getContentType(), formatter.getBinaryData());
    }

    private void createHeader(ReportTableXLSXFormatter formatter) {
        formatter.header(i18n.tr("Building"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Unit"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Lease ID"));
        formatter.mergeCells(2, 1);

        formatter.header(i18n.tr("Lease Status"));
        leaseInfoColumns++;
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Lease From"));
        leaseInfoColumns++;
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Lease To"));
        leaseInfoColumns++;
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Expected Move Out"));
        leaseInfoColumns++;
        formatter.mergeCells(2, 1);

        formatter.header(i18n.tr("Tenant Name"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Charge Code"));
        formatter.mergeCells(2, 1);

        formatter.header(i18n.tr("Auto Pay - Suspended"));
        formatter.mergeCells(1, 3);
        formatter.cellsEmpty(2, false);
        formatter.header(i18n.tr("Auto Pay - Suggested"));
        formatter.mergeCells(1, 4);
        formatter.cellsEmpty(3, false);
        formatter.header(i18n.tr("Payment Due"));
        formatter.mergeCells(2, 1);
        formatter.newRow();

        formatter.cellsEmpty(5 + leaseInfoColumns, false);
        formatter.header(i18n.tr("Total Price"));
        formatter.header(i18n.tr("Payment"));
        formatter.header(i18n.tr("% of Total"));

        formatter.header(i18n.tr("Total Price"));
        formatter.header(i18n.tr("% Change"));
        formatter.header(i18n.tr("Payment"));
        setCurentCellRed(formatter);
        formatter.header(i18n.tr("% of Total"));
        formatter.newRow();
    }

    private void reportEntity(ReportTableXLSXFormatter formatter, AutoPayReviewLeaseDTO reviewCase) {
        formatter.cell(reviewCase.building().getValue());
        formatter.cell(reviewCase.unit().getValue());
        formatter.cell(reviewCase.leaseId().getValue());
        formatter.cell(CommonsStringUtils.nvl_concat(reviewCase.lease().status().getValue(), reviewCase.lease().completion().getValue(), " "));
        formatter.cell(reviewCase.lease().leaseFrom().getValue());
        formatter.cell(reviewCase.lease().leaseTo().getValue());
        formatter.cell(reviewCase.lease().expectedMoveOut().getValue());

        boolean isFirstLine = true;

        for (AutoPayReviewPreauthorizedPaymentDTO reviewPap : reviewCase.pap()) {
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                formatter.cellsEmpty(3 + leaseInfoColumns, false);
            }
            formatter.cell(reviewPap.tenantName().getValue());

            boolean isFirstCharge = true;
            for (AutoPayReviewChargeDTO charge : reviewPap.items()) {
                if (!isFirstCharge) {
                    formatter.cellsEmpty(4 + leaseInfoColumns, false);
                }

                formatter.cell(charge.leaseCharge().getValue());

                formatter.cell(charge.previous().totalPrice().getValue());
                formatter.cell(charge.previous().payment().getValue());
                formatter.cell(prc(charge.previous().percent().getValue()));

                formatter.cell(charge.current().totalPrice().getValue());

                if (charge.current().billableItem().isNull()) {
                    formatter.cell("Removed");
                } else if (charge.current().percentChange().isNull()) {
                    formatter.cell("New");
                } else {
                    formatter.cell(prc(charge.current().percentChange().getValue()));
                }

                formatter.cell(charge.current().payment().getValue());
                if (!charge.current().payment().isNull()) {
                    setCurentCellRed(formatter);
                }

                formatter.cell(prc(charge.current().percent().getValue()));

                if (isFirstCharge) {
                    formatter.cell(reviewCase.paymentDue().getValue());
                    isFirstCharge = false;
                }
                formatter.newRow();
            }
        }

        // add summary for lease
        formatter.cellsEmpty(2, false);
        formatter.header(i18n.tr("Total for lease:"));
        formatter.mergeCells(1, 3);
        formatter.cellsEmpty(2 + leaseInfoColumns, true);

        formatter.cell(reviewCase.totalPrevious().totalPrice().getValue());
        formatter.cell(reviewCase.totalPrevious().payment().getValue());
        formatter.cell(prc(reviewCase.totalPrevious().percent().getValue()));

        formatter.cell(reviewCase.totalCurrent().totalPrice().getValue());
        formatter.createCell();
        formatter.cell(reviewCase.totalCurrent().payment().getValue());
        formatter.cell(prc(reviewCase.totalCurrent().percent().getValue()));
        formatter.createCell();

        Iterator<Cell> ci = formatter.getCurentRow().cellIterator();
        while (ci.hasNext()) {
            Cell cell = ci.next();
            CellStyle style = formatter.getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }

        formatter.newRow();

    }

    private void setCurentCellRed(ReportTableXLSXFormatter formatter) {
        CellStyle style = formatter.getWorkbook().createCellStyle();
        style.cloneStyleFrom(formatter.getCurentCell().getCellStyle());
        style.setFont(redBoldFont);
        formatter.getCurentCell().setCellStyle(style);
    }

    static void calulatePercent(AutoPayReviewChargeDetailDTO chargeDetail) {
        if (chargeDetail.totalPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            if (!chargeDetail.payment().isNull()) {
                chargeDetail.percent().setValue(chargeDetail.payment().getValue().divide(chargeDetail.totalPrice().getValue(), 4, RoundingMode.FLOOR));
            }
        } else {
            chargeDetail.percent().setValue(BigDecimal.ZERO);
        }
    }

    static String prc(BigDecimal value) {
        if (value == null) {
            return null;
        } else {
            NumberFormat nf = new DecimalFormat("#.##");
            return nf.format(value.multiply(new BigDecimal("100"))) + "%";
        }
    }
}
