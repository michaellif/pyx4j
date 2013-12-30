/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.dbp;

import java.io.File;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.server.sftp.SftpClient;
import com.propertyvista.server.sftp.SftpFile;

public class BmoSftpClient {

    private BmoInterfaceConfiguration configuration() {
        return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBmoInterfaceConfiguration();
    }

    public SftpFile receiveFile(File targetDirectory) throws SftpTransportConnectionException {
        return SftpClient.receiveFile(configuration(), new BmoSftpRetrieveFilter(targetDirectory, configuration().bmoMailboxNumber()), ".");
    }

    public void removeFile(String fileName) throws SftpTransportConnectionException {
        if (configuration().removeReceivedFileFromSftpHost()) {
            SftpClient.removeFile(configuration(), ".", fileName);
        }
    }

}
