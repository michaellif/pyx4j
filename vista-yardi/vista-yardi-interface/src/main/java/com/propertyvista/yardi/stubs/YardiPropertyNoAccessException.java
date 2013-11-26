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
package com.propertyvista.yardi.stubs;

public class YardiPropertyNoAccessException extends Exception {

    private static final long serialVersionUID = 1L;

    public YardiPropertyNoAccessException() {
        super();
    }

    public YardiPropertyNoAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public YardiPropertyNoAccessException(String message) {
        super(message);
    }

    public YardiPropertyNoAccessException(Throwable cause) {
        super(cause);
    }
}
