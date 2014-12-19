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

import org.apache.sshd.server.filesystem.NativeSshFile;

public class SimpleSshFile extends NativeSshFile {

    protected SimpleSshFile(final String fileName, final File file, final String userName) {
        super(fileName, file, userName);
    }

}
