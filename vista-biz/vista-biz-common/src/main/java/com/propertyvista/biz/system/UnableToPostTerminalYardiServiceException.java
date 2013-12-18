/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

/**
 * We can't post Receipt again, The error will not be corrected automatically.
 */
public class UnableToPostTerminalYardiServiceException extends YardiServiceException {

    private static final long serialVersionUID = 1L;

    public UnableToPostTerminalYardiServiceException(String message) {
        super(message);
    }
}
