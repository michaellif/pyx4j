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
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Stack;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18n;

public class ReportTableXLSXFormatter implements ReportTableFormatter {

    private static final long serialVersionUID = 7937142277508559591L;

    private static final I18n i18n = I18n.get(ReportTableXLSXFormatter.class);

    private final boolean xlsx;

    private final Workbook workbook;

    private Sheet curentSheet = null;

    private Row curentRow = null;

    private int rowIdx = 0;

    private int cellIdx = 0;

    protected final CellStyle cellStyleColumnHeading;

    protected final CellStyle cellStyleColumnHeading2;

    protected final CellStyle cellStyleDefault;

    protected final CellStyle cellStyleDate;

    protected final CellStyle cellStyleDateTime;

    protected final CellStyle cellStyleDollar;

    protected final CellStyle cellStyleInteger;

    protected final CellStyle cellStyleDouble;

    private Stack<GroupStart> groupRows = null;

    private int columnsCount;

    private boolean autosize = true;

    private int rowCount = 0;

    public ReportTableXLSXFormatter() {
        this(true);
    }

    public ReportTableXLSXFormatter(InputStream is, boolean xlsx) throws IOException {
        this(loadWorkbook(is, xlsx), xlsx);
    }

    public ReportTableXLSXFormatter(boolean xlsx) {
        this(xlsx ? new XSSFWorkbook() : new HSSFWorkbook(), xlsx);
    }

    private ReportTableXLSXFormatter(Workbook workbook, boolean xlsx) {
        this.xlsx = xlsx;
        this.workbook = workbook;

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

        // N.B. "mm/dd/yyyy" is not working because it is not build in Excel

        this.cellStyleDate = this.workbook.createCellStyle();
        this.cellStyleDate.setFont(font);
        this.cellStyleDate.setDataFormat((short) BuiltinFormats.getBuiltinFormat("m/d/yy"));

        this.cellStyleDateTime = this.workbook.createCellStyle();
        this.cellStyleDateTime.setFont(font);
        this.cellStyleDateTime.setDataFormat((short) BuiltinFormats.getBuiltinFormat("m/d/yy h:mm"));

        // Create currency style
        this.cellStyleDollar = this.workbook.createCellStyle();
        this.cellStyleDollar.setAlignment(CellStyle.ALIGN_RIGHT);
        //Not working
        //this.dollarStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("currency"));
        DataFormat format = workbook.createDataFormat();
        this.cellStyleDollar.setDataFormat(format.getFormat("_-\"$\"* #,##0.00_-;\\-\"$\"* #,##0.00_-;_-\"$\"* \"-\"??_-;_-@_-"));

        this.cellStyleInteger = this.workbook.createCellStyle();
        this.cellStyleInteger.setFont(font);
        this.cellStyleInteger.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));

        this.cellStyleDouble = this.workbook.createCellStyle();
        this.cellStyleDouble.setFont(font);
        this.cellStyleDouble.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
    }

    public boolean isAutosize() {
        return autosize;
    }

    public void setAutosize(boolean autosize) {
        this.autosize = autosize;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public Sheet getCurentSheet() {
        return curentSheet;
    }

    public int getCurentRowIdx() {
        return rowIdx - 1;
    }

    public Row getCurentRow() {
        return this.curentRow;
    }

    public int getCurentCellIdx() {
        return cellIdx - 1;
    }

    public Cell getCurentCell() {
        return this.curentRow.getCell(getCurentCellIdx());
    }

    public void newSheet(String sheetName) {
        this.curentSheet = this.workbook.createSheet(sheetName);
    }

    @Override
    public void newRow() {
        if (this.curentSheet == null) {
            newSheet("Data");
        }
        this.curentRow = curentSheet.createRow(rowIdx++);
        if (columnsCount < this.cellIdx) {
            columnsCount = this.cellIdx;
        }
        this.cellIdx = 0;
        rowCount++;
    }

    public static Workbook loadWorkbook(InputStream is, boolean xlsx) throws IOException {
        if (xlsx) {
            return new XSSFWorkbook(is);
        } else {
            return new HSSFWorkbook(is);
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    public void cellEmpty() {
        this.cellIdx++;
    }

    public void cellsEmpty(int count, boolean create) {
        if (create) {
            for (int c = 0; c < count; c++) {
                createCell();
            }
        } else {
            this.cellIdx += count;
        }
    }

    public Cell createCell() {
        return this.curentRow.createCell(this.cellIdx++);
    }

    public void mergeCells(int rowspan, int colspan) {
        this.curentSheet.addMergedRegion(new CellRangeAddress(this.rowCount - 1, this.rowCount - 2 + rowspan, this.cellIdx - 1, this.cellIdx - 2 + colspan));
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
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleColumnHeading);
        cell.setCellValue(createRichTextString(text));
    }

    public void header2Cell(String text) {
        if (!CommonsStringUtils.isStringSet(text)) {
            this.cellIdx++;
            return;
        }
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleColumnHeading2);
        cell.setCellValue(createRichTextString(text));
    }

    protected RichTextString createRichTextString(String text) {
        if (xlsx) {
            return new XSSFRichTextString(text);
        } else {
            return new HSSFRichTextString(text);
        }
    }

    @Override
    public void cell(Object value) {
        if (value == null) {
            cellEmpty();
        } else if (value instanceof String) {
            cell((String) value);
        } else if (value instanceof java.sql.Date) {
            cell((java.sql.Date) value);
        } else if (value instanceof Date) {
            cell((Date) value);
        } else if (value instanceof Boolean) {
            cell(((Boolean) value).booleanValue());
        } else if (value instanceof BigDecimal) {
            cell((BigDecimal) value);
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
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleDefault);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(createRichTextString(value));
    }

    public void cell(BigDecimal value) {
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleDollar);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
    }

    public void cell(double value) {
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleDouble);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
    }

    public void cell(long value) {
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleInteger);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
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

    public void cell(java.sql.Date value) {
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleDate);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    public void cell(Date value) {
        Cell cell = createCell();
        cell.setCellStyle(this.cellStyleDateTime);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    public void cell(double value, int fractionDigits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractionDigits);
        nf.setMinimumFractionDigits(fractionDigits);
        cell(nf.format(value));
    }

    public void cellCurrency(double value) {
        Cell cell = createCell();
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(this.cellStyleDollar);
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
        return MimeMap.getContentType(xlsx ? DownloadFormat.XLSX : DownloadFormat.XLS);
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
