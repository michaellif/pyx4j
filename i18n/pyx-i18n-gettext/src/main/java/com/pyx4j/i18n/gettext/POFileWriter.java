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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class POFileWriter {

    public boolean writeBom = true;

    /**
     * Set output page width, N.B. poEdit counts CR.
     */
    public int pageWidth = 78;

    public boolean wrapLines = true;

    /**
     * Bug in PO Edit that we can't see the source code in UI
     */
    public boolean duplicateReferencesAsComments = false;

    public void write(File file, POFile po) throws IOException {
        PrintWriter writer = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (writeBom) {
                fos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            }
            writer = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
            this.write(writer, po);
            writer.flush();
            writer.close();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Throwable ignore) {
                }
            }
        }
    }

    public void write(PrintWriter writer, POFile po) {
        writeEntry(writer, po.header);

        for (POEntry entry : po.entries) {
            writer.println();
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

        if (duplicateReferencesAsComments) {
            if (entry.references != null) {
                for (final String str : entry.references) {
                    writer.println("#. " + str);
                }
            }
        }

        if (entry.references != null) {
            boolean cr = false;
            int lineSize = 0;

            for (final String str : entry.references) {
                if (!wrapLines) {
                    writer.println("#: " + str);
                    cr = true;
                } else {
                    if ((lineSize != 0) && (lineSize + str.length() + 1 > pageWidth)) {
                        lineSize = 0;
                        writer.println();
                    }
                    if (lineSize == 0) {
                        writer.print("#:");
                        lineSize += 2;
                    }
                    writer.print(" ");
                    writer.print(str);
                    lineSize += str.length() + 1;
                }
            }

            if (!cr) {
                writer.println();
            }
        }

        if (entry.flags != null) {
            writer.print("#, ");
            boolean first = true;
            for (final String str : entry.flags) {
                if (!first) {
                    writer.print(", ");
                }
                writer.print(str);
            }
            writer.println();
        }

        if (entry.unparsedComments != null) {
            for (final String str : entry.unparsedComments) {
                writer.println("#" + str);
            }
        }

        if (entry.previousUntranslated != null) {
            //TODO
        }

        if (entry.context != null) {
            writer.print("msgctxt ");
            writeString(writer, entry.context, "msgctxt ".length());
        }

        writer.print("msgid ");
        writeString(writer, entry.untranslated, "msgid ".length());

        writer.print("msgstr ");
        writeString(writer, entry.translated, "msgstr ".length());
    }

    void writeString(PrintWriter writer, String str, int firstLnePrefixLen) {
        if (str == null) {
            writer.print("\"\"");
        } else {
            str = str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t");
            if (str.contains("\n") || (wrapLines && (str.length() > pageWidth - 2 - firstLnePrefixLen))) {
                int lineSize = 0;
                // ignore case of single last CR in string
                if (str.indexOf("\n") == str.length() - 1) {
                    lineSize = firstLnePrefixLen;
                    writer.print("\"");
                } else {
                    writer.print("\"\"");
                }

                StringTokenizer t = new StringTokenizer(str, wrapLines ? " \n" : "\n", true);
                while (t.hasMoreTokens()) {
                    if (lineSize == 0) {
                        writer.println();
                        writer.print("\"");
                        lineSize = 1;
                    }
                    String token = t.nextToken();
                    if (token.equals("\n")) {
                        writer.print("\\n\"");
                        lineSize = 0;
                    } else {
                        if (wrapLines && (lineSize + token.length() > pageWidth - 1)) {
                            writer.println("\"");
                            writer.print("\"");
                            lineSize = 1;
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
