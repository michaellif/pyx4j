/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Feb 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.i18n.client.NumberFormat;

public class CNumberLabel extends CAbstractLabel<Number> {

    public void setNumberFormat(final String format) {
        if (format != null) {
            setFormat(new IFormat<Number>() {

                NumberFormat formatter = NumberFormat.getFormat(format);

                @Override
                public String format(Number value) {
                    return formatter.format(value);
                }

                @Override
                public Number parse(String string) {
                    return null;
                }
            });
        }
    }
}
