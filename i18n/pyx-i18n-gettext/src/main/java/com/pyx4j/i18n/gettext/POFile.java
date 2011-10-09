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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * The Format of PO Files http://www.gnu.org/s/hello/manual/gettext/PO-Files.html
 * 
 */
public class POFile {

    public POEntry header = new POEntry();

    public List<POEntry> entries = new Vector<POEntry>();

    public POFile() {
    }

    public void createDefaultHeader() {
        header.untranslated = "";
        header.translated = "";

        header.addComment("SOME DESCRIPTIVE TITLE.");
        header.addComment("Copyright (C) YEAR THE PACKAGE'S COPYRIGHT HOLDER");
        header.addComment("This file is distributed under the same license as the PACKAGE package.");
        header.addComment("FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.");
        header.addComment("");

        addHeader("Project-Id-Version", "PACKAGE VERSION");
        addHeader("Report-Msgid-Bugs-To", "");
        addHeader("POT-Creation-Date", new SimpleDateFormat("yyyy-MM-dd kk:mmZ").format(new Date()));
        addHeader("PO-Revision-Date", "YEAR-MO-DA HO:MI+ZONE");
        addHeader("Last-Translator", "FULL NAME <EMAIL@ADDRESS>");
        addHeader("Language-Team", "LANGUAGE <LL@li.org>");
        addHeader("MIME-Version", "1.0");
        addHeader("Content-Type", "text/plain; charset=UTF-8");
        addHeader("Content-Transfer-Encoding", "8bit");
    }

    public void addHeader(String name, String value) {
        header.translated += name + ": " + value + "\n";
    }

    public POFile cloneForTranslation() {
        POFile po = new POFile();
        po.header = this.header.cloneEntry();
        for (POEntry entry : this.entries) {
            po.entries.add(entry.cloneForTranslation());
        }
        return po;
    }
}
