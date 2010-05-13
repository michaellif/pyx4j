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
 * Created on 2010-05-13
 * @author vlads
 * @version $Id$
 */
//package com.pyx4j.gwt.emul.java.text;
package java.text;

/**
 * Minimal implementation to get going
 */
public class MessageFormat {

    private static final char DELIM_START = '{';

    private static final char DELIM_STOP = '}';

    private MessageFormat() {
    }

    public static String format(final String pattern, Object... arguments) {
        StringBuilder result = new StringBuilder();

        boolean quotedString = false;
        boolean formatElement = false;

        StringBuilder formatPattern = null;

        char c = '\0';
        char pc;
        for (int index = 0; index < pattern.length(); index++) {
            pc = c;
            c = pattern.charAt(index);
            if (quotedString) {
                if ((c == '\'') && (pc != '\'')) {
                    quotedString = false;
                    break;
                }
                result.append(c);
                continue;
            } else if (formatElement) {
                if (c == DELIM_STOP) {
                    format(result, formatPattern.toString(), arguments);
                    formatElement = false;
                    formatPattern = null;
                } else {
                    formatPattern.append(c);
                }
                continue;
            }

            switch (c) {
            case '\'':
                quotedString = true;
                break;
            case DELIM_START:
                formatPattern = new StringBuilder();
                formatElement = true;
                break;
            default:
                result.append(c);
                break;
            }
        }

        return result.toString();
    }

    private static void format(StringBuilder result, final String formatPattern, Object... arguments) {
        int comaIdx = formatPattern.indexOf(',');
        int index;
        if (comaIdx > 0) {
            index = Integer.valueOf(formatPattern.substring(0, comaIdx));
        } else {
            index = Integer.valueOf(formatPattern);
        }
        Object value = arguments[index];
        result.append(value);
    }

}
