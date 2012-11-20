/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.propertyvista.oapi.LeaseFacade;
import com.propertyvista.oapi.model.LeaseRS;

//http://localhost:8888/vista/interfaces/oapi/rs/propertyService/listAllBuildings
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/rs/propertyService/listAllBuildings

/**
 * 
 * interfaces/oapi/rs/leases/ - all buildings
 * 
 * interfaces/oapi/rs/leases?propertyCode=B1 - all leases under property B1
 * 
 * interfaces/oapi/rs/leases/<LeaseId> - specific lease
 * 
 * 
 */
@Path("/leases")
public class LeaseService {

    public LeaseService() {
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public List<LeaseRS> getLeases(@QueryParam("propertyCode") String propertyCode) {

        List<LeaseRS> allLeases = LeaseFacade.getLeases();
        List<LeaseRS> filteredLeases = new ArrayList<LeaseRS>();
        for (LeaseRS lease : allLeases) {
            if (lease.propertyCode.equals(propertyCode)) {
                filteredLeases.add(lease);
            }
        }
        return filteredLeases;

    }

    @GET
    @Path("/{leaseId}")
    @Produces({ MediaType.APPLICATION_XML })
    public LeaseRS getLeaseByNumber(@PathParam("leaseId") String leaseId) {

        return LeaseFacade.getLeaseById(leaseId);

    }
}
