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
 * Created on Mar 27, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.util.HashMap;
import java.util.Map;

public class XMLEscape {

    private final static String[] XML_ENTITIES = {

    ">", "&gt;",

    "<", "&lt;",

    "&", "&amp;",

    "\"", "&quot;",

    "'", "&apos;",

    "\u20AC", "&euro;",

    "\u00A3", "&pound;",

    "\u00A9", "&copy;" };

    private static Map<Character, String> xmlEntityTableEncode;

    private static void buildEntityTables() {
        xmlEntityTableEncode = new HashMap<Character, String>(XML_ENTITIES.length);
        for (int i = 0; i < XML_ENTITIES.length; i += 2) {
            if (!xmlEntityTableEncode.containsKey(XML_ENTITIES[i].charAt(0))) {
                xmlEntityTableEncode.put(XML_ENTITIES[i].charAt(0), XML_ENTITIES[i + 1]);
            }
        }
    }

    public static String escapeText(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() * 2);
        appendEscapeText(sb, value);
        return sb.toString();
    }

    public static StringBuilder appendEscapeText(StringBuilder sb, String value) {
        if (value == null) {
            return sb;
        }
        if (xmlEntityTableEncode == null) {
            buildEntityTables();
        }
        char ch;
        for (int i = 0; i < value.length(); ++i) {
            ch = value.charAt(i);
            if (xmlEntityTableEncode.get(ch) != null) {
                sb.append(xmlEntityTableEncode.get(ch));
            } else if ((ch >= 45 && ch <= 59) || (ch >= 63 && ch <= 95) || (ch >= 97 && ch <= 122) || ch == ' ' || ",!\"#$%()*+-".indexOf(ch) != -1) {
                sb.append(ch);
            } else if (ch == '\n') {
                sb.append("\n");
            } else {
                sb.append(xmlHex(ch));
            }
        }
        return sb;
    }

    public static String xmlHex(char ch) {
        return "&#" + Integer.valueOf(ch) + ";";
    }
}
