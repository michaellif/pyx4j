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
package com.propertyvista.oapi.v1.service;

import java.util.List;

import com.propertyvista.oapi.v1.model.LeaseIO;
import com.propertyvista.oapi.v1.model.LeaseListIO;
import com.propertyvista.oapi.v1.model.TenantIO;
import com.propertyvista.oapi.v1.model.TenantListIO;

public interface LeaseService extends OAPIService {

    LeaseListIO getLeases(String propertyCode);

    LeaseIO getLeaseById(String leaseId);

    TenantListIO getTenants(String leaseId);

    void updateLease(LeaseIO leaseIO);

    void updateTenants(String leaseId, List<TenantIO> tenantIOs);
}
