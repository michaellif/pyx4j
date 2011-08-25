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

import java.util.Collection;
import java.util.Vector;

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

    TIF(new String[] { "tif", "tiff" }, "Image"),

    GIF("gif", "Image"),

    JPEG(new String[] { "jpg", "jpeg" }, "Image"),

    PNG("png", "Image"),

    BMP("bmp", "Image"),

    JAVA_SERIALIZED("ser", "Java Serialized Object");

    private String[] extensions;

    private String name;

    DownloadFormat(String extension, String name) {
        this.extensions = new String[] { extension };
        this.name = name;
    }

    DownloadFormat(String[] extensions, String name) {
        this.extensions = extensions;
        this.name = name;
    }

    /**
     * get name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get default extension
     * 
     * @return default extension
     */
    public String getExtension() {
        return extensions[0];
    }

    /**
     * get all extensions
     * 
     * @return array of all extensions
     */
    public String[] getExtensions() {
        return extensions;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Collection<String> getExtensions(Collection<DownloadFormat> formats) {
        Collection<String> extensions = new Vector<String>();
        for (DownloadFormat f : formats) {
            for (String extension : f.getExtensions()) {
                extensions.add(extension);
            }
        }
        return extensions;
    }

    public static DownloadFormat valueByExtension(String ext) {
        ext = ext.toLowerCase();
        for (DownloadFormat df : DownloadFormat.values()) {
            for (String extension : df.getExtensions()) {
                if (extension.equals(ext)) {
                    return df;
                }
            }
        }
        throw new IllegalArgumentException();
    }
}
