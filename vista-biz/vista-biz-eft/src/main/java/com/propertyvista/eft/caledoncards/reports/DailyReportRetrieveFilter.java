/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards.reports;

import java.io.File;
import java.util.Locale;

import com.propertyvista.eft.AbstractSftpRetrieveFilter;
import com.propertyvista.server.sftp.SftpFile;

class DailyReportRetrieveFilter extends AbstractSftpRetrieveFilter<SftpFile> {

    private final String cardsReconciliationId;

    public DailyReportRetrieveFilter(File targetDirectory, String cardsReconciliationId) {
        super(targetDirectory);
        this.cardsReconciliationId = cardsReconciliationId;
    }

    @Override
    public SftpFile accept(String directoryName, String fileName) {
        // 20140531_003631_PROPERTYVISTA.CSV
        if (!fileName.contains(cardsReconciliationId + ".") || !fileName.toLowerCase(Locale.ENGLISH).endsWith(".csv")) {
            return null;
        }
        if (!fileName.matches("^\\d{8}_\\d{6}_.+")) {
            return null;
        }

        if (existsLoadedOrProcessed(fileName)) {
            return null;
        }

        SftpFile sftpFile = new SftpFile();
        sftpFile.localFile = new File(targetDirectory, fileName);
        return sftpFile;
    }
}
