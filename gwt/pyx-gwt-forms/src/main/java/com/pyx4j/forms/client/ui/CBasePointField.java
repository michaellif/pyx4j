/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Nov 25, 2015
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.i18n.shared.I18n;

public class CBasePointField extends CTextFieldBase<BigDecimal, NTextBox<BigDecimal>> {

    static final I18n i18n = I18n.get(CBasePointField.class);

    public CBasePointField() {
        super();
        setFormatter(new BasePointFormat("#,###"));
        setParser(new BasePointParser());
        setNativeComponent(new NTextBox<BigDecimal>(this));
        setWatermark("0bps");
    }

    public void setBasePointFormat(String pattern) {
        setFormatter(new BasePointFormat(pattern));
    }

    public static class BasePointFormat implements IFormatter<BigDecimal, String> {

        private final NumberFormat nf;

        public BasePointFormat() {
            this("#,###");
        }

        public BasePointFormat(String pattern) {
            nf = NumberFormat.getFormat(pattern);
        }

        @Override
        public String format(BigDecimal value) {
            if (value == null) {
                return "";
            } else {
                return nf.format(value.multiply(new BigDecimal("10000"))) + "bps";
            }
        }
    }

    public static class BasePointParser implements IParser<BigDecimal> {

        @Override
        public BigDecimal parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                string = string.replaceAll("[,bps]+", "");
                return new BigDecimal(string).divide(new BigDecimal("10000"));
            } catch (NumberFormatException e) {
                throw new ParseException(i18n.tr("Invalid bps format. Enter valid bps number"), 0);
            }
        }

    }
}