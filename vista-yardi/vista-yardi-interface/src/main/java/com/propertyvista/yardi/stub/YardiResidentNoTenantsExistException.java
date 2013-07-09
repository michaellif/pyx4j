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
 * @version $Id$
 */
package com.propertyvista.yardi.stub;

public class YardiResidentNoTenantsExistException extends Exception {

    private static final long serialVersionUID = 1L;

    public YardiResidentNoTenantsExistException() {
        super();
    }

    public YardiResidentNoTenantsExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public YardiResidentNoTenantsExistException(String message) {
        super(message);
    }

    public YardiResidentNoTenantsExistException(Throwable cause) {
        super(cause);
    }
}
