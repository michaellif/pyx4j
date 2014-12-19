/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2012
 * @author Mykola
 */
package com.propertyvista.oapi.v1.rs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;

/**
 * Maps all exceptions to RS response.
 */
@Provider
public class RSExceptionMapper implements ExceptionMapper<Throwable> {

    private final static Logger log = LoggerFactory.getLogger(RSExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Error", exception);
        String message = "ERROR: ";
        if (exception instanceof UserRuntimeException) {
            message += exception.getMessage();
        } else {
            message += "Internal Application Error";
        }
        return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build();
    }

}
