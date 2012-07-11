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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

public class XLSLoad {

    private final HSSFWorkbook wb;

    private final HSSFDataFormatter formatter;

    public static void loadFile(InputStream is, CSVReciver reciver) {
        try {
            XLSLoad l = new XLSLoad(is);
            HSSFSheet sheet = l.wb.getSheetAt(0);
            l.loadSheet(sheet, reciver);
        } catch (IOException ioe) {
            throw new RuntimeException("Load file error", ioe);
        }
    }

    public XLSLoad(InputStream is) throws IOException {
        try {
            formatter = new HSSFDataFormatter();
            POIFSFileSystem fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
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

    public void loadSheet(int sheetNumber, CSVReciver reciver) {
        HSSFSheet sheet = wb.getSheetAt(sheetNumber);
        loadSheet(sheet, reciver);
    }

    public void loadSheet(HSSFSheet sheet, CSVReciver reciver) {
        int lineNumber = 0;
        try {
            boolean header = true;
            for (Row row : sheet) {
                lineNumber++;
                List<String> values = new Vector<String>();
                for (Cell cell : row) {
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
        } catch (Throwable e) {
            throw new RuntimeException("Load file error, Line# " + lineNumber, e);
        }
    }

    protected String getCellStringValue(Cell cell) {
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            return "";
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(cell.getDateCellValue());
            } else {
                return formatter.formatCellValue(cell);
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
