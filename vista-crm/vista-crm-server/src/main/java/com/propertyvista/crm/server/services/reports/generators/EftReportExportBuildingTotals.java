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

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.i18n.shared.I18n;

public class EftReportExportBuildingTotals extends ExportTotals<BigDecimal, EftReportExportModel> {

    private static final I18n i18n = I18n.get(EftReportExportBuildingTotals.class);

    private BigDecimal reportTotal = BigDecimal.ZERO;

    @Override
    protected BigDecimal add(BigDecimal total, EftReportExportModel entity) {
        reportTotal = reportTotal.add(entity.amount().getValue());

        BigDecimal newTotal;
        if (total == null) {
            newTotal = entity.amount().getValue();
        } else {
            newTotal = total.add(entity.amount().getValue());
        }

        return newTotal;
    }

    @Override
    protected void exportTotal(ReportTableXLSXFormatter formatter, String key, BigDecimal total) {
        formatter.cellsEmpty(1, true);
        formatter.header(i18n.tr("Building {0} Total:", key));
        formatter.mergeCells(1, 9);
        formatter.cellsEmpty(8, true);

        formatter.cell(total);

        formatter.cellsEmpty(5, true);

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

    @Override
    public void reportLastTotal(ReportTableXLSXFormatter formatter) {
        super.reportLastTotal(formatter);

        formatter.header(i18n.tr("Total:"));
        formatter.mergeCells(1, 10);
        formatter.cellsEmpty(9, true);

        formatter.cell(reportTotal);

        formatter.cellsEmpty(5, true);

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
