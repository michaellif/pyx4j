/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-17
 * @author vlads
 */
package com.propertyvista.sshd.fs;

import java.io.File;
import java.io.IOException;

import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;

public class SimpleFileSystemFactory implements FileSystemFactory {

    private final File rootDir;

    public SimpleFileSystemFactory(File rootDir) {
        super();
        this.rootDir = rootDir;
    }

    @Override
    public FileSystemView createFileSystemView(Session session) throws IOException {
        return new SimpleFileSystemView(rootDir, session.getUsername());
    }

}
