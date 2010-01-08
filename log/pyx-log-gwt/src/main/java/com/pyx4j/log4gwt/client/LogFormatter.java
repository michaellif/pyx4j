/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on May 14, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import java.util.Date;

import org.slf4j.helpers.MessageFormatter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.pyx4j.log4gwt.shared.LogEvent;

public class LogFormatter {

    public static enum FormatStyle {
        LINE, HTML, FULL, FULL_HOSTED
    }

    private static final DateTimeFormat timeFormatter = DateTimeFormat.getFormat("HH:mm:ss.SSS");

    public static String format(LogEvent event, FormatStyle style) {
        StringBuilder b = new StringBuilder();
        format(event, style, b);
        return b.toString();
    }

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
        b.append(timeFormatter.format(new Date(event.getEventTime()))).append(space);
        if ((style == FormatStyle.HTML) && (closeFontTag)) {
            b.append("</font>");
        }
        if (event.getDataArray() != null) {
            b.append(MessageFormatter.arrayFormat(event.getMessage(), event.getDataArray()));
        } else {
            b.append(MessageFormatter.format(event.getMessage(), event.getData1(), event.getData2()));
            //b.append(event.getMessage(), event.getData1(), event.getData2());
        }
        if ((style != FormatStyle.FULL_HOSTED) && (event.getThrowable() != null)) {
            b.append(space);
            b.append(event.getThrowable());
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
