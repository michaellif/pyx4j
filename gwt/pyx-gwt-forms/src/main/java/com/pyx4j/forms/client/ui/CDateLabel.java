/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on 2011-02-10
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.DatePicker;

public class CDateLabel extends CLabel<Date> {

    public CDateLabel() {
        super();
        setDateFormat(DatePicker.defaultDateFormat);
    }

    public void setDateFormat(final String format) {
        if (format != null) {
            setFormatter(new IFormatter<Date, String>() {

                DateTimeFormat formatter = DateTimeFormat.getFormat(format);

                @Override
                public String format(Date value) {
                    return formatter.format(value);
                }

            });
        }
    }
}
