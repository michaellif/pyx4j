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

import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.apache.sshd.server.filesystem.NativeFileSystemFactory;

class SimpleFileSystemView implements FileSystemView {

    private final String rootDir;

    // the first and the last character will always be '/'
    // It is always with respect to the root directory.
    private final String currDir;

    private final String userName;

    private final boolean caseInsensitive = true;

    /**
     * Constructor - internal do not use directly, use {@link NativeFileSystemFactory} instead
     */
    public SimpleFileSystemView(File rootDir, String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("user can not be null");
        }
        this.rootDir = rootDir.getAbsolutePath() + "/";
        this.currDir = "/";
        this.userName = userName;
    }

    /**
     * Get file object.
     */
    @Override
    public SshFile getFile(String file) {
        return getFile(currDir, file);
    }

    @Override
    public SshFile getFile(SshFile baseDir, String file) {
        return getFile(baseDir.getAbsolutePath(), file);
    }

    protected SshFile getFile(String dir, String file) {
        // get actual file object
        String physicalName = SimpleSshFile.getPhysicalName(rootDir, dir, file, caseInsensitive);
        File fileObj = new File(physicalName);

        // strip the root directory and return
        String userFileName = physicalName.substring(rootDir.length() - 1);
        return new SimpleSshFile(userFileName, fileObj, userName);
    }

}
