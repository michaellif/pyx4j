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
package com.propertyvista.oapi.v1.rs;

import java.util.Iterator;
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

import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.model.LeaseIO;
import com.propertyvista.oapi.v1.model.LeaseListIO;
import com.propertyvista.oapi.v1.model.TenantIO;
import com.propertyvista.oapi.v1.model.TenantListIO;
import com.propertyvista.oapi.v1.processing.LeaseServiceProcessor;
import com.propertyvista.oapi.v1.service.LeaseService;

//http://localhost:8888/vista/interfaces/oapi/v1/rs/leases
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/v1/rs/leases

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
    public LeaseListIO getLeases(@QueryParam("propertyCode") String propertyCode) {
        LeaseServiceProcessor processor = new LeaseServiceProcessor(ServiceType.Read);
        try {
            LeaseListIO allLeases = processor.getLeases();
            if (propertyCode != null) {
                for (Iterator<LeaseIO> it = allLeases.getList().iterator(); it.hasNext();) {
                    if (!propertyCode.equals(it.next().propertyCode)) {
                        it.remove();
                    }
                }
            }
            return allLeases;
        } finally {
            processor.destroy();
        }
    }

    @Override
    @GET
    @Path("/{leaseId}")
    @Produces({ MediaType.APPLICATION_XML })
    public LeaseIO getLeaseById(@PathParam("leaseId") String leaseId) {
        LeaseServiceProcessor processor = new LeaseServiceProcessor(ServiceType.Read);
        try {
            LeaseIO leaseIO = processor.getLeaseById(leaseId);
            if (leaseIO == null) {
                throw new RuntimeException(i18n.tr("Lease with leaseId={0} not found", leaseId));
            }
            return leaseIO;
        } finally {
            processor.destroy();
        }
    }

    @Override
    @GET
    @Path("/{leaseId}/tenants")
    @Produces({ MediaType.APPLICATION_XML })
    public TenantListIO getTenants(@PathParam("leaseId") String leaseId) {
        LeaseServiceProcessor processor = new LeaseServiceProcessor(ServiceType.Read);
        try {
            return processor.getTenants(leaseId);
        } finally {
            processor.destroy();
        }
    }

    @Override
    @POST
    @Path("/updateLease")
    @Consumes({ MediaType.APPLICATION_XML })
    public void updateLease(LeaseIO lease) {
        LeaseServiceProcessor processor = new LeaseServiceProcessor(ServiceType.Write);
        try {
            processor.updateLease(lease);
        } finally {
            processor.destroy();
        }
    }

    @Override
    @POST
    @Path("/{leaseId}/updateTenants")
    @Consumes({ MediaType.APPLICATION_XML })
    public void updateTenants(@PathParam("leaseId") String leaseId, List<TenantIO> tenantIOs) {
        //TODO mkoval implementation TBD
    }

}
