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
 * Created on 2010-05-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.report;

public enum DownloadFormat {

    CSV("csv", "Comma-separated values (CSV)"),

    RTF("rtf", "MS Word (RTF)"),

    XLS("xls", "MS Excel"),

    PDF("pdf", "Adobe PDF"),

    DOCX("docx", "Microsoft Office Word 2007 (DOCX)"),

    ODT("odt", "OpenDocument Text Document (ODT)"),

    XML("xml", "XML format"),

    HTML("html", "HTML Document"),

    TXT("txt", "Text file"),

    JAVA_SERIALIZED("ser", "Java Serialized Object");

    private String extension;

    private String name;

    DownloadFormat(String extension, String name) {
        this.extension = extension;
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return name;
    }
}
