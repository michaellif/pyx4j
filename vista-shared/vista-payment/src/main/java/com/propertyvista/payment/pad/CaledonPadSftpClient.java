/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad;

import java.io.File;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.server.sftp.SftpClient;
import com.propertyvista.server.sftp.SftpTransportConnectionException;

public class CaledonPadSftpClient {

    private final CaledonFundsTransferConfiguration configuration;

    public CaledonPadSftpClient() {
        configuration = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration();
    }

    private static boolean usePadSimulator() {
        return VistaSystemsSimulationConfig.getConfiguration().usePadSimulator().getValue(Boolean.TRUE);
    }

    public String sftpPut(FundsTransferType fundsTransferType, File file) {
        return SftpClient.sftpPut(configuration, file, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.postDst));
    }

    public String sftpPutSim(FundsTransferType fundsTransferType, File file) {
        if (!usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        return SftpClient.sftpPut(configuration, file, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.getSrc));
    }

    public CaledonFundsTransferSftpFile receiveFiles(String companyId, CaledonFundsTransferFileType padFileType, File targetDirectory)
            throws SftpTransportConnectionException {
        return SftpClient.receiveFile(configuration, new CaledonFundsTransferSftpRetrieveFilter(targetDirectory, companyId, padFileType),
                CaledonFundsTransferDirectories.allGetDirectories());
    }

    public CaledonFundsTransferSftpFile receiveFilesSim(File targetDirectory) throws SftpTransportConnectionException {
        if (!usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        String companyId = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getCaledonCompanyId();
        return SftpClient.receiveFile(configuration, new CaledonFundsTransferSftpRetrieveFilter(targetDirectory, companyId,
                CaledonFundsTransferFileType.PadFile), CaledonFundsTransferDirectories.allPostDirectories());
    }

    public void removeFile(FundsTransferType fundsTransferType, String fileName) {
        SftpClient.removeFile(configuration, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.getSrc), fileName);
    }

    public void removeFilesSim(FundsTransferType fundsTransferType, String fileName) {
        if (!CaledonPadSftpClient.usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        SftpClient.removeFile(configuration, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.postDst), fileName);
    }

}
