/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 9, 2012
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.oapi.rs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Defines server-side exception, will be in case of fail or data not found scenario.
 */
@SuppressWarnings("serial")
public class RSServiceException extends WebApplicationException {

    public RSServiceException(Response.Status status) {
        super(Response.status(status).type(MediaType.TEXT_PLAIN).build());
    }

    public RSServiceException(Response.Status status, String message) {
        super(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
