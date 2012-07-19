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
 * Created on Jan 26, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.csv;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class XLSLoad {

    private static final I18n i18n = I18n.get(XLSLoad.class);

    private final static Logger log = LoggerFactory.getLogger(XLSLoad.class);

    private final Workbook wb;

    private final DataFormatter formatter;

    public static void loadFile(InputStream is, boolean xlsx, CSVReciver reciver) {
        try {
            XLSLoad l = new XLSLoad(is, xlsx);
            Sheet sheet = l.wb.getSheetAt(0);
            l.loadSheet(sheet, reciver);
        } catch (IOException ioe) {
            throw new RuntimeException("Load file error", ioe);
        }
    }

    public XLSLoad(InputStream is, boolean xlsx) throws IOException {
        try {
            formatter = new DataFormatter();
            if (xlsx) {
                wb = new XSSFWorkbook(is);
            } else {
                POIFSFileSystem fs = new POIFSFileSystem(is);
                wb = new HSSFWorkbook(fs);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) {
                is = null;
            }
        }
    }

    public int getNumberOfSheets() {
        return wb.getNumberOfSheets();
    }

    public String getSheetName(int sheetNumber) {
        return wb.getSheetAt(sheetNumber).getSheetName();
    }

    public boolean loadSheet(int sheetNumber, CSVReciver reciver) {
        Sheet sheet = wb.getSheetAt(sheetNumber);
        return loadSheet(sheet, reciver);
    }

    public boolean loadSheet(Sheet sheet, CSVReciver reciver) {
        int lineNumber = 0;
        try {
            boolean header = true;
            for (Row row : sheet) {
                lineNumber++;
                List<String> values = new Vector<String>();
                short cells = row.getLastCellNum();
                for (int cellnum = 0; cellnum < cells; cellnum++) {
                    Cell cell = row.getCell(cellnum, Row.RETURN_BLANK_AS_NULL);
                    values.add(getCellStringValue(cell));
                }
                if (header) {
                    if (reciver.onHeader(values.toArray(new String[values.size()]))) {
                        header = false;
                    }
                } else {
                    reciver.onRow(values.toArray(new String[values.size()]));
                }
            }
            return !header;
        } catch (UserRuntimeException e) {
            throw e;
        } catch (Throwable e) {
            log.error("XLSLoad error", e);
            throw new UserRuntimeException(i18n.tr("Load file error ''{0}'', row # {1}", e.getMessage(), lineNumber));
        }
    }

    protected String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            return "";
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(cell.getDateCellValue());
            } else {
                String format = cell.getCellStyle().getDataFormatString();
                // MS Excel 2010 accounting
                if (format.equals("_(* #,##0.00_);_(* \\(#,##0.00\\);_(* \"-\"??_);_(@_)")) {
                    return new DecimalFormat("0.00").format(cell.getNumericCellValue());
                } else {
                    return formatter.formatCellValue(cell);
                }
            }
        case Cell.CELL_TYPE_BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case Cell.CELL_TYPE_FORMULA:
            //TODO calculate 
            return "";
        case Cell.CELL_TYPE_ERROR:
            return "";
        }
        return null;
    }
}
