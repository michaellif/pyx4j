/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2012
 * @author Mykola
 */
package com.propertyvista.oapi.v1.rs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RS services utilities.
 */
public class RSUtils {

    /**
     * Creates successful response with specified message.
     * 
     * @param message
     *            the message for service consumer
     * @return created response
     */
    public static Response createSuccessResponse(String message) {
        return Response.ok().entity(message).type(MediaType.TEXT_PLAIN).build();
    }
}
