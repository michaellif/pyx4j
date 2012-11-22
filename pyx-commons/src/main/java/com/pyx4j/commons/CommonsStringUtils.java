/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Date;

/**
 * This class would be common for client (GWT) and server.
 */
public class CommonsStringUtils {

    public static final String NO_BREAK_SPACE_HTML = "&nbsp;";

    public static final char NO_BREAK_SPACE_UTF8 = '\u00A0';

    public static boolean isStringSet(String str) {
        return ((str != null) && (str.length() > 0));
    }

    public static boolean isEmpty(String str) {
        return (!isStringSet(str) || (str.trim().length() == 0));
    }

    public static String nvl(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    public static String nvl(Object value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    public static boolean equals(String value1, String value2) {
        return ((value1 == value2) || (nvl(value1).equals(nvl(value2))));
    }

    public static boolean equals(String value1, Object value2) {
        return ((value1 == value2) || (nvl(value1).equals(nvl(value2))));
    }

    /**
     * Mainly used to avoid automatic strings concatenation in generated GWT
     * 
     * @param strs
     *            array of strings to be concatenated
     */
    public static String concat(String... strs) {
        StringBuilder b = new StringBuilder();
        for (String s : strs) {
            b.append(s);
        }
        return b.toString();
    }

    public static String nvl_concat(String value1, String value2, String sep) {
        if (isEmpty(value1)) {
            return nvl(value2);
        } else if (isEmpty(value2)) {
            return nvl(value1);
        } else {
            return value1 + sep + value2;
        }
    }

    public static String nvl_concat(Object value1, Object value2, String sep) {
        if (value1 == null) {
            return nvl(value2);
        } else if (value2 == null) {
            return nvl(value1);
        } else {
            return value1 + sep + value2;
        }
    }

    public static String paddingRight(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        return str.concat(padding(pads, padChar));
    }

    public static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    public static String paddZerro(int number, int lenght) {
        String value = String.valueOf(number);
        int len = value.length();
        if (len > lenght) {
            throw new IllegalArgumentException();
        }
        return padding(lenght - len, '0') + value;
    }

    public static String d00(long number) {
        if (number < 10) {
            return "0" + String.valueOf(number);
        } else {
            return String.valueOf(number);
        }
    }

    public static String d000(int i) {
        if ((i > 99) || (i < 0)) {
            return String.valueOf(i);
        } else if (i > 9) {
            return "0" + String.valueOf(i);
        } else {
            return "00" + String.valueOf(i);
        }
    }

    @SuppressWarnings("deprecation")
    public static String formatTime(Date date) {
        return d00(date.getHours()) + ":" + d00(date.getMinutes()) + ":" + d00(date.getSeconds());
    }

    public static String formatLong(long value) {
        if (value < 0) {
            return String.valueOf(value);
        } else if (value < 1000) {
            if (value > 99) {
                return String.valueOf(value);
            } else {
                return "0" + d00(value);
            }
        } else {
            return String.valueOf(value / 1000) + "," + formatLong(value % 1000);
        }
    }

    public static int linesCount(String message, int wrapLength) {
        if (message == null) {
            return 0;
        }
        String[] lines = message.split("\n");
        int count = lines.length;
        if (wrapLength > 0) {
            for (String line : lines) {
                if (line.length() > wrapLength) {
                    int over = line.length() % wrapLength;
                    if (over > 0) {
                        count++;
                    }
                    count += Math.ceil((line.length() - 1 - over) / wrapLength);
                }
            }
        }
        return count;
    }

    public static String splitTextToLines(String message, String... separators) {
        for (String separator : separators) {
            message = splitTextToLines(message, separator);
        }
        return message;
    }

    private static String splitTextToLines(String message, String separator) {
        StringBuilder messagesBuffer = new StringBuilder();
        String[] lines = message.split(separator);
        LoopCounter c = new LoopCounter(lines);
        for (String m : lines) {
            messagesBuffer.append(m);
            if (messagesBuffer.charAt(messagesBuffer.length() - 1) != '.') {
                messagesBuffer.append('.');
            }
            switch (c.next()) {
            case SINGLE:
                break;
            case FIRST:
            case ITEM:
                messagesBuffer.append("\n");
                break;
            case LAST:
                break;
            }
        }
        return messagesBuffer.toString();
    }
}
