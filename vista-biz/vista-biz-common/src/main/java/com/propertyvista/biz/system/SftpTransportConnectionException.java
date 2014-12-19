/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-13
 * @author vlads
 */
package com.propertyvista.biz.system;

/**
 * Recoverable connection error. e.g. no attempt to send or receive file to remote interface was made
 */
@SuppressWarnings("serial")
public class SftpTransportConnectionException extends Exception {

    public SftpTransportConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
