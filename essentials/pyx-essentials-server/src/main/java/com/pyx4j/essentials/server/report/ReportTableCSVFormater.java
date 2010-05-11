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
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.pyx4j.commons.Consts;

public class ReportTableCSVFormater implements ReportTableFormater, Externalizable {

    protected transient StringBuilder dataBuilder;

    protected SimpleDateFormat dateFormat;

    public ReportTableCSVFormater() {
        dataBuilder = new StringBuilder();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    }

    public void setTimezoneOffset(int timezoneOffset) {
        // Hack. Selecting first time zone is as good as any, There are no Daylight Saving Time information from the GWT client.
        String[] ids = TimeZone.getAvailableIDs(-(int) (timezoneOffset * Consts.MIN2MSEC));
        if (ids.length != 0) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(ids[0]));
        }
    }

    @Override
    public byte[] getBinaryData() {
        return dataBuilder.toString().getBytes();
    }

    @Override
    public String getContentType() {
        return "text/csv";
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
        } else if (value instanceof Date) {
            text = dateFormat.format((Date) value);
        } else {
            text = value.toString();
        }
        boolean needQuote = text.contains("\"") || text.contains("\n") || text.contains(",");
        if (needQuote) {
            dataBuilder.append("\"");
            text = text.replaceAll("\"", "\"\"");
        }
        dataBuilder.append(text);
        if (needQuote) {
            dataBuilder.append("\"");
        }
        dataBuilder.append(',');
    }

    @Override
    public void newRow() {
        dataBuilder.append("\r\n");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        dataBuilder = new StringBuilder();
        String dataStored = in.readUTF();
        if (dataStored != null) {
            dataBuilder.append(dataStored);
        }
        dateFormat = (SimpleDateFormat) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(dataBuilder.toString());
        out.writeObject(dateFormat);
    }

}
