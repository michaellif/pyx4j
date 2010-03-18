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
 * @author go.david
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Local Executable to create report
 */
public class CreateReportMain {

    public static void main(String[] args) throws Throwable {

        ByteArrayOutputStream xslTransformationBuffer = new ByteArrayOutputStream();
        // Create reusable XSL
        {
            InputStream binaryZip = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template.docx");
            InputStream xslTransformation = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-transform.xsl");
            ReportsProcessor.createTransformation(binaryZip, xslTransformation, xslTransformationBuffer);
        }

        // Create file
        {
            InputStream binaryZip = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template.docx");
            InputStream data = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-data.xml");
            InputStream xslTransformation = new ByteArrayInputStream(xslTransformationBuffer.toByteArray());

            ByteArrayOutputStream report = new ByteArrayOutputStream();
            ReportsProcessor.createReport(binaryZip, xslTransformation, data, report);

            // RandomAccessFile is JRE Class White List  in GAE. next code will not run on GAE

            RandomAccessFile file = new RandomAccessFile("target/text-report.docx", "rw");
            file.seek(0);
            byte[] repData = report.toByteArray();
            file.write(repData);
            file.setLength(repData.length);
            file.close();
            System.out.println("file created");
        }

    }
}
