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
package com.propertyvista.field.server.security;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.field.rpc.services.FieldAuthenticationService;
import com.propertyvista.field.rpc.services.breadcrumbs.BreadcrumbsService;
import com.propertyvista.field.rpc.services.building.BuildingCrudService;
import com.propertyvista.field.rpc.services.building.FloorplanCrudService;
import com.propertyvista.field.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.field.rpc.services.building.ParkingCrudService;
import com.propertyvista.field.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.field.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.field.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.field.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.field.rpc.services.building.mech.ElevatorCrudService;
import com.propertyvista.field.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.field.rpc.services.unit.UnitCrudService;

public class VistaFieldAccessControlList extends ServletContainerAclBuilder {

    private static final boolean allowAllEntityDuringDevelopment = true;

    public VistaFieldAccessControlList() {

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission("*"));
        grant(VistaBasicBehavior.CRM, new ServiceExecutePermission("*"));
        grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.READ));

        grant(new IServiceExecutePermission(FieldAuthenticationService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BreadcrumbsService.class));

// - Building-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Building.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BuildingCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Elevator.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ElevatorCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Boiler.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BoilerCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Roof.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(RoofCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Parking.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ParkingCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(LockerArea.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LockerAreaCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Floorplan.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FloorplanCrudService.class));

// - Unit-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnit.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitCrudService.class));

// - Service-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Service.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ServiceCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Feature.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FeatureCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Concession.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ConcessionCrudService.class));

    }
}
