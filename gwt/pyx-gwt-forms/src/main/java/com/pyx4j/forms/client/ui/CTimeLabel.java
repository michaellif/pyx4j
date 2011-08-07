/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on 2011-02-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.sql.Time;

import com.google.gwt.i18n.client.DateTimeFormat;

public class CTimeLabel extends CAbstractLabel<Time> {

    public CTimeLabel() {
        this(null);
    }

    public CTimeLabel(String title) {
        super(title);
        setTimeFormat(CTimeField.defaultTimeFormat);
    }

    public void setTimeFormat(final String format) {
        if (format != null) {
            setFormat(new IFormat<Time>() {

                DateTimeFormat formatter = DateTimeFormat.getFormat(format);

                @Override
                public String format(Time value) {
                    return formatter.format(value);
                }

                @Override
                public Time parse(String string) {
                    return null;
                }

            });
        }
    }
}
