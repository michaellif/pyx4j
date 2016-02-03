/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Feb 11, 2011
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.formatters.NumberFormatter;
import com.pyx4j.commons.formatters.SimpleMessageFormatter;

public class CNumberLabel extends CLabel<Number> {

    public void setNumberFormat(String format, boolean useMessageFormat) {
        if (format != null) {
            if (useMessageFormat) {
                setFormatter(new SimpleMessageFormatter<Number>(format));
            } else {
                setFormatter(new NumberFormatter<Number>(format));
            }
        }
    }

}
