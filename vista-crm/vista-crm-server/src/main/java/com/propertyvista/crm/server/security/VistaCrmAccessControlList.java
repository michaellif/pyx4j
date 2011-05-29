/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.crm.rpc.CrmSiteMap.Editors.LockerArea;
import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.crm.rpc.services.BoilerCrudService;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.crm.rpc.services.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.InquiryCrudService;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.rpc.services.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.LockerCrudService;
import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.crm.rpc.services.ParkingSpotCrudService;
import com.propertyvista.crm.rpc.services.RoofCrudService;
import com.propertyvista.crm.rpc.services.TenantCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.crm.rpc.services.UnitItemCrudService;
import com.propertyvista.crm.rpc.services.UnitOccupancyCrudService;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.tenant.Inquiry;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.ptapp.Application;

public class VistaCrmAccessControlList extends ServletContainerAclBuilder {

    // Change this if you want to make it work temporary. Build will fail!
    private static final boolean allowAllDuringDevelopment = false;

    public VistaCrmAccessControlList() {

        if (allowAllDuringDevelopment) {
            // Debug
            grant(new IServiceExecutePermission("*"));
            grant(new ServiceExecutePermission(EntityServices.class, "*"));
            grant(new ServiceExecutePermission("*"));
            grant(new EntityPermission("*", EntityPermission.ALL));
            grant(new EntityPermission("*", EntityPermission.READ));
        }

// - Building-related:
        grant(new EntityPermission(Building.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(BuildingCrudService.class));

        grant(new EntityPermission(Elevator.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(ElevatorCrudService.class));

        grant(new EntityPermission(Boiler.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(BoilerCrudService.class));

        grant(new EntityPermission(Roof.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(RoofCrudService.class));

        grant(new EntityPermission(Parking.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(ParkingCrudService.class));
        grant(new EntityPermission(ParkingSpot.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(ParkingSpotCrudService.class));

        grant(new EntityPermission(LockerArea.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(LockerAreaCrudService.class));
        grant(new EntityPermission(Locker.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(LockerCrudService.class));

// - Unit-related:
        grant(new EntityPermission(AptUnit.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(UnitCrudService.class));
        grant(new EntityPermission(AptUnitItem.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(UnitItemCrudService.class));
        grant(new EntityPermission(AptUnitOccupancy.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(UnitOccupancyCrudService.class));

        grant(new EntityPermission(Concession.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(ConcessionCrudService.class));

// - Tenant-related:
        grant(new EntityPermission(Tenant.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(TenantCrudService.class));

        grant(new EntityPermission(Lease.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(LeaseCrudService.class));

        grant(new EntityPermission(Application.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(ApplicationCrudService.class));

        grant(new EntityPermission(Inquiry.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(InquiryCrudService.class));

// - Other:

// - Old servies:
        grant(new EntityPermission(Country.class.getPackage().getName() + ".*", EntityPermission.READ));
        grant(new ServiceExecutePermission(EntityServices.class, "*"));
    }
}
