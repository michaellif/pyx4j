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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class POFile {

    public List<String> comments = new Vector<String>();

    public Map<String, String> headers = new LinkedHashMap<String, String>();

    public List<POEntry> entries = new Vector<POEntry>();

    public void createDefaultHeader() {
        comments.add("# SOME DESCRIPTIVE TITLE.");
        comments.add("# Copyright (C) YEAR THE PACKAGE'S COPYRIGHT HOLDER");
        comments.add("# This file is distributed under the same license as the PACKAGE package.");
        comments.add("# FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.");
        comments.add("#");
        comments.add("#, fuzzy");

        headers.put("Project-Id-Version", "PACKAGE VERSION");
        headers.put("Report-Msgid-Bugs-To", "");
        headers.put("POT-Creation-Date", new SimpleDateFormat("yyyy-MM-dd kk:mmZ").format(new Date()));
        headers.put("PO-Revision-Date", "YEAR-MO-DA HO:MI+ZONE");
        headers.put("Last-Translator", "FULL NAME <EMAIL@ADDRESS>");
        headers.put("Language-Team", "LANGUAGE <LL@li.org>");
        headers.put("MIME-Version", "1.0");
        headers.put("Content-Type", "text/plain; charset=UTF-8");
        headers.put("Content-Transfer-Encoding", "8bit");
    }
}
