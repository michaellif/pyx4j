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
 * Created on Oct 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class POFileReader {

    public POFile readResource(String name) throws IOException {
        return readResource(Thread.currentThread().getContextClassLoader(), name);
    }

    public POFile readResource(ClassLoader classLoader, String name) throws IOException {
        InputStream is = classLoader.getResourceAsStream(name);
        if (is != null) {
            try {
                return read(new InputStreamReader(is, Charset.forName("UTF-8")));
            } finally {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        } else {
            return null;
        }
    }

    public POFile read(Reader reader) throws IOException {
        POFile po = new POFile();

        POEntry currentEntry = null;
        boolean firstEntry = true;
        LineNumberReader lnr = new LineNumberReader(reader);
        for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
            if ((line.length() == 0) || (line.matches("\\s+"))) {
                if (currentEntry != null) {
                    if (firstEntry) {
                        addHeader(po, currentEntry);
                        firstEntry = false;
                    } else {
                        po.entries.add(currentEntry);
                    }
                    currentEntry = null;
                }
            } else {
                if (currentEntry == null) {
                    currentEntry = new POEntry();
                }

                if (line.startsWith("#")) {
                    if (line.startsWith("#: ")) {
                        currentEntry.referenceAdd(line.substring(2).trim());
                    } else if (line.startsWith("#, fuzzy")) {
                        currentEntry.fuzzy = true;
                    } else if (line.startsWith("#, ")) {
                        currentEntry.addFlag(line.substring(3).trim());
                    } else if (line.startsWith("#. ")) {
                        currentEntry.addExtractedComment(line.substring(3).trim());
                        //} else if (line.startsWith("#| msgid ")) {
                        //TODO previousUntranslated
                    } else if (line.startsWith("# ")) {
                        currentEntry.addComment(line.substring(2).trim());
                    } else {
                        currentEntry.addUnparsedComment(line.substring(1).trim());
                    }
                } else if (line.startsWith("msgid ")) {
                    currentEntry.untranslated = readString(line.substring("msgid ".length()), lnr);
                } else if (line.startsWith("msgstr ")) {
                    currentEntry.translated = readString(line.substring("msgstr ".length()), lnr);
                } else {
                    //TODO read plural stuff
                }
            }
        }

        if (currentEntry != null) {
            if (firstEntry) {
                addHeader(po, currentEntry);
                firstEntry = false;
            } else {
                po.entries.add(currentEntry);
            }
        }
        return po;
    }

    private void addHeader(POFile po, POEntry entry) {
        if ((entry.untranslated != null) && (entry.untranslated.length() > 0)) {
            // Not well formated file, allow
            po.entries.add(entry);
        } else {
            po.header = entry;
            entry.untranslated = "";
        }
    }

    private String readString(String line, LineNumberReader lnr) throws IOException {
        StringBuilder b = new StringBuilder();
        while (line != null) {
            line = line.trim();
            if (!line.startsWith("\"") || !line.endsWith("\"")) {
                throw new IOException("invalid format [" + line + "] at line#" + (lnr.getLineNumber() + 1));
            }
            line = line.substring(1, line.length() - 1);
            b.append(line.replace("\\n", "\n"));

            // peek next line
            lnr.mark(1024);
            line = lnr.readLine();
            if ((line == null) || !line.startsWith("\"")) {
                // push back 
                lnr.reset();
                break;
            }
        }
        return b.toString();
    }
}
