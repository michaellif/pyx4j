/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.payment.pad.CaledonPadFileWriter;
import com.propertyvista.payment.pad.CaledonPadSftpClient;

public class PadCaledon {

    private static final Logger log = LoggerFactory.getLogger(PadCaledon.class);

    private static Object lock = new Object();

    public PadFile sendPadFile() {
        File padWorkdir = new File(new File("vista-work"), LoggerConfig.getContextName());
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }

        PadFile padFile;
        synchronized (lock) {
            EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
            criteria.add(PropertyCriterion.in(criteria.proto().status(), PadFile.PadFileStatus.Creating, PadFile.PadFileStatus.Error));
            padFile = Persistence.service().retrieve(criteria);
            if (padFile == null) {
                return null;
            }

            padFile.status().setValue(PadFile.PadFileStatus.Sending);
            padFile.sent().setValue(new Date());
            Persistence.service().merge(padFile);
            Persistence.service().commit();
        }

        File file = null;
        try {
            do {
                String filename = new SimpleDateFormat("yyyyMMddmmHHss").format(padFile.sent().getValue());
                file = new File(padWorkdir, filename + ".BIRCHWOOD");
                if (file.exists()) {
                    padFile.sent().setValue(new Date());
                    Persistence.service().merge(padFile);
                    Persistence.service().commit();
                }
            } while (file.exists());

            Persistence.service().retrieveMember(padFile.batches());
            for (PadBatch padBatch : padFile.batches()) {
                Persistence.service().retrieveMember(padBatch.records());
            }

            log.info("sending pad file {}", file.getAbsolutePath());

            CaledonPadFileWriter writer = new CaledonPadFileWriter(padFile, file);
            try {
                writer.write();
            } finally {
                writer.close();
            }

            // Save Sent totals
            for (PadBatch padBatch : padFile.batches()) {
                Persistence.service().persist(padBatch);
            }

            String errorMessage = new CaledonPadSftpClient().sftpPut(file);
            if (errorMessage != null) {
                throw new Error(errorMessage);
            }
            log.info("pad file sent {}", file.getAbsolutePath());
        } catch (Throwable e) {
            log.error("pad write error", e);
            //Error recovery
            padFile.status().setValue(PadFile.PadFileStatus.Error);
            Persistence.service().merge(padFile);
            Persistence.service().commit();

            if (file != null) {
                move(file, padWorkdir, "error");
            }

            throw new Error(e.getMessage());
        }

        move(file, padWorkdir, "sent");

        padFile.status().setValue(PadFile.PadFileStatus.Sent);
        Persistence.service().merge(padFile);
        Persistence.service().commit();

        padFile.batches().setAttachLevel(AttachLevel.Detached);
        return padFile;
    }

    private String uniqueNameTimeStamp() {
        return new SimpleDateFormat("-yyMMdd-HHmmss.S").format(new Date());
    }

    private File move(File file, File baseDir, String subdir) {
        File dir = new File(baseDir, subdir);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            log.error("Unable to create directory {}", dir.getAbsolutePath());
            return null;
        } else {
            File dst = new File(dir, file.getName());
            int attemptCount = 0;
            while (dst.exists()) {
                attemptCount++;
                if (attemptCount > 10) {
                    log.error("File {} already exists", dst.getAbsolutePath());
                    return null;
                }
                dst = new File(dir, file.getName() + uniqueNameTimeStamp());
            }
            if (!file.renameTo(dst)) {
                log.error("Rename {} to {} failed", file.getAbsolutePath(), dst.getAbsolutePath());
                return null;
            } else {
                return dst;
            }
        }
    }
}
