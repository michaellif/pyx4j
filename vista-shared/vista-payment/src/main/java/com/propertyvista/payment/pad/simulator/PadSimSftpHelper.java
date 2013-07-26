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
package com.propertyvista.payment.pad.simulator;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.util.FileUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.operations.domain.payment.pad.FundsTransferType;

public class PadSimSftpHelper {

    private static final Logger log = LoggerFactory.getLogger(PadSimSftpHelper.class);

    public static File buildSftpRootDir() {

        AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());

        File dir = config.getCaledonSimulatorSftpDirectory();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("Unable to create directory {}", dir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dir.getAbsolutePath()));
            }
        }

        for (FundsTransferType fundsTransferType : FundsTransferType.values()) {
            try {
                FileUtils.forceMkdir(new File(dir, fundsTransferType.getDirectoryName("in")));
                FileUtils.forceMkdir(new File(dir, fundsTransferType.getDirectoryName("out")));
            } catch (IOException e) {
                throw new Error(e);
            }
        }

        return dir;
    }

}
