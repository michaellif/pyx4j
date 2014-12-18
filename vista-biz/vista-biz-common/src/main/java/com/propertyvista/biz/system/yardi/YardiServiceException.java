/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2012
 * @author Mykola
 */
package com.propertyvista.biz.system.yardi;

public class YardiServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public YardiServiceException() {
        super();
    }

    public YardiServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public YardiServiceException(String message) {
        super(message);
    }

    public YardiServiceException(Throwable cause) {
        super(cause);
    }

}
