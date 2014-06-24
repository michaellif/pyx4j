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

import com.propertyvista.eft.AbstractSftpRetrieveFilter;
import com.propertyvista.server.sftp.SftpFile;

class CardsReconciliationCardTotalRetrieveFilter extends AbstractSftpRetrieveFilter<SftpFile> {

    private final String expectedFileName;

    public CardsReconciliationCardTotalRetrieveFilter(File targetDirectory, String dailyconsolidatedtotalsFileName) {
        super(targetDirectory);
        expectedFileName = dailyconsolidatedtotalsFileName.replace("dailyconsolidatedtotals", "dailycardtotals");
    }

    @Override
    public SftpFile accept(String directoryName, String fileName) {
        if (!fileName.equals(expectedFileName)) {
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
