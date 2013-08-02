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
package com.propertyvista.config;

import com.pyx4j.config.server.Credentials;

public abstract class SftpConnectionConfiguration {

    public abstract String sftpHost();

    public abstract int sftpPort();

    public abstract Credentials sftpCredentials();

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                : ").append(getClass().getName()).append("\n");
        b.append("sftpHost                          : ").append(sftpHost()).append("\n");
        b.append("sftpPort                          : ").append(sftpPort()).append("\n");
        b.append("sftpCredentials                   : ").append(sftpCredentials().userName).append("\n");
        return b.toString();
    }

}
