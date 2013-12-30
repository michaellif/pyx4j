/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoneft.simulator;

import java.io.File;

import com.propertyvista.eft.caledoneft.CaledonFundsTransferDirectories;
import com.propertyvista.eft.caledoneft.CaledonFundsTransferFileType;
import com.propertyvista.eft.caledoneft.CaledonFundsTransferSftpFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;
import com.propertyvista.server.sftp.SftpRetrieveFilter;

public class CaledonFundsTransferSimulatorSftpRetrieveFilter implements SftpRetrieveFilter<CaledonFundsTransferSftpFile> {

    private final File targetDirectory;

    public CaledonFundsTransferSimulatorSftpRetrieveFilter(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    @Override
    public CaledonFundsTransferSftpFile accept(String directoryName, String fileName) {
        // file name match;
        // Only load PadFiles.
        if (fileName.contains(FundsTransferAckFile.FileNameSufix) || fileName.contains(FundsReconciliationFile.FileNameSufix)) {
            return null;
        }

        File dst = new File(targetDirectory, fileName);
        File dst2 = new File(new File(targetDirectory, "processed"), fileName);
        if ((dst.exists()) || (dst2.exists())) {
            return null;
        } else {
            CaledonFundsTransferSftpFile sftpFile = new CaledonFundsTransferSftpFile();
            sftpFile.fundsTransferType = CaledonFundsTransferDirectories.getFundsTransferTypeByDirectory(directoryName);
            sftpFile.fileType = CaledonFundsTransferFileType.PadFile;
            sftpFile.localFile = dst;
            return sftpFile;
        }

    }
}
