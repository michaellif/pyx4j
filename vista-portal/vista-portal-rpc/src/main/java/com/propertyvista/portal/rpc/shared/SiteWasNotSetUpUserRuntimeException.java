/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared;

import com.pyx4j.rpc.shared.ContainerHandledUserRuntimeException;

/**
 * N.B. this class is referenced in web.xml text file. exit xml when refactoring.
 */
public class SiteWasNotSetUpUserRuntimeException extends ContainerHandledUserRuntimeException {

    private static final long serialVersionUID = 1L;

    public SiteWasNotSetUpUserRuntimeException(String message) {
        super(message);
    }
}
