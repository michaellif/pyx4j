/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-24
 * @author ArtyomB
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

public class EftReportOverallTotals extends ExportTotals<BigDecimal, EftReportExportModel> {

    private final static I18n i18n = I18n.get(EftReportOverallTotals.class);

    private BigDecimal reportTotal = BigDecimal.ZERO;

    @Override
    protected BigDecimal add(BigDecimal total, EftReportExportModel entity) {
        reportTotal = reportTotal.add(entity.amount().getValue());
        return reportTotal;
    }

    @Override
    protected void exportTotal(ReportTableXLSXFormatter formatter, String key, BigDecimal total) {
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

    @Override
    public void reportTotalIfKeyChanged(ReportTableXLSXFormatter formatter, String key) {
        // this was intentionally left blank to do nothing
    }

}
