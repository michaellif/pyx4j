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
 * Created on Nov 26, 2015
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.rpc.shared;

import java.math.BigDecimal;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;

public class MoneyFormat implements IFormatter<BigDecimal, String> {

    static final I18n i18n = I18n.get(MoneyFormat.class);

    public static final String symbol = i18n.tr("$");

    protected final NumberFormat nf;

    @I18nContext(javaFormatFlag = true)
    public MoneyFormat() {
        nf = NumberFormat.getFormat(i18n.tr("#,##0.00"));
    }

    @Override
    public String format(BigDecimal value) {
        if (value == null) {
            return "";
        } else {
            return symbol + nf.format(value);
        }
    }
}