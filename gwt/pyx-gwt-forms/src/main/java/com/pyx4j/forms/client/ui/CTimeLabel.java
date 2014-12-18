/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on 2011-02-10
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import java.sql.Time;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.IFormatter;

public class CTimeLabel extends CLabel<Time> {

    public CTimeLabel() {
        super();
        setTimeFormat(CTimeField.defaultTimeFormat);
    }

    public void setTimeFormat(final String format) {
        if (format != null) {
            setFormatter(new IFormatter<Time, String>() {

                DateTimeFormat formatter = DateTimeFormat.getFormat(format);

                @Override
                public String format(Time value) {
                    return formatter.format(value);
                }

            });
        }
    }
}
