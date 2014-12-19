/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-26
 * @author ArtyomB
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

public class ReportFileCreatorImpl implements ReportFileCreator {

    private static final Logger log = LoggerFactory.getLogger(ReportFileCreator.class);

    private final File file;

    public ReportFileCreatorImpl(String reportFileNameBase, File dirReports) {

        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                log.error("Unable to create directory {}", dirReports.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirReports.getAbsolutePath()));
            }
        }

        file = createFile(dirReports, reportFileNameBase);
        log.debug("creating report file {}", file);
    }

    @Override
    public void report(byte[] data) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data);
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private File createFile(File dirReports, String baseFileName) {
        String extension = "";
        String base = "";
        if (baseFileName.lastIndexOf(".") != -1) {
            extension = baseFileName.substring(baseFileName.lastIndexOf("."));
            base = baseFileName.substring(0, baseFileName.lastIndexOf("."));
        }
        return makeFileName(dirReports, base, extension);
    }

    protected File makeFileName(File dirReports, String baseFileName, String extension) {
        File dst = new File(dirReports, baseFileName + extension);

        int attemptCount = 0;
        while (dst.exists()) {
            attemptCount++;
            if (attemptCount > 1000) {
                log.warn("File {} already exists", dst.getAbsolutePath());
                return new File(dirReports, baseFileName + "." + System.currentTimeMillis() + extension);
            }
            dst = new File(dirReports, baseFileName + "." + attemptCount + extension);
        }
        return dst;
    }

}
