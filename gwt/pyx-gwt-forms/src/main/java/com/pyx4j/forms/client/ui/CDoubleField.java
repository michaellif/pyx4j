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
 * Created on 2010-05-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.i18n.client.NumberFormat;

public class CDoubleField extends CNumberField<Double> {

    public CDoubleField() {
        this(null);
    }

    public CDoubleField(String title) {
        super(title, "Should be numeric value");
    }

    @Override
    boolean isInRange(Double value, Double from, Double to) {
        if (value == null) {
            return false;
        }
        return value >= from && value <= to;
    }

    @Override
    public Double valueOf(String string) {
        return Double.valueOf(string);
    }

    public void setNumberFormat(String pattern) {
        setFormat(new DoubleNumberFormat(pattern));
    }

    class DoubleNumberFormat implements IFormat<Double> {

        NumberFormat nf;

        DoubleNumberFormat(String pattern) {
            nf = NumberFormat.getFormat(pattern);
        }

        public String format(Double value) {
            if (value == null) {
                return "";
            } else {
                return nf.format(value);
            }
        }

        public Double parse(String string) {
            try {
                return valueOf(string);
            } catch (NumberFormatException e) {
                return null;
            }
        }

    }
}
