/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.payment.AutoPayReviewDTO;

public class AutoPayChangesReportExportBuildingTotals extends ExportTotals<AutoPayReviewDTO, AutoPayReviewDTO> {

    private static final I18n i18n = I18n.get(AutoPayChangesReportExportBuildingTotals.class);

    private final int leaseInfoColumns;

    AutoPayChangesReportExportBuildingTotals(int leaseInfoColumns) {
        this.leaseInfoColumns = leaseInfoColumns;
    }

    @Override
    protected AutoPayReviewDTO add(AutoPayReviewDTO total, AutoPayReviewDTO leaseReview) {
        if (total == null) {
            total = EntityFactory.create(AutoPayReviewDTO.class);
            total.building().setValue(leaseReview.building().getValue());
        }
        DomainUtil.nvlAddBigDecimal(total.totalSuspended().totalPrice(), leaseReview.totalSuspended().totalPrice());
        DomainUtil.nvlAddBigDecimal(total.totalSuspended().payment(), leaseReview.totalSuspended().payment());
        DomainUtil.nvlAddBigDecimal(total.totalSuggested().totalPrice(), leaseReview.totalSuggested().totalPrice());
        DomainUtil.nvlAddBigDecimal(total.totalSuggested().payment(), leaseReview.totalSuggested().payment());

        if (!total.totalSuspended().totalPrice().isNull()) {
            AutoPayChangesReportExport.calulatePercent(total.totalSuspended());
        }
        if (!total.totalSuggested().totalPrice().isNull()) {
            AutoPayChangesReportExport.calulatePercent(total.totalSuggested());
        }
        return total;
    }

    @Override
    protected void exportTotal(ReportTableXLSXFormatter formatter, String key, AutoPayReviewDTO totals) {
        formatter.header(i18n.tr("Total for Building {0}:", totals.building()));
        formatter.mergeCells(1, 5);
        formatter.cellsEmpty(4 + leaseInfoColumns, true);

        formatter.cell(totals.totalSuspended().totalPrice().getValue());
        formatter.cell(totals.totalSuspended().payment().getValue());
        formatter.cell(AutoPayChangesReportExport.prc(totals.totalSuspended().percent().getValue()));

        formatter.cell(totals.totalSuggested().totalPrice().getValue());
        formatter.createCell();
        formatter.cell(totals.totalSuggested().payment().getValue());
        formatter.cell(AutoPayChangesReportExport.prc(totals.totalSuggested().percent().getValue()));
        formatter.createCell();

        Iterator<Cell> ci = formatter.getCurentRow().cellIterator();
        while (ci.hasNext()) {
            Cell cell = ci.next();
            CellStyle style = formatter.getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }
        formatter.newRow();
    }

}
