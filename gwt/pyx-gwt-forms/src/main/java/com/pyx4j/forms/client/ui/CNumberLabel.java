/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Feb 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.SimpleMessageFormat;

public class CNumberLabel extends CAbstractLabel<Number> {

    public void setNumberFormat(String format, boolean useMessageFormat) {
        if (format != null) {
            if (useMessageFormat) {
                setFormat(new ViewNumberSimpleMessageFormat<Number>(format));
            } else {
                setFormat(new ViewNumberFormat<Number>(format));
            }
        }
    }

    public static class ViewNumberFormat<T extends Number> implements IFormat<T> {

        private final NumberFormat formatter;

        ViewNumberFormat(String format) {
            formatter = NumberFormat.getFormat(format);
        }

        @Override
        public String format(T value) {
            return formatter.format(value);
        }

        @Override
        public T parse(String string) throws ParseException {
            return null;
        }
    }

    public static class ViewNumberSimpleMessageFormat<T extends Number> implements IFormat<T> {

        private final String format;

        ViewNumberSimpleMessageFormat(String format) {
            this.format = format;
        }

        @Override
        public String format(T value) {
            return SimpleMessageFormat.format(format, value);
        }

        @Override
        public T parse(String string) throws ParseException {
            return null;
        }
    }

}
