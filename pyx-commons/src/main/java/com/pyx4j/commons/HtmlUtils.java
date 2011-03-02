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
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.HashMap;
import java.util.Map;

public class HtmlUtils {

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

    public static String h4(String text) {
        return "<h4>" + text + "</h4>";
    }
}
