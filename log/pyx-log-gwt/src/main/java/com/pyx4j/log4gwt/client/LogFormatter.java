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
 * Created on May 14, 2009
 * @author vlads
 */
package com.pyx4j.log4gwt.client;

import java.util.Date;

import org.slf4j.helpers.MessageFormatter;

import com.pyx4j.log4gwt.shared.LogEvent;

public class LogFormatter {

    public static enum FormatStyle {
        LINE, HTML, FULL, FULL_HOSTED
    }

    public static String format(LogEvent event, FormatStyle style) {
        StringBuilder b = new StringBuilder();
        format(event, style, b);
        return b.toString();
    }

    @SuppressWarnings("deprecation")
    private static void format(LogEvent event, FormatStyle style, StringBuilder b) {
        boolean closeFontTag = false;
        if (style == FormatStyle.HTML) {
            switch (event.getLevel()) {
            case ERROR:
                b.append("<font color=\"#FF0000\">");
                closeFontTag = true;
                break;
            case INFO:
                b.append("<font color=\"#0000FF\">");
                closeFontTag = true;
                break;
            case WARN:
                b.append("<font color=\"#FF9933\">");
                closeFontTag = true;
                break;
            }
        }
        String level = event.getLevel().toString();
        b.append(level);
        String space = " ";
        if (style == FormatStyle.HTML) {
            space = "&nbsp;";
        }
        if (level.length() < 5) {
            // level names are only 4 and 5 chars length
            b.append(space);
        }
        b.append(space);
        // "HH:mm:ss.SSS":  Use  Simple time format to exclude com.google.gwt.i18n.client.DateTimeFormat from linking
        Date date = new Date(event.getEventTime());
        b.append(date.getHours()).append(':');
        if (date.getMinutes() < 10) {
            b.append('0');
        }
        b.append(date.getMinutes());
        b.append(':');
        if (date.getSeconds() < 10) {
            b.append('0');
        }
        b.append(date.getSeconds());
        b.append('.');
        int milliseconds = (int) (event.getEventTime() % 1000);
        if (milliseconds < 10) {
            b.append("00");
        } else if (milliseconds < 100) {
            b.append('0');
        }
        b.append(milliseconds);

        b.append(space);

        if ((style == FormatStyle.HTML) && (closeFontTag)) {
            b.append("</font>");
        }
        if (event.getFormatedMessage() == null) {
            if (event.getDataArray() != null) {
                event.setFormatedMessage(MessageFormatter.arrayFormat(event.getMessage(), event.getDataArray()).getMessage());
            } else {
                event.setFormatedMessage(MessageFormatter.format(event.getMessage(), event.getData1(), event.getData2()).getMessage());
            }
        }
        b.append(event.getFormatedMessage());
        if ((style != FormatStyle.FULL_HOSTED) && (event.getThrowable() != null)) {
            b.append(space);
            b.append(event.getThrowableMessage());
        }
        //TODO
        switch (style) {
        case LINE:
            break;
        case HTML:
            break;
        case FULL:
            break;
        }
    }
}
