/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.financial.offeringnew.ServiceCatalog;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface LeaseDTO extends Lease {

//  Note: While show ApplicationDTO - make 2 tabs (or 1 tab with 2 listers in it with Building and Unit selection) for:
    @Detached
    Building selectedBuilding();

//    Tenant primaryTenant();

//  Note : While show ApplicationDTO - make tab with :
//  IList<TenantInLease> tenants();

    ServiceCatalog serviceCatalog();
}
