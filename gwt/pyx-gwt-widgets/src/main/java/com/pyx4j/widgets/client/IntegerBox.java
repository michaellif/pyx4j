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
 * Created on Jun 11, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.widgets.client;

import java.text.ParseException;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.i18n.shared.I18n;

public class IntegerBox extends TextBox<Integer> {

    private static final I18n i18n = I18n.get(IntegerBox.class);

    public IntegerBox() {

        setParser(new IParser<Integer>() {

            @Override
            public Integer parse(String string) throws ParseException {
                if (string == null || string.trim().equals("")) {
                    return null;
                }
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    throw new ParseException(i18n.tr("Invalid format."), 0);
                }

            }
        });

        setFormatter(new IFormatter<Integer, String>() {

            @Override
            public String format(Integer value) {
                return value.toString();
            }
        });
    }
}