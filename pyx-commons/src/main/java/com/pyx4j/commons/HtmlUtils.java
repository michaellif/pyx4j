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
 * Created on Jan 25, 2010
 * @author vlads
 */
package com.pyx4j.commons;

import java.util.HashMap;
import java.util.Map;

public class HtmlUtils {

    public static final String NO_BREAK_SPACE_HTML = "&nbsp;";

    public static final char NO_BREAK_SPACE_UTF8 = '\u00A0';

    public static final char ZERO_WIDTH_SPACE_UTF8 = '\u200B';

    public static final char RIGHT_ARROW_UTF8 = '\u2192';

    public static final String RIGHT_ARROW_HTML = "&#8594;";

    public static final char LEFT_ARROW_UTF8 = '\u2190';

    public static final String LEFT_ARROW_HTML = "&#8592;";

    public static final char TRIANGLE_UP_UTF8 = '\u25B2';

    public static final char TRIANGLE_DOWN_UTF8 = '\u25BC';

    public static final char TRIANGLE_UP_SMALL_UTF8 = '\u25B4';

    public static final char TRIANGLE_DOWN_SMALL_UTF8 = '\u25BE';

    private final static String[] HTML_ENTITIES = {

            ">", "&gt;",

            "<", "&lt;",

            "&", "&amp;",

            "\"", "&quot;",

            "\u20AC", "&euro;",

            "\u00A3", "&pound;",

            "\u00A9", "&copy;" };

    private static Map<Character, String> htmlEntityTableEncode;

    private static void buildEntityTables() {
        htmlEntityTableEncode = new HashMap<Character, String>(HTML_ENTITIES.length);
        for (int i = 0; i < HTML_ENTITIES.length; i += 2) {
            if (!htmlEntityTableEncode.containsKey(HTML_ENTITIES[i].charAt(0))) {
                htmlEntityTableEncode.put(HTML_ENTITIES[i].charAt(0), HTML_ENTITIES[i + 1]);
            }
        }
    }

    public static String escapeText(String value) {
        if (value == null) {
            return "";
        }
        if (htmlEntityTableEncode == null) {
            buildEntityTables();
        }

        StringBuilder sb = new StringBuilder(value.length() * 2);
        char ch;
        for (int i = 0; i < value.length(); ++i) {
            ch = value.charAt(i);
            if (htmlEntityTableEncode.get(ch) != null) {
                sb.append(htmlEntityTableEncode.get(ch));
            } else if ((ch >= 45 && ch <= 59) || (ch >= 63 && ch <= 95) || (ch >= 97 && ch <= 122) || ch == ' ' || "!\"#$%()*+".indexOf(ch) != -1) {
                sb.append(ch);
            } else if (ch == '\n') {
                sb.append("\n");
            } else {
                sb.append(htmlHex(ch));
            }
        }
        return sb.toString();
    }

    /**
     * Will return true if string contains HTML tags.
     *
     * Warn: Not very sophisticated.
     *
     * @param s
     *            String to test
     * @return true if string contains HTML
     */
    public static boolean isHtml(String text) {
        if (CommonsStringUtils.isEmpty(text)) {
            return false;
        } else {
            return text.matches("\\<[^\\>]*\\>");
        }
    }

    public static boolean isEmpty(String html) {
        if (CommonsStringUtils.isEmpty(html)) {
            return true;
        }
        boolean tag = false;
        for (char part : html.toCharArray()) {
            if (part == '<') {
                tag = true;
            } else if (tag) {
                if (part == '>') {
                    tag = false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static String htmlHex(char ch) {
        return "&#" + Integer.valueOf(ch) + ";";
    }

    // HTML header decoration:

    public static String h1(String text) {
        return "<h1>" + text + "</h1>";
    }

    public static String h2(String text) {
        return "<h2>" + text + "</h2>";
    }

    public static String h3(String text) {
        return "<h3>" + text + "</h3>";
    }

    public static String h4(String text) {
        return "<h4>" + text + "</h4>";
    }

    public static String h5(String text) {
        return "<h5>" + text + "</h5>";
    }

    public static String h6(String text) {
        return "<h6>" + text + "</h6>";
    }

    public static String removeHtmlTags(String htmlText) {

        return htmlText.replaceAll("\\<br[^>]*\\>", " ").replaceAll("\\<[^>]*>", "");
    }
}
