/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 09, 2013
 * @author Anatoly
 */
package com.propertyvista.ils.kijiji.rs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KijijiExceptionMapper implements ExceptionMapper<Throwable> {

    private static Logger log = LoggerFactory.getLogger(KijijiExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        log.info("XML generation failed", exception);
        return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }

}