/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.PrintWriter;
import java.util.StringTokenizer;

public class POFileWriter {

    public int pageWidth = 78;

    public boolean wrapLines = true;

    public void write(PrintWriter writer, POFile po) {
        writeEntry(writer, po.header);

        for (POEntry entry : po.entries) {
            writeEntry(writer, entry);
        }
    }

    private void writeEntry(PrintWriter writer, POEntry entry) {
        if (entry.comments != null) {
            for (final String str : entry.comments) {
                writer.println(("# " + str).trim());
            }
        }

        if (entry.extractedComments != null) {
            for (final String str : entry.extractedComments) {
                writer.println(("#. " + str).trim());
            }
        }

        if (entry.references != null) {
            for (final String str : entry.references) {
                writer.println("#: " + str);
            }
        }

        if (entry.flags != null) {
            for (final String str : entry.flags) {
                writer.println("#, " + str);
            }
        }

        if (entry.fuzzy) {
            writer.println("#, fuzzy");
        }

        if (entry.unparsedComments != null) {
            for (final String str : entry.unparsedComments) {
                writer.println("#" + str);
            }
        }

        if (entry.previousUntranslated != null) {
            //TODO
        }

        writer.print("msgid ");
        writeString(writer, entry.untranslated, "msgid ".length());

        writer.print("msgstr ");
        writeString(writer, entry.translated, "msgstr ".length());

        writer.println();

    }

    private void writeString(PrintWriter writer, String str, int firstLnePrefixLen) {
        if (str == null) {
            writer.print("\"\"");
        } else {
            if (str.contains("\n") || (wrapLines && (str.length() > pageWidth - 2 - firstLnePrefixLen))) {
                writer.print("\"\"");

                int lineSize = 0;
                StringTokenizer t = new StringTokenizer(str, wrapLines ? " \n" : "\n", true);
                while (t.hasMoreTokens()) {
                    if (lineSize == 0) {
                        writer.print("\n\"");
                    }
                    String token = t.nextToken();
                    if (token.equals("\n")) {
                        writer.print("\\n\"");
                        lineSize = 0;
                    } else {
                        if (wrapLines && (lineSize + token.length() > pageWidth - 2)) {
                            writer.println("\"");
                            writer.print("\"");
                            lineSize = 0;
                        }
                        writer.print(token);
                        lineSize += token.length();
                    }
                }
                if (lineSize != 0) {
                    writer.print("\"");
                }
            } else {
                writer.print("\"");
                writer.print(str);
                writer.print("\"");
            }
        }

        writer.println();
    }
}
