/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance;

public class MaintenanceException extends Exception {

    private static final long serialVersionUID = 1L;

    public MaintenanceException() {
        super();
    }

    public MaintenanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaintenanceException(String message) {
        super(message);
    }

    public MaintenanceException(Throwable cause) {
        super(cause);
    }

}
