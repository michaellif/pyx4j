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
 * Created on 2010-04-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.csv;

import java.util.List;
import java.util.Vector;

public class CSVParser implements TextParser {

    public static boolean allowEscapeBackslash = false;

    @Override
    public String[] parse(String line) {
        if (line.startsWith("#") || (line.length() == 0)) {
            return null;
        }
        List<String> columns = new Vector<String>();

        StringBuilder col = new StringBuilder();
        boolean escape = false;
        boolean quoted = false;
        boolean expectDoubleQuote = false;
        for (char c : line.toCharArray()) {
            if (escape) {
                col.append(c);
                escape = false;
            } else {
                switch (c) {
                case '"':
                    if (quoted) {
                        if (expectDoubleQuote) {
                            col.append(c);
                            expectDoubleQuote = false;
                        } else {
                            expectDoubleQuote = true;
                        }
                    } else {
                        if (col.length() == 0) {
                            quoted = true;
                        } else {
                            // Just add it,, ignore errors,
                            col.append(c);
                        }
                    }
                    break;
                case ',':
                    if ((quoted) && (!expectDoubleQuote)) {
                        col.append(c);
                    } else {
                        columns.add(col.toString());
                        col = new StringBuilder();
                        quoted = false;
                    }
                    break;
                case '\\':
                    if (allowEscapeBackslash) {
                        escape = true;
                    } else {
                        col.append(c);
                    }
                    break;
                default:
                    if (expectDoubleQuote) {
                        expectDoubleQuote = false;
                        quoted = false;
                    }
                    col.append(c);
                    break;
                }
            }
        }
        if (col.length() > 0) {
            columns.add(col.toString());
        }

        return columns.toArray(new String[columns.size()]);
    }
}
