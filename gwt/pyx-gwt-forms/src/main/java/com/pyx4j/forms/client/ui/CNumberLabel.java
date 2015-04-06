/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Feb 11, 2011
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;

public class CNumberLabel extends CLabel<Number> {

    public void setNumberFormat(String format, boolean useMessageFormat) {
        if (format != null) {
            if (useMessageFormat) {
                setFormatter(new ViewNumberSimpleMessageFormat<Number>(format));
            } else {
                setFormatter(new ViewNumberFormat<Number>(format));
            }
        }
    }

    public static class ViewNumberFormat<T extends Number> implements IFormatter<T, String> {

        private final NumberFormat formatter;

        ViewNumberFormat(String format) {
            formatter = NumberFormat.getFormat(format);
        }

        @Override
        public String format(T value) {
            return formatter.format(value);
        }

    }

    public static class ViewNumberSimpleMessageFormat<T extends Number> implements IFormatter<T, String> {

        private final String format;

        ViewNumberSimpleMessageFormat(String format) {
            this.format = format;
        }

        @Override
        public String format(T value) {
            return SimpleMessageFormat.format(format, value);
        }

    }

}
