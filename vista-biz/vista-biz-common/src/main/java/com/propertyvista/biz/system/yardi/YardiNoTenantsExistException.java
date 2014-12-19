/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2013
 * @author VladL
 */
package com.propertyvista.biz.system.yardi;


public class YardiNoTenantsExistException extends YardiServiceException {

    private static final long serialVersionUID = 1L;

    public YardiNoTenantsExistException() {
        super();
    }

    public YardiNoTenantsExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public YardiNoTenantsExistException(String message) {
        super(message);
    }

    public YardiNoTenantsExistException(Throwable cause) {
        super(cause);
    }
}
