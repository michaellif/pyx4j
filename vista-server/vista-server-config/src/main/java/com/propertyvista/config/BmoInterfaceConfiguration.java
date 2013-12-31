/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-01
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

public abstract class BmoInterfaceConfiguration extends SftpConnectionConfiguration {

    public abstract String bmoMailboxNumber();

    public abstract boolean removeReceivedFileFromSftpHost();

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(super.toString());
        b.append("bmoMailboxNumber                  : ").append(bmoMailboxNumber()).append("\n");
        b.append("removeReceivedFileFromSftpHost    : ").append(removeReceivedFileFromSftpHost()).append("\n");
        return b.toString();
    }
}
