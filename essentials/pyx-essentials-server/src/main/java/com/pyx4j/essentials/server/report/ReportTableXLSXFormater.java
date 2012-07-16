/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-07-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Stack;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18n;

public class ReportTableXLSXFormater implements ReportTableFormater {

    private static final long serialVersionUID = 7937142277508559591L;

    private static final I18n i18n = I18n.get(ReportTableXLSXFormater.class);

    private final Workbook workbook;

    private Sheet curentSheet = null;

    private Row curentRow = null;

    private short rowIdx = 0;

    private short cellIdx = 0;

    protected final CellStyle cellStyleColumnHeading;

    protected final CellStyle cellStyleColumnHeading2;

    protected final CellStyle cellStyleDefault;

    protected final CellStyle cellStyleDate;

    protected final CellStyle dollarStyle;

    private Stack<GroupStart> groupRows = null;

    private int columnsCount;

    private boolean autosize = true;

    public ReportTableXLSXFormater() {
        workbook = new XSSFWorkbook();

        //Create column heading0 font.
        Font columnHeading2Font = this.workbook.createFont();
        columnHeading2Font.setFontHeightInPoints((short) 12);
        columnHeading2Font.setFontName("Arial");
        columnHeading2Font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        this.cellStyleColumnHeading2 = this.workbook.createCellStyle();
        this.cellStyleColumnHeading2.setFont(columnHeading2Font);

        //Create column heading font.
        Font columnHeadingFont = this.workbook.createFont();
        columnHeadingFont.setFontHeightInPoints((short) 10);
        columnHeadingFont.setFontName("Arial");
        columnHeadingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        this.cellStyleColumnHeading = this.workbook.createCellStyle();
        this.cellStyleColumnHeading.setFont(columnHeadingFont);

        //Create default workbook font.
        Font font = this.workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        this.cellStyleDefault = this.workbook.createCellStyle();
        this.cellStyleDefault.setFont(font);

        this.cellStyleDate = this.workbook.createCellStyle();
        this.cellStyleDate.setFont(font);
        this.cellStyleDate.setDataFormat((short) BuiltinFormats.getBuiltinFormat("m/d/yy"));

        // N.B. "mm/dd/yyyy" is not working because it is not build in Excel
        // format.
        // cellStyleDate.setDataFormat(HSSFDataFormat.getFormat("m/d/yy"));
        // Use text for now ...

        // Create currency style
        this.dollarStyle = this.workbook.createCellStyle();
        this.dollarStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        //Not working
        //this.dollarStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("currency"));
        DataFormat format = workbook.createDataFormat();
        this.dollarStyle.setDataFormat(format.getFormat("$* #,##0.00"));
    }

    public boolean isAutosize() {
        return autosize;
    }

    public void setAutosize(boolean autosize) {
        this.autosize = autosize;
    }

    @Override
    public void header(String text) {
        if (!CommonsStringUtils.isStringSet(text)) {
            this.cellIdx++;
            return;
        }
        if (this.curentRow == null) {
            newRow();
        }
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        cell.setCellStyle(this.cellStyleColumnHeading);
        cell.setCellValue(new XSSFRichTextString(text));
    }

    public void header2Cell(String text) {
        if (!CommonsStringUtils.isStringSet(text)) {
            this.cellIdx++;
            return;
        }
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        cell.setCellStyle(this.cellStyleColumnHeading2);
        cell.setCellValue(new XSSFRichTextString(text));
    }

    public void newSheet(String sheetName) {
        this.curentSheet = this.workbook.createSheet(sheetName);
    }

    @Override
    public void newRow() {
        if (this.curentSheet == null) {
            newSheet("new sheet");
        }
        this.curentRow = curentSheet.createRow(rowIdx++);
        if (columnsCount < this.cellIdx) {
            columnsCount = this.cellIdx;
        }
        this.cellIdx = 0;
    }

    public void cellEmpty() {
        this.cellIdx++;
    }

    public void cellEmpty(int count) {
        this.cellIdx += count;
    }

    @Override
    public void cell(Object value) {
        if (value == null) {
            cellEmpty();
        } else if (value instanceof String) {
            cell((String) value);
        } else if (value instanceof Date) {
            cell((Date) value);
        } else if (value instanceof Boolean) {
            cell(((Boolean) value).booleanValue());
        } else if (value instanceof Double) {
            cell(((Double) value).doubleValue());
        } else if (value instanceof Number) {
            cell(((Number) value).longValue());
        } else {
            cell(value.toString());
        }
    }

    public void cell(String value) {
        if (!CommonsStringUtils.isStringSet(value)) {
            this.cellIdx++;
            return;
        }
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        cell.setCellStyle(this.cellStyleDefault);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(new XSSFRichTextString(value));
    }

    public void cell(double value) {
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        cell.setCellStyle(this.cellStyleDefault);
        cell.setCellValue(value);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
    }

    public void cell(long value) {
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        cell.setCellStyle(this.cellStyleDefault);
        cell.setCellValue(value);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
    }

    public void cell(boolean value) {
        cell(value ? yesText() : noText());
    }

    @I18nComment("As an answer to a question")
    protected String noText() {
        return i18n.tr("No");
    }

    @I18nComment("As an answer to a question")
    protected String yesText() {
        return i18n.tr("Yes");
    }

    public void cell(Date value) {
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        // TODO: fix cellStyleDate
        cell.setCellStyle(this.cellStyleDate);
        cell.setCellValue(value);
    }

    public void cell(double value, int fractionDigits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractionDigits);
        nf.setMinimumFractionDigits(fractionDigits);
        cell(nf.format(value));
    }

    public void cellCurrency(double value) {
        Cell cell = this.curentRow.createCell(this.cellIdx++);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(this.dollarStyle);
        cell.setCellValue(value);
    }

    public void autosizeColumns() {
        for (int c = 0; c < columnsCount; c++) {
            curentSheet.autoSizeColumn(c);
        }
    }

    @Override
    public int getBinaryDataSize() {
        return -1;
    }

    @Override
    public byte[] getBinaryData() {
        if (isAutosize()) {
            autosizeColumns();
        }
        verify();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            this.workbook.write(out);
        } catch (IOException e) {
            throw new Error(e);
        }
        return out.toByteArray();
    }

    @Override
    public String getContentType() {
        return MimeMap.getContentType(DownloadFormat.XLSX);
    }

    protected void verify() throws Error {
        if ((this.groupRows != null) && (this.groupRows.size() != 0)) {
            throw new Error("groupRow should be closed");
        }
    }

    private static class GroupStart {
        int start;

        boolean collapsed;

        public GroupStart(int start, boolean collapsed) {
            this.start = start;
            this.collapsed = collapsed;
        }
    }

    /*
     * 
     * @see http://jakarta.apache.org/poi/hssf/quick-guide.html#Outlining
     */
    public void groupRowStart(boolean collapsed) {
        if (this.groupRows == null) {
            this.groupRows = new Stack<GroupStart>();
        }
        this.groupRows.push(new GroupStart(rowIdx, collapsed));
    }

    public void groupRowEnd() {
        if ((this.groupRows == null) || (this.groupRows.size() == 0)) {
            throw new Error("groupRow should be started first");
        }
        GroupStart groupStart = this.groupRows.pop();
        this.curentSheet.groupRow(groupStart.start, rowIdx);
        if (groupStart.collapsed) {
            this.curentSheet.setRowGroupCollapsed(groupStart.start, true);
        }
    }
}
