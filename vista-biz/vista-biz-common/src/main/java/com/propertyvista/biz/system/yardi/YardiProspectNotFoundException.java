/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

public class YardiProspectNotFoundException extends YardiServiceException {

    private static final long serialVersionUID = 1L;

    public YardiProspectNotFoundException() {
        super();
    }

    public YardiProspectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public YardiProspectNotFoundException(String message) {
        super(message);
    }

    public YardiProspectNotFoundException(Throwable cause) {
        super(cause);
    }
}
