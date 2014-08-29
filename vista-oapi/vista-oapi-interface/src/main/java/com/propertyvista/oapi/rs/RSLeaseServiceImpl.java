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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;
import com.propertyvista.oapi.processing.LeaseServiceProcessor;
import com.propertyvista.oapi.service.LeaseService;

//http://localhost:8888/vista/interfaces/oapi/rs/leases
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/rs/leases

/**
 * 
 * interfaces/oapi/rs/leases/ - all leases
 * 
 * interfaces/oapi/rs/leases?propertyCode=B1 - all leases under property B1
 * 
 * interfaces/oapi/rs/leases/<LeaseId> - specific lease
 * 
 * interfaces/oapi/rs/leases/<LeaseId>/tenants
 * 
 * interfaces/oapi/rs/leases/updateLease - updates/creates lease
 * 
 * interfaces/oapi/rs/leases/<LeaseId>/updateTenants - updates/creates tenants for corresponding lease
 * 
 */

@Path("/leases")
public class RSLeaseServiceImpl implements LeaseService {

    private static I18n i18n = I18n.get(RSLeaseServiceImpl.class);

    @Override
    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public List<LeaseIO> getLeases(@QueryParam("propertyCode") String propertyCode) {
        List<LeaseIO> allLeases = LeaseServiceProcessor.getLeases();
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

    @Override
    @GET
    @Path("/{leaseId}")
    @Produces({ MediaType.APPLICATION_XML })
    public LeaseIO getLeaseById(@PathParam("leaseId") String leaseId) {
        LeaseIO leaseIO = LeaseServiceProcessor.getLeaseById(leaseId);
        if (leaseIO == null) {
            throw new RuntimeException(i18n.tr("Lease with leaseId={0} not found", leaseId));
        }
        return LeaseServiceProcessor.getLeaseById(leaseId);
    }

    @Override
    @GET
    @Path("/{leaseId}/tenants")
    @Produces({ MediaType.APPLICATION_XML })
    public List<TenantIO> getTenants(@PathParam("leaseId") String leaseId) {
        return LeaseServiceProcessor.getTenants(leaseId);
    }

    @Override
    @POST
    @Path("/updateLease")
    @Consumes({ MediaType.APPLICATION_XML })
    public void updateLease(LeaseIO leaseIO) {
        LeaseServiceProcessor.updateLease(leaseIO);
        RSUtils.createSuccessResponse(i18n.tr("Lease updated successfully"));
    }

    @Override
    @POST
    @Path("/{leaseId}/updateTenants")
    @Consumes({ MediaType.APPLICATION_XML })
    public void updateTenants(@PathParam("leaseId") String leaseId, List<TenantIO> tenantIOs) {
        //TODO mkoval implementation TBD
        RSUtils.createSuccessResponse(i18n.tr("Operation is not implemented"));
    }

}
