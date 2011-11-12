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
 * Created on Oct 18, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.download;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.essentials.rpc.report.DownloadFormat;

public class MimeMap {

    private static final Map<String, String> map = new HashMap<String, String>();

    static {
        map.put("txt", "text/plain");
        map.put("csv", "text/csv");
        map.put("html", "text/html");
        map.put("htm", "text/html");
        map.put("xml", "text/xml");
        map.put("gif", "image/gif");
        map.put("jpg", "image/jpeg");
        map.put("jpe", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("png", "image/png");
        map.put("bmp", "image/bmp");
        map.put("java", "text/plain");
        map.put("zip", "application/zip");
        map.put("jar", "application/java-archive");
        map.put("tar", "application/x-tar");
        map.put("bin", "application/octet-stream");
        map.put("odt", "application/vnd.oasis.opendocument.text");
        map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        map.put("doc", "application/msword");
        map.put("pdf", "application/pdf");
        map.put("rtf", "application/rtf");
        map.put("xls", "application/xls");
        map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("pps", "application/vnd.ms-powerpoint");
        map.put("exe", "application/octet-stream");
        map.put("gz", "application/x-gzip");
        map.put("tiff", "image/tiff");
        map.put("tif", "image/tiff");
        map.put("wav", "audio/x-wav");
        map.put("mpeg", "video/mpeg");
        map.put("mpg", "video/mpeg");
        map.put("mpe", "video/mpeg");
        map.put("qt", "video/quicktime");
        map.put("mov", "video/quicktime");
        map.put("avi", "video/x-msvideo");
        map.put("ser", "application/x-java-serialized-object");
    }

    public static String getContentType(String extension) {
        return map.get(extension.toLowerCase());
    }

    public static String getContentType(DownloadFormat downloadFormat) {
        return getContentType(downloadFormat.getExtension());
    }
}
