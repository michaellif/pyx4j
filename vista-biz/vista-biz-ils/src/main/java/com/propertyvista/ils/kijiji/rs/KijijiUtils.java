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

/**
 * RS services utilities.
 */
public class KijijiUtils {

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
