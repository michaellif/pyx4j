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
 */
package com.pyx4j.i18n.gettext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.pyx4j.i18n.shared.I18n;

public class POCatalog implements Translator {

    final String lang;

    final File poDirectory;

    private final Map<String, String> translations = new HashMap<String, String>();

    private boolean updated = false;

    public static class CatalogIgnore {

        public boolean messageFormat = false;

        public boolean multiLine = false;

    }

    private CatalogIgnore catalogIgnore;

    public static boolean debug = false;

    public POCatalog(String lang, boolean loadDefault) {
        this(lang, loadDefault, null);
    }

    public POCatalog(String lang, boolean loadDefault, CatalogIgnore catalogIgnore) {
        this.lang = lang;
        this.catalogIgnore = catalogIgnore;
        if (loadDefault) {
            poDirectory = new File(System.getProperty("user.home"), ".po-catalog");
            readMainFile();
        } else {
            poDirectory = null;
        }
    }

    public POCatalog(Locale locale) throws IOException {
        poDirectory = null;
        String fullLangName = locale.getLanguage() + "_" + locale.getCountry();
        POFile po = new POFileReader().readResource("translations/" + fullLangName + ".po");
        if (po != null) {
            this.lang = fullLangName;
        } else {
            this.lang = locale.getLanguage();
            po = new POFileReader().readResource("translations/" + this.lang + ".po");
            if (po == null) {
                throw new FileNotFoundException("translation file not found");
            }

        }
        buildTranslations(po);
    }

    private File mainLandFile() {
        return new File(poDirectory, lang + ".po");
    }

    private void readMainFile() {
        File file = mainLandFile();
        if (file.canRead()) {
            readFile(file);
        }
    }

    private void readFile(File file) {
        POFile po;
        try {
            POFileReader r = new POFileReader();
            po = r.read(file);
        } catch (IOException e) {
            throw new RuntimeException("POFile " + file.getAbsolutePath() + " read error", e);
        }
        buildTranslations(po);
    }

    public void loadCatalog(File catalogDirectory) {
        File file = new File(catalogDirectory, lang + ".po");
        if (file.canRead()) {
            readFile(file);
        }
    }

    private void buildTranslations(POFile po) {
        for (POEntry entry : po.entries) {
            if ((entry.translated != null) && (entry.translated.length() != 0)) {

                if (catalogIgnore != null) {
                    if (catalogIgnore.messageFormat) {
                        if (entry.contanisFlag("java-format")) {
                            continue;
                        }
                        if (entry.translated.contains("{")) {
                            continue;
                        }
                        if (entry.untranslated.contains("{")) {
                            continue;
                        }
                    }
                    if (catalogIgnore.multiLine) {
                        if (entry.untranslated.contains("\n") || entry.translated.contains("\n")) {
                            continue;
                        }
                    }
                }

                if ((entry.context != null) && (entry.context.length() > 0)) {
                    translations.put(entry.context + I18n.CONTEXT_GLUE + entry.untranslated, entry.translated);
                } else {
                    translations.put(entry.untranslated, entry.translated);
                }
            }
        }
    }

    @Override
    public String translate(String context, String text) {
        return translations.get(key(context, text));
    }

    private String key(String context, String text) {
        if ((context != null) && (context.length() > 0)) {
            return context + I18n.CONTEXT_GLUE + text;
        } else {
            return text;
        }
    }

    public void update(String context, String text, String translation) {
        if (translation != null) {
            translations.put(key(context, text), translation);
            updated = true;
        }
    }

    public int size() {
        return translations.size();
    }

    public void write() {
        if (!updated) {
            if (debug) {
                System.out.println("POCatalog " + lang + " not updated");
            }
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
            int cIdx = pe.untranslated.indexOf(I18n.CONTEXT_GLUE);
            if (cIdx != -1) {
                pe.context = pe.untranslated.substring(0, cIdx);
                pe.untranslated = pe.untranslated.substring(cIdx + 1);
            }

            pe.translated = entry.getValue();
            po.entries.add(pe);
        }
        Collections.sort(po.entries, new POEntry.ByTextComparator());
        try {
            POFileWriter poWriter = new POFileWriter();
            poWriter.write(mainLandFile(), po);
            if (debug) {
                System.out.println("POCatalog " + lang + " file " + mainLandFile().getAbsolutePath() + " updated");
            }
        } catch (IOException e) {
            throw new RuntimeException("POFile " + mainLandFile().getAbsolutePath() + " write error", e);
        }
    }

}
