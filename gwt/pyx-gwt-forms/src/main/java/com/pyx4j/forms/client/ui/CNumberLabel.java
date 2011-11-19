/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Feb 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.SimpleMessageFormat;

public class CNumberLabel extends CAbstractLabel<Number> {

    public void setNumberFormat(final String format) {
        if (format != null) {
            setFormat(new IFormat<Number>() {

                @Override
                public String format(Number value) {
                    return SimpleMessageFormat.format(format, value);
                }

                @Override
                public Number parse(String string) {
                    return null;
                }
            });
        }
    }
}
