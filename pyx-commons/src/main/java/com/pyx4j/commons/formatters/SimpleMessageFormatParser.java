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
 * Created on Mar 4, 2016
 * @author vlads
 */
package com.pyx4j.commons.formatters;

import java.io.Serializable;
import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.TimeUtils;

/**
 * Opposite to SimpleMessageFormat.format.
 * value type is detected base on message Format
 *
 * supports:
 * {0,duration,sec}
 *
 * TODO
 * {0,number,integer}
 * {0,time}
 * {0,date,short}
 */
public class SimpleMessageFormatParser<E extends Serializable> implements IParser<E> {

    private String formatType = null;

    private String formatStyle = null;

    public SimpleMessageFormatParser(String formatPattern) {
        int start = formatPattern.indexOf('{');
        if (start != -1) {
            int end = formatPattern.indexOf('}');
            int comaIdx = formatPattern.indexOf(',', start);
            if (comaIdx > 0) {
                // argumentIndex = Integer.valueOf(formatPattern.substring(0, comaIdx));
                int comaTypeIdx = formatPattern.indexOf(',', comaIdx + 1);
                if (comaTypeIdx > 0) {
                    formatType = formatPattern.substring(comaIdx + 1, comaTypeIdx);
                    formatStyle = formatPattern.substring(comaTypeIdx + 1, end);
                } else {
                    formatType = formatPattern.substring(comaIdx + 1, end);
                }
            }
        }

        if (formatType == null) {
            throw new IllegalArgumentException("'" + formatPattern + "' format pattern is unexpected");
        }

        switch (formatType) {
        case "duration":
            if (formatStyle == null) {
                formatStyle = "msec";
            }
            break;
        case "number":
            if (formatStyle == null) {
                formatStyle = "#";
            }
            break;
        default:
            throw new IllegalArgumentException("Unsupported '" + formatType + "' in '" + formatPattern + "' format pattern");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public E parse(String string) throws ParseException {
        if (CommonsStringUtils.isEmpty(string)) {
            return null;
        }
        switch (formatType) {
        case "duration":
            switch (formatStyle) {
            //case "msec":
            //formatedArg = TimeUtils.durationFormat(duration.longValue());
            case "sec":
                return (E) Integer.valueOf(TimeUtils.durationParseSeconds(string));
            default:
                throw new UnsupportedOperationException();
            }
// TODO in pyx 2.2.10 merge
//        case "number":
//            return SimpleFormat.numberParse(string);

        default:
            throw new UnsupportedOperationException();
        }

    }

}
