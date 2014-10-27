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
package com.propertyvista.eft;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.server.sftp.SftpFile;
import com.propertyvista.server.sftp.SftpRetrieveFilter;

public abstract class AbstractSftpRetrieveFilter<E extends SftpFile> implements SftpRetrieveFilter<E> {

    private static final Logger log = LoggerFactory.getLogger(AbstractSftpRetrieveFilter.class);

    protected final File targetDirectory;

    public AbstractSftpRetrieveFilter(File targetDirectory) {
        super();
        this.targetDirectory = targetDirectory;
    }

    protected boolean existsLoadedOrProcessed(String fileName) {
        File dst = new File(targetDirectory, fileName);
        if (dst.exists()) {
            log.debug("{} already receiving file {}", this.getClass().getSimpleName(), dst);
            return true;
        } else {
            File dst2 = new File(new File(targetDirectory, "processed"), fileName);
            if (dst2.exists()) {
                log.debug("{} already processed file {}", this.getClass().getSimpleName(), dst);
                return true;
            } else {
                return false;
            }
        }
    }
}
