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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import com.gottarent.rs.Listing;
import com.gottarent.rs.ObjectFactory;

import com.propertyvista.biz.occupancy.ILSGottarentIntegrationAgent;
import com.propertyvista.ils.gottarent.GottarentClient;
import com.propertyvista.ils.gottarent.mapper.GottarentDataMapper;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;

/**
 * Ils gottarent based Rest API
 * 
 * Currently j-rs API. Should be replaced either by scheduled task or by something else
 * 
 * @author smolka
 * 
 */
@Path("/send")
public class GottarentApiRsService {

    /**
     * Update gottarent server with building listing
     * 
     * @param userId
     *            - vendor id in gottarent system
     * @return gottarent data is going to be imported (for validation purposes only, can return gottarent response, but it is not informative)
     * @throws Exception
     */
    @POST
    @Path("/{userId}")
    public Response sendDataToGottarentServer(@PathParam("userId") String userId) throws Exception {

        // fetch relevant data and prepare gottarent xml
        Listing listing = generateData();

        if (hasData(listing)) {
            // update gottarent server
            GottarentClient.updateGottarent(userId, listing);
            return Response.ok().entity(listing).build();
        }
        return Response.noContent().build();
    }

    private boolean hasData(Listing listing) {
        return listing != null && listing.getCompany() != null && listing.getCompany().getPortfolio() != null
                && listing.getCompany().getPortfolio().getBuilding() != null && listing.getCompany().getPortfolio().getBuilding().size() > 0;
    }

    private Listing generateData() throws JAXBException {

        ILSReportDTO ilsReport = new ILSGottarentIntegrationAgent().getUnitListing();

        return new GottarentDataMapper(new ObjectFactory()).createListing(ilsReport);
    }

}
