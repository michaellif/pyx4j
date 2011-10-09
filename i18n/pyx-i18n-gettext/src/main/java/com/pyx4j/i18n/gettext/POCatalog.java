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
 * Created on Oct 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class POCatalog {

    final String lang;

    final File poDirectory;

    private final Map<String, String> translations = new HashMap<String, String>();

    private boolean updated = false;

    public POCatalog(String lang) {
        this.lang = lang;
        poDirectory = new File(System.getProperty("user.home"), ".po-catalog");
        read();
    }

    private File mainLandFile() {
        return new File(poDirectory, lang + ".po");
    }

    public void read() {
        File file = mainLandFile();
        if (!file.canRead()) {
            return;
        }
        InputStream is = null;
        POFile po;
        try {
            is = new FileInputStream(file);
            POFileReader r = new POFileReader();
            po = r.read(new InputStreamReader(is, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("POFile " + mainLandFile().getAbsolutePath() + " read error", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
        for (POEntry entry : po.entries) {
            translations.put(entry.untranslated, entry.translated);
        }
    }

    public String translate(String text) {
        return translations.get(text);
    }

    public void update(String text, String translation) {
        if (translation != null) {
            translations.put(text, translation);
            updated = true;
        }
    }

    public void write() {
        if (!updated) {
            return;
        }
        if (!poDirectory.isDirectory()) {
            if (!poDirectory.mkdirs()) {
                throw new RuntimeException("Unable to create poDirectory " + poDirectory);
            }
        }
        POFile po = new POFile();
        po.createDefaultHeader();
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            POEntry pe = new POEntry();
            pe.untranslated = entry.getKey();
            pe.translated = entry.getValue();
            po.entries.add(pe);
        }
        Collections.sort(po.entries, new POEntry.ByTextComparator());
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(mainLandFile(), "UTF-8");
            POFileWriter poWriter = new POFileWriter();

            poWriter.write(writer, po);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException("POFile " + mainLandFile().getAbsolutePath() + " write error", e);
        } finally {
            writer.close();
        }
    }

}
