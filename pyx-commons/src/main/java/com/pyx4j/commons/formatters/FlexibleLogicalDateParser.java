/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jan 8, 2016
 * @author vlads
 */
package com.pyx4j.commons.formatters;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;

public class FlexibleLogicalDateParser implements IParser<LogicalDate> {

    private List<String> posiblePatters;

    public FlexibleLogicalDateParser(List<String> posiblePatters) {
        this.posiblePatters = posiblePatters;
    }

    @Override
    public LogicalDate parse(String string) throws ParseException {
        if (CommonsStringUtils.isEmpty(string)) {
            return null; // empty value case
        }
        for (String pattern : posiblePatters) {
            try {
                Date dateObj = TimeUtils.strictDateParse(string, pattern);
                String asString = TimeUtils.simpleFormat(dateObj, pattern);
                if (string.equalsIgnoreCase(asString)) {
                    return new LogicalDate(dateObj);
                }
            } catch (IllegalArgumentException ignore) {
                continue;
            }
        }
        throw new ParseException("Invalid date format.", 0);
    }

}
