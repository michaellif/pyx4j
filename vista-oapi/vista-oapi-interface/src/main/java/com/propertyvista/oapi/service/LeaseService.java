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
package com.propertyvista.oapi.service;

import java.util.List;

import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;

public interface LeaseService extends OAPIService {

    List<LeaseIO> getLeases(String propertyCode);

    LeaseIO getLeaseById(String leaseId);

    List<TenantIO> getTenants(String leaseId);

    void updateLease(LeaseIO leaseIO);

    void updateTenants(String leaseId, List<TenantIO> tenantIOs);
}
