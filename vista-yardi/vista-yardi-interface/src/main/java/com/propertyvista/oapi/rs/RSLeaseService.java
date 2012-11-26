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
package com.propertyvista.oapi.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.propertyvista.oapi.LeaseService;
import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;

//http://localhost:8888/vista/interfaces/oapi/rs/leases
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/rs/leases

/**
 * 
 * interfaces/oapi/rs/leases/ - all buildings
 * 
 * interfaces/oapi/rs/leases?propertyCode=B1 - all leases under property B1
 * 
 * interfaces/oapi/rs/leases/<LeaseId> - specific lease
 * 
 * interfaces/oapi/rs/leases/<LeaseId>/tenants
 * 
 * 
 */
@Path("/leases")
public class RSLeaseService {

    public RSLeaseService() {
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public List<LeaseIO> getLeases(@QueryParam("propertyCode") String propertyCode) {

        List<LeaseIO> allLeases = LeaseService.getLeases();
        if (propertyCode == null) {
            return allLeases;
        }
        List<LeaseIO> filteredLeases = new ArrayList<LeaseIO>();
        for (LeaseIO lease : allLeases) {
            if (lease.propertyCode.equals(propertyCode)) {
                filteredLeases.add(lease);
            }
        }
        return filteredLeases;

    }

    @GET
    @Path("/{leaseId}")
    @Produces({ MediaType.APPLICATION_XML })
    public LeaseIO getLeaseByNumber(@PathParam("leaseId") String leaseId) {

        return LeaseService.getLeaseById(leaseId);

    }

    @GET
    @Path("/{leaseId}/tenants")
    @Produces({ MediaType.APPLICATION_XML })
    public List<TenantIO> getTenants(@PathParam("leaseId") String leaseId) {

        return LeaseService.getTenants(leaseId);

    }
}
