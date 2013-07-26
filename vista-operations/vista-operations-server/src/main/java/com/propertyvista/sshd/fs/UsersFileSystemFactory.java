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
package com.propertyvista.sshd.fs;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;

public class UsersFileSystemFactory implements FileSystemFactory {

    private final Map<String, File> usersRootDirectories;

    public UsersFileSystemFactory(Map<String, File> usersRootDirectories) {
        super();
        this.usersRootDirectories = usersRootDirectories;
    }

    @Override
    public FileSystemView createFileSystemView(Session session) throws IOException {
        File rootDir = usersRootDirectories.get(session.getUsername());
        return new SimpleFileSystemView(rootDir, session.getUsername());
    }

}
