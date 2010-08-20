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
 * Created on Mar 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

public class ReportsProcessor {

    private static final Logger log = LoggerFactory.getLogger(ReportsProcessor.class);

    public static TransformerFactory newTransformerFactoryInstance() {
        // return TransformerFactory.newInstance();
        // Use XSLT since default is not available on GAE.
        return TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", Thread.currentThread().getContextClassLoader());
    }

    public static void transform(InputStream input, InputStream xslTransformation, OutputStream transformationOut) {
        try {
            Transformer transformer = newTransformerFactoryInstance().newTransformer(new StreamSource(xslTransformation));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new StreamSource(input), new StreamResult(new OutputStreamWriter(transformationOut, "UTF-8")));
        } catch (Throwable e) {
            log.error("Unable to perform transformation", e);
            throw new RuntimeException("Transformation error", e);
        }
    }

    public static void createTransformation(InputStream binaryZip, InputStream xslTransformation, OutputStream transformationOut) {
        ZipInputStream zip = new ZipInputStream(binaryZip);
        try {
            while (true) {
                ZipEntry entry = zip.getNextEntry();
                if (entry == null) {
                    break;
                }
                if (entry.getName().equals("word/document.xml")) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    ByteArrayInputStream in = null;
                    byte buf[] = new byte[0xFF];
                    try {
                        while (zip.available() != 0) {
                            int size = zip.read(buf);
                            if (size == -1) {
                                break;
                            }
                            b.write(buf, 0, size);
                        }
                        in = new ByteArrayInputStream(b.toByteArray());
                        // Output original document.xml entry to template.xml
                        transform(in, xslTransformation, transformationOut);
                        zip.closeEntry();
                    } finally {
                        IOUtils.closeQuietly(b);
                        IOUtils.closeQuietly(in);
                    }
                    break;
                }
            }
        } catch (Throwable e) {
            log.error("Unable to create transformation", e);
            throw new RuntimeException("Template error", e);
        } finally {
            IOUtils.closeQuietly(zip);
        }
    }

    public static void createReport(InputStream binaryZip, InputStream xslTransformation, InputStream data, OutputStream report) {
        createReport(binaryZip, xslTransformation, data, report, null);
    }

    public static void createReport(InputStream binaryZip, InputStream xslTransformation, InputStream data, OutputStream report, Map<String, byte[]> media) {
        ZipInputStream zipIn = new ZipInputStream(binaryZip);
        ZipOutputStream zipOut = new ZipOutputStream(report);
        try {
            while (true) {
                ZipEntry entry = zipIn.getNextEntry();
                if (entry == null) {
                    break;
                }
                zipOut.putNextEntry(new ZipEntry(entry.getName()));
                if (entry.getName().equals("word/document.xml")) {
                    // Transform data with document-transform
                    transform(data, xslTransformation, zipOut);
                } else if ((media != null) && media.containsKey(entry.getName())) {
                    zipOut.write(media.get(entry.getName()));
                } else {
                    // Copy data
                    byte buf[] = new byte[0xFF];
                    while (zipIn.available() != 0) {
                        int size = zipIn.read(buf);
                        if (size == -1) {
                            break;
                        }
                        zipOut.write(buf, 0, size);
                    }
                }
                zipOut.closeEntry();
            }
        } catch (Throwable e) {
            log.error("Unable to create report", e);
            throw new RuntimeException("Report error", e);
        } finally {
            IOUtils.closeQuietly(zipIn);
            IOUtils.closeQuietly(zipOut);
        }
    }

    public static void createPDF(InputStream xslTransformation, InputStream data, OutputStream report) {
        ByteArrayOutputStream memOut = null;
        ByteArrayInputStream memIn = null;

        // Transform data with document-transform
        try {
            memOut = new ByteArrayOutputStream();
            transform(data, xslTransformation, memOut);

            memIn = new ByteArrayInputStream(memOut.toByteArray());
            HtmlToPDFParser.parse(memIn, report);
        } catch (Throwable e) {
            log.error("Unable to create report", e);
            throw new RuntimeException("Report error", e);
        } finally {
            IOUtils.closeQuietly(memOut);
            IOUtils.closeQuietly(memIn);
        }
    }

}
