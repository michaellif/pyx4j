/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-10
 * @author vlads
 */
package com.pyx4j.essentials.server.docs.sheet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.pyx4j.commons.Consts;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.shared.DownloadFormat;

public class ReportTableCSVFormatter implements ReportTableFormatter {

    private static final long serialVersionUID = 6357364356839304581L;

    protected DataBuilder dataBuilder;

    protected SimpleDateFormat dateFormat;

    protected SimpleDateFormat dateTimeFormat;

    protected transient Charset charset;

    protected String charsetSerial;

    private boolean forceQuote;
    
    private boolean emitUTF8BOM = true;

    private int rowCount = 0;

    private int cellCount = 0;

    public ReportTableCSVFormatter() {
        this(StandardCharsets.UTF_8);
    }

    public ReportTableCSVFormatter(Charset charset) {
        this.charset = charset;
        this.charsetSerial = charset.name();
        this.dataBuilder = new DataBuilder();
        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        this.dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.charset = Charset.forName(charsetSerial);
    }

    public boolean isForceQuote() {
        return forceQuote;
    }

    public void setForceQuote(boolean forceQuote) {
        this.forceQuote = forceQuote;
    }
    
    public boolean isEmitUTF8BOM() {
        return emitUTF8BOM;
    }

    public void setEmitUTF8BOM(boolean emitUTF8BOM) {
        this.emitUTF8BOM = emitUTF8BOM;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        // Hack. Selecting first time zone is as good as any, There are no Daylight Saving Time information from the GWT client.
        String[] ids = TimeZone.getAvailableIDs(-(int) (timezoneOffset * Consts.MIN2MSEC));
        if (ids.length != 0) {
            dateTimeFormat.setTimeZone(TimeZone.getTimeZone(ids[0]));
        }
    }

    public void setDateFormatPattern(String pattern) {
        dateFormat = new SimpleDateFormat(pattern);
    }

    public void setDateTimeFormatPattern(String pattern) {
        dateTimeFormat = new SimpleDateFormat(pattern);
    }

    @Override
    public byte[] getBinaryData() {
        byte[] b = dataBuilder.getBinaryData(charset);
        if (isEmitUTF8BOM() && charset.equals(StandardCharsets.UTF_8)) {
            byte[] utf = new byte[b.length + 3];
            // Add UTF-8 BOM for MS Excel
            utf[0] = (byte) 0xEF;
            utf[1] = (byte) 0xBB;
            utf[2] = (byte) 0xBF;
            System.arraycopy(b, 0, utf, 3, b.length);
            return utf;
        } else {
            return b;
        }
    }

    @Override
    public int getBinaryDataSize() {
        return dataBuilder.getBinaryDataSize();
    }

    @Override
    public String getContentType() {
        return MimeMap.getContentType(DownloadFormat.CSV);
    }

    @Override
    public void header(String text) {
        cell(text);
    }

    @Override
    public void cell(Object value) {
        String text;
        if (value == null) {
            text = "";
        } else if (value instanceof java.sql.Date) {
            text = dateFormat.format((Date) value);
        } else if (value instanceof Date) {
            text = dateTimeFormat.format((Date) value);
        } else {
            text = value.toString();
        }

        if (cellCount > 0) {
            dataBuilder.append(',');
        }

        boolean needQuote = text.contains("\"") || text.contains("\n") || text.contains(",");
        if (needQuote) {
            text = text.replaceAll("\"", "\"\"");
        }
        if (needQuote || isForceQuote()) {
            dataBuilder.append("\"");
        }
        dataBuilder.append(text);
        if (needQuote || isForceQuote()) {
            dataBuilder.append("\"");
        }
        cellCount++;
    }

    @Override
    public void newRow() {
        dataBuilder.append("\r\n");
        rowCount++;
        cellCount = 0;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

}
