/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 3, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.eft;

/**
 * Recoverable error. e.g. no attempt to send or receive file to remote interface was made
 * 
 */
@SuppressWarnings("serial")
public class FileCreationException extends Exception {

    public FileCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
