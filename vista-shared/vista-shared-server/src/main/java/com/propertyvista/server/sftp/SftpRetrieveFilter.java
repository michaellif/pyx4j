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
package com.propertyvista.server.sftp;

public interface SftpRetrieveFilter<E extends SftpFile> {

    /**
     * Tests if a specified file should be retrieved and Create new SftpFile that indicates the local destination.
     * 
     * @param directoryName
     *            the directory in which the file was found.
     * @param fileName
     *            the name of the file.
     * @return <code>new SftpFile</code> if and only if the file should be download; <code>null</code> otherwise.
     */
    E accept(String directoryName, String fileName);

}
