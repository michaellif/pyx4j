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
package com.propertyvista.eft.caledoneft;

import java.io.File;

import com.propertyvista.eft.AbstractSftpRetrieveFilter;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;

public class CaledonFundsTransferSftpRetrieveFilter extends AbstractSftpRetrieveFilter<CaledonFundsTransferSftpFile> {

    private final String companyId;

    private final CaledonFundsTransferFileType fileType;

    public CaledonFundsTransferSftpRetrieveFilter(File targetDirectory, String companyId, CaledonFundsTransferFileType fileType) {
        super(targetDirectory);
        this.companyId = companyId;
        this.fileType = fileType;
    }

    @Override
    public CaledonFundsTransferSftpFile accept(String directoryName, String fileName) {
        // file name match;
        switch (fileType) {
        case Acknowledgement:
            //YYYYMMDDhhmmss_pad.COMPANYID_acknowledgement.csv
            if (!(fileName.contains("." + companyId + "_") && fileName.contains(FundsTransferAckFile.FileNameSufix))) {
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

        if (existsLoadedOrProcessed(fileName)) {
            return null;
        }

        CaledonFundsTransferSftpFile sftpFile = new CaledonFundsTransferSftpFile();
        sftpFile.fundsTransferType = CaledonFundsTransferDirectories.getFundsTransferTypeByDirectory(directoryName);
        sftpFile.fileType = fileType;
        sftpFile.localFile = new File(targetDirectory, fileName);
        return sftpFile;
    }

}
