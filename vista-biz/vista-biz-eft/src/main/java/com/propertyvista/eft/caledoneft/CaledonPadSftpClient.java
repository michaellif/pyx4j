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
package com.propertyvista.eft.caledoneft;

import java.io.File;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.eft.caledoneft.simulator.CaledonFundsTransferSimulatorSftpRetrieveFilter;
import com.propertyvista.server.sftp.SftpClient;

public class CaledonPadSftpClient {

    private final CaledonFundsTransferConfiguration configuration;

    public CaledonPadSftpClient() {
        configuration = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration();
    }

    public void sftpPut(FundsTransferType fundsTransferType, File file) throws SftpTransportConnectionException {
        SftpClient.sftpPut(configuration, file, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.postDst));
    }

    public void sftpPutSim(FundsTransferType fundsTransferType, File file) throws SftpTransportConnectionException {
        if (!ApplicationMode.isDevelopment()) {
            throw new UserRuntimeException("FundsTransfer Simulator is not available");
        }
        SftpClient.sftpPut(configuration, file, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.getSrc));
    }

    public CaledonFundsTransferSftpFile receiveFiles(String companyId, CaledonFundsTransferFileType padFileType, File targetDirectory)
            throws SftpTransportConnectionException {
        return SftpClient.receiveFile(configuration, new CaledonFundsTransferSftpRetrieveFilter(targetDirectory, companyId, padFileType),
                CaledonFundsTransferDirectories.allGetDirectories());
    }

    public CaledonFundsTransferSftpFile receiveFilesSim(File targetDirectory) throws SftpTransportConnectionException {
        if (!ApplicationMode.isDevelopment()) {
            throw new UserRuntimeException("FundsTransfer Simulator is not available");
        }
        return SftpClient.receiveFile(configuration, new CaledonFundsTransferSimulatorSftpRetrieveFilter(targetDirectory),
                CaledonFundsTransferDirectories.allPostDirectories());
    }

    public void removeFile(FundsTransferType fundsTransferType, String fileName) throws SftpTransportConnectionException {
        SftpClient.removeFile(configuration, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.getSrc), fileName);
    }

    public void removeFilesSim(FundsTransferType fundsTransferType, String fileName) throws SftpTransportConnectionException {
        if (!ApplicationMode.isDevelopment()) {
            throw new UserRuntimeException("FundsTransfer Simulator is not available");
        }
        SftpClient.removeFile(configuration, fundsTransferType.getDirectoryName(CaledonFundsTransferDirectories.postDst), fileName);
    }

}
