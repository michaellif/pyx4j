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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.essentials.server.services.reports.ReportExporter.ExportedReport;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewChargeDetailDTO;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReportExport {

    private static final I18n i18n = I18n.get(EftReportExport.class);

    public ExportedReport createReport(List<AutoPayReviewDTO> reviewRecords, ReportProgressStatusHolder reportProgressStatusHolder) {
        int numOfRecords = reviewRecords.size();
        String stageName = i18n.tr("Preparing Excel Spreadsheet");
        reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, 0, numOfRecords));

        ReportTableXLSXFormatter formatter = new ReportTableXLSXFormatter(true);
        createHeader(formatter);

        Map<String, AutoPayReviewDTO> buildingsTotals = new HashMap<String, AutoPayReviewDTO>();
        String currentBuilding = null;

        int counter = 0;
        for (AutoPayReviewDTO review : reviewRecords) {
            ++counter;

            if ((currentBuilding != null) && !currentBuilding.equals(review.building().getValue())) {
                reportBuildingTotals(formatter, buildingsTotals.get(currentBuilding));
            }

            reportEntity(formatter, review);

            addBuildingTotals(buildingsTotals, review);
            currentBuilding = review.building().getValue();

            if (counter % 50 == 0) {
                reportProgressStatusHolder.set(new ReportProgressStatus(stageName, 2, 2, counter, numOfRecords));
            }
        }

        if (currentBuilding != null) {
            reportBuildingTotals(formatter, buildingsTotals.get(currentBuilding));
        }

        return new ExportedReport("auto-pay-changes-report.xlsx", formatter.getContentType(), formatter.getBinaryData());
    }

    private void createHeader(ReportTableXLSXFormatter formatter) {
        formatter.header(i18n.tr("Building"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Unit"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Lease ID"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Tenant Name"));
        formatter.mergeCells(2, 1);
        formatter.header(i18n.tr("Charge Code"));
        formatter.mergeCells(2, 1);

        formatter.header(i18n.tr("Auto Pay Suspended"));
        formatter.mergeCells(1, 3);
        formatter.cellEmpty(2);
        formatter.header(i18n.tr("Auto Pay Suggested"));
        formatter.mergeCells(1, 4);
        formatter.cellEmpty(3);
        formatter.header(i18n.tr("Payment Due"));
        formatter.mergeCells(2, 1);
        formatter.newRow();

        formatter.cellEmpty(5);
        formatter.header(i18n.tr("Total Price"));
        formatter.header(i18n.tr("Payment"));
        formatter.header(i18n.tr("% of Total"));

        formatter.header(i18n.tr("Total Price"));
        formatter.header(i18n.tr("% Change"));
        formatter.header(i18n.tr("Payment"));
        formatter.header(i18n.tr("% of Total"));
        formatter.newRow();
    }

    private void reportEntity(ReportTableXLSXFormatter formatter, AutoPayReviewDTO reviewCase) {
        formatter.cell(reviewCase.building().getValue());
        formatter.cell(reviewCase.unit().getValue());
        formatter.cell(reviewCase.leaseId().getValue());

        boolean isFirstLine = true;

        for (AutoPayReviewPreauthorizedPaymentDTO reviewPap : reviewCase.pap()) {
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                formatter.cellEmpty(3);
            }
            formatter.cell(reviewPap.tenantName().getValue());

            boolean isFirstCharge = true;
            for (AutoPayReviewChargeDTO charge : reviewPap.items()) {
                if (!isFirstCharge) {
                    formatter.cellEmpty(4);
                }

                formatter.cell(charge.leaseCharge().getValue());

                formatter.cell(charge.suspended().totalPrice().getValue());
                formatter.cell(charge.suspended().payment().getValue());
                formatter.cell(prc(charge.suspended().percent().getValue()));

                formatter.cell(charge.suggested().totalPrice().getValue());

                if (charge.suggested().percentChange().isNull()) {
                    formatter.cell("New");
                } else if (charge.suggested().billableItem().isNull()) {
                    formatter.cell("Removed item");
                } else {
                    formatter.cell(prc(charge.suggested().percentChange().getValue()));
                }

                formatter.cell(charge.suggested().payment().getValue());
                formatter.cell(prc(charge.suggested().percent().getValue()));

                if (isFirstCharge) {
                    formatter.cell(reviewCase.paymentDue().getValue());
                    isFirstCharge = false;
                }
                formatter.newRow();
            }
        }

        // add summary for lease
        formatter.cellEmpty(2);
        formatter.header(i18n.tr("Total for lease:"));
        formatter.mergeCells(1, 3);
        formatter.cellEmpty(2);

        formatter.cell(reviewCase.totalSuspended().totalPrice().getValue());
        formatter.cell(reviewCase.totalSuspended().payment().getValue());
        formatter.cell(prc(reviewCase.totalSuspended().percent().getValue()));

        formatter.cell(reviewCase.totalSuggested().totalPrice().getValue());
        formatter.cellEmpty();
        formatter.cell(reviewCase.totalSuggested().payment().getValue());
        formatter.cell(prc(reviewCase.totalSuggested().percent().getValue()));
        formatter.newRow();

    }

    private void addBuildingTotals(Map<String, AutoPayReviewDTO> buildingsTotals, AutoPayReviewDTO leaseReview) {
        AutoPayReviewDTO totals = buildingsTotals.get(leaseReview.building().getValue());
        if (totals == null) {
            totals = EntityFactory.create(AutoPayReviewDTO.class);
            totals.building().setValue(leaseReview.building().getValue());
            buildingsTotals.put(leaseReview.building().getValue(), totals);
        }

        DomainUtil.nvlAddBigDecimal(totals.totalSuspended().totalPrice(), leaseReview.totalSuspended().totalPrice());
        DomainUtil.nvlAddBigDecimal(totals.totalSuspended().payment(), leaseReview.totalSuspended().payment());
        DomainUtil.nvlAddBigDecimal(totals.totalSuggested().totalPrice(), leaseReview.totalSuggested().totalPrice());
        DomainUtil.nvlAddBigDecimal(totals.totalSuggested().payment(), leaseReview.totalSuggested().payment());

        if (!totals.totalSuspended().totalPrice().isNull()) {
            calulatePercent(totals.totalSuspended());
        }
        if (!totals.totalSuggested().totalPrice().isNull()) {
            calulatePercent(totals.totalSuggested());
        }
    }

    private void calulatePercent(AutoPayReviewChargeDetailDTO chargeDetail) {
        if (chargeDetail.totalPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            chargeDetail.percent().setValue(chargeDetail.payment().getValue().divide(chargeDetail.totalPrice().getValue(), 2, RoundingMode.FLOOR));
        } else {
            chargeDetail.percent().setValue(BigDecimal.ZERO);
        }
    }

    private void reportBuildingTotals(ReportTableXLSXFormatter formatter, AutoPayReviewDTO totals) {
        formatter.header(i18n.tr("Total for Building {0}:", totals.building()));
        formatter.mergeCells(1, 4);
        formatter.cellEmpty(4);

        formatter.cell(totals.totalSuspended().totalPrice().getValue());
        formatter.cell(totals.totalSuspended().payment().getValue());
        formatter.cell(prc(totals.totalSuspended().percent().getValue()));

        formatter.cell(totals.totalSuggested().totalPrice().getValue());
        formatter.cellEmpty();
        formatter.cell(totals.totalSuggested().payment().getValue());
        formatter.cell(prc(totals.totalSuggested().percent().getValue()));

        formatter.newRow();

    }

    public String prc(BigDecimal value) {
        if (value == null) {
            return null;
        } else {
            NumberFormat nf = new DecimalFormat("#.##");
            return nf.format(value.multiply(new BigDecimal("100"))) + "%";
        }
    }
}
