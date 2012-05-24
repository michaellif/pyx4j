/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services.sim;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.payment.pad.CaledonPadSftpClient;

public class PadSim {

    private static final Logger log = LoggerFactory.getLogger(PadSim.class);

    private File getPadBaseDir() {
        File padWorkdir = new File(new File("vista-sim"), LoggerConfig.getContextName());
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }
        return padWorkdir;
    }

    public PadSimFile loadPadFile() {
        File padWorkdir = getPadBaseDir();
        List<File> files = new CaledonPadSftpClient().reciveFilesSim(padWorkdir);
        if (files.size() == 0) {
            return null;
        }

        for (File file : files) {
            PadSimFile padFile = new PadSimFileParser().parsReport(file);
            padFile.fileName().setValue(file.getName());
            padFile.status().setValue(PadSimFile.PadSimFileStatus.Loaded);
            Persistence.service().persist(padFile);
            for (PadSimBatch padBatch : padFile.batches()) {
                Persistence.service().persist(padBatch);
            }
            Persistence.service().commit();
            return padFile;
        }
        return null;
    }
}
