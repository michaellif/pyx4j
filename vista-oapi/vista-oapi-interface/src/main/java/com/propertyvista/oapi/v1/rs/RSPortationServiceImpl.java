/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 16, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.processing.PortationServiceProcessor;
import com.propertyvista.oapi.v1.service.PortationService;

@Path("/portation")
public class RSPortationServiceImpl implements PortationService {

    @Override
    @GET
    @Path("exportBuildings")
    @Produces(MediaType.APPLICATION_XML)
    public BuildingListIO exportBuildings() {
        PortationServiceProcessor processor = new PortationServiceProcessor(ServiceType.Read);
        try {
            return processor.exportBuildings();
        } finally {
            processor.destroy();
        }
    }

    @POST
    @Path("/importBuildings")
    @Consumes({ MediaType.APPLICATION_XML })
    @Override
    public void importBuildings(BuildingListIO buildings) {
        // TODO Auto-generated method stub

    }

}
