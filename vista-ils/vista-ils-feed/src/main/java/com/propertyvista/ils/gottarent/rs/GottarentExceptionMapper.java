/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 */
package com.propertyvista.ils.gottarent.rs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Currently j-rs API. Should be replaced either by scheduled task or by something else
 * 
 * 
 */
public class GottarentExceptionMapper implements ExceptionMapper<Throwable> {

    private static Logger log = LoggerFactory.getLogger(GottarentExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        log.info("Failed to synchronize data with external gottarent server", exception);
        return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }

}