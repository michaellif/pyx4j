/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;

import com.propertyvista.interfaces.importer.model.ImportInformation;

class ProcessingResponseReport {

    protected ReportTableFormatter formatter;

    protected int messagesCount = 0;

    ProcessingResponseReport() {
        this.formatter = new ReportTableXLSXFormatter();
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    protected void createHeader() {
        formatter.header("Sheet");
        formatter.header("Row");
        formatter.header("Message");
        formatter.newRow();
    }

    void addMessage(ImportInformation info) {
        if (messagesCount == 0) {
            createHeader();
        }
        messagesCount++;
        formatter.cell(info.sheet().getValue());
        formatter.cell(info.row().getValue());
        formatter.cell(info.message().getValue());
        formatter.newRow();
    }

    protected void createDownloadable(String fileName) {
        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        d.save(fileName);
    }

}
