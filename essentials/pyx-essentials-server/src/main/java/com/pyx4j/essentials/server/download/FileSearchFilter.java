/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 25, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.download;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.gwt.server.DateUtils;

public class FileSearchFilter {

    private String text;

    private Time fromTime;

    private Time toTime;

    private Date fromDate;

    private Date toDate;

    private Date fromDateTime;

    private Date toDateTime;

    private boolean recursive;

    public FileSearchFilter() {

    }

    public FileSearchFilter(HttpServletRequest request) {
        setText(request.getParameter("text"));
        setFromTime(timeValue(dateValue(request, "ft", "HH:mm")));
        setToTime(timeValue(dateValue(request, "tt", "HH:mm")));
        setFromDate(dateValue(request, "fd", "yyyy-MM-dd"));
        setToDate(dateValue(request, "td", "yyyy-MM-dd"));
        setRecursive("true".equalsIgnoreCase(request.getParameter("recursive")));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Time getFromTime() {
        return fromTime;
    }

    public void setFromTime(Time fromTime) {
        this.fromTime = fromTime;
    }

    public void setFromTime(String fromTime) throws ParseException {
        this.fromTime = timeValue(DateUtils.detectDateformat(fromTime.trim(), "HH:mm"));
    }

    public Time getToTime() {
        return toTime;
    }

    public void setToTime(Time toTime) {
        this.toTime = toTime;
    }

    public void setToTime(String toTime) throws ParseException {
        this.toTime = timeValue(DateUtils.detectDateformat(toTime.trim(), "HH:mm"));
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getFromDateTime() {
        if (fromDateTime == null && fromDate != null) {
            fromDateTime = datePlusTime(fromDate, fromTime, false);
        }
        return fromDateTime;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public Date getToDateTime() {
        if (toDateTime == null && toDate != null) {
            toDateTime = datePlusTime(toDate, toTime, true);
        }
        return toDateTime;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    private static Date dateValue(HttpServletRequest request, String name, String format) {
        String text = request.getParameter(name);
        if (text == null) {
            return null;
        } else {
            Date date;
            try {
                date = new SimpleDateFormat(format, Locale.ENGLISH).parse(text.trim());
            } catch (ParseException e1) {
                try {
                    date = DateUtils.detectDateformat(text.trim());
                } catch (RuntimeException e2) {
                    return null;
                }
            }
            return date;
        }
    }

    private static Date datePlusTime(Date date, Time time, boolean dayEnd) {
        if (date == null) {
            return null;
        }

        if (time == null) {
            if (dayEnd) {
                return DateUtils.dayEnd(date);
            } else {
                return date;
            }
        } else {
            Calendar c = new GregorianCalendar();
            c.setTime(date);
            DateUtils.setTime(c, time);
            return c.getTime();
        }
    }

    private static Time timeValue(Date date) {
        if (date == null) {
            return null;
        } else {
            return new Time(date.getTime());
        }
    }
}
