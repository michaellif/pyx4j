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
        DomainUtil.nvlAddBigDecimal(total.totalPrevious().totalPrice(), leaseReview.totalPrevious().totalPrice());
        DomainUtil.nvlAddBigDecimal(total.totalPrevious().payment(), leaseReview.totalPrevious().payment());
        DomainUtil.nvlAddBigDecimal(total.totalCurrent().totalPrice(), leaseReview.totalCurrent().totalPrice());
        DomainUtil.nvlAddBigDecimal(total.totalCurrent().payment(), leaseReview.totalCurrent().payment());

        if (!total.totalPrevious().totalPrice().isNull()) {
            AutoPayChangesReportExport.calulatePercent(total.totalPrevious());
        }
        if (!total.totalCurrent().totalPrice().isNull()) {
            AutoPayChangesReportExport.calulatePercent(total.totalCurrent());
        }
        return total;
    }

    @Override
    protected void exportTotal(ReportTableXLSXFormatter formatter, String key, AutoPayReviewDTO totals) {
        formatter.header(i18n.tr("Total for Building {0}:", totals.building()));
        formatter.mergeCells(1, 5);
        formatter.cellsEmpty(4 + leaseInfoColumns, true);

        formatter.cell(totals.totalPrevious().totalPrice().getValue());
        formatter.cell(totals.totalPrevious().payment().getValue());
        formatter.cell(AutoPayChangesReportExport.prc(totals.totalPrevious().percent().getValue()));

        formatter.cell(totals.totalCurrent().totalPrice().getValue());
        formatter.createCell();
        formatter.cell(totals.totalCurrent().payment().getValue());
        formatter.cell(AutoPayChangesReportExport.prc(totals.totalCurrent().percent().getValue()));
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
