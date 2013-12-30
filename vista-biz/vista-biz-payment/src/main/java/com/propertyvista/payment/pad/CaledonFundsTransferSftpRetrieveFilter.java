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
package com.propertyvista.payment.pad;

import java.io.File;

import com.propertyvista.operations.domain.payment.pad.FundsReconciliationFile;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.server.sftp.SftpRetrieveFilter;

public class CaledonFundsTransferSftpRetrieveFilter implements SftpRetrieveFilter<CaledonFundsTransferSftpFile> {

    private final File targetDirectory;

    private final String companyId;

    private final CaledonFundsTransferFileType fileType;

    public CaledonFundsTransferSftpRetrieveFilter(File targetDirectory, String companyId, CaledonFundsTransferFileType fileType) {
        this.targetDirectory = targetDirectory;
        this.companyId = companyId;
        this.fileType = fileType;
    }

    @Override
    public CaledonFundsTransferSftpFile accept(String directoryName, String fileName) {
        // file name match;
        switch (fileType) {
        case Acknowledgement:
            //YYYYMMDDhhmmss_pad.COMPANYID_acknowledgement.csv
            if (!(fileName.contains("." + companyId + "_") && fileName.contains(PadAckFile.FileNameSufix))) {
                return null;
            }
            break;
        case Reconciliation:
            // YYYYMMDDhhmmss_reconciliation_rpt_pad.COMPANYID
            if (!(fileName.contains(FundsReconciliationFile.FileNameSufix) && fileName.endsWith("." + companyId))) {
                return null;
            }
            break;
        default:
            return null;
        }

        File dst = new File(targetDirectory, fileName);
        File dst2 = new File(new File(targetDirectory, "processed"), fileName);
        if ((dst.exists()) || (dst2.exists())) {
            return null;
        } else {
            CaledonFundsTransferSftpFile sftpFile = new CaledonFundsTransferSftpFile();
            sftpFile.fundsTransferType = CaledonFundsTransferDirectories.getFundsTransferTypeByDirectory(directoryName);
            sftpFile.fileType = fileType;
            sftpFile.localFile = dst;
            return sftpFile;
        }

    }
}
