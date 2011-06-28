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
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.common.domain.VistaBehavior;
import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.crm.rpc.services.AuthenticationService;
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
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.Company;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
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
import com.propertyvista.server.common.security.SharedUserEntityInstanceAccess;

public class VistaCrmAccessControlList extends ServletContainerAclBuilder {

    // TODO Change this if you want to make it work temporary. Build will fail!
    private static final boolean allowAllDuringDevelopment = true;

    public VistaCrmAccessControlList() {

        if (allowAllDuringDevelopment) {
            // Debug
            grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission("*"));
            grant(VistaBehavior.PROPERTY_MANAGER, new ServiceExecutePermission("*"));
            grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission("*", EntityPermission.ALL));
            grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission("*", EntityPermission.READ));
        }

        grant(new IServiceExecutePermission(AuthenticationService.class));

// - Dashboard:
        InstanceAccess sharedUserEntityAccess = new SharedUserEntityInstanceAccess();
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(DashboardMetadata.class, sharedUserEntityAccess, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(GadgetMetadata.class, sharedUserEntityAccess, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(DashboardMetadataService.class));

        // Add All GadgetSettings
        //grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(MyGadgetSettings.class, EntityPermission.ALL));

// - Building-related:
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Building.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(BuildingCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Elevator.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(ElevatorCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Boiler.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(BoilerCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Roof.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(RoofCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Parking.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(ParkingCrudService.class));
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(ParkingSpot.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(ParkingSpotCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(LockerArea.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(LockerAreaCrudService.class));
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Locker.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(LockerCrudService.class));

// - Unit-related:
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(AptUnit.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(UnitCrudService.class));
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(AptUnitItem.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(UnitItemCrudService.class));
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(AptUnitOccupancy.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(UnitOccupancyCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Concession.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(ConcessionCrudService.class));

// - Tenant-related:
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Tenant.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(TenantCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Lease.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(LeaseCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Application.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(ApplicationCrudService.class));

        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Inquiry.class, EntityPermission.ALL));
        grant(VistaBehavior.PROPERTY_MANAGER, new IServiceExecutePermission(InquiryCrudService.class));

// - Other:
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Company.class, EntityPermission.ALL));

// - Old servies:
        grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(Country.class.getPackage().getName() + ".*", EntityPermission.READ));
        grant(VistaBehavior.PROPERTY_MANAGER, new ServiceExecutePermission(EntityServices.class, "*"));
    }
}
