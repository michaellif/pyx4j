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

import javax.ws.rs.Path;

import com.propertyvista.oapi.LeaseService;
import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;

/**
 * Implementation of {@link RSLeaseService}
 * 
 */
@Path("/leases")
public class RSLeaseServiceImpl implements RSLeaseService {

    @Override
    public List<LeaseIO> getLeases(String propertyCode) {
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

    @Override
    public LeaseIO getLeaseById(String leaseId) {
        return LeaseService.getLeaseById(leaseId);
    }

    @Override
    public List<TenantIO> getTenants(String leaseId) {
        return LeaseService.getTenants(leaseId);
    }

    @Override
    public void updateLease(LeaseIO leaseIO) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateTenants(String leaseId, List<TenantIO> tenantIOs) {
        // TODO Auto-generated method stub

    }

}
