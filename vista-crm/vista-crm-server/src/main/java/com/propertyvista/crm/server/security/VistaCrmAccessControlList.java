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
import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.essentials.rpc.report.ReportServices;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.crm.rpc.services.FeedbackService;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.crm.rpc.services.admin.MerchantAccountCrudService;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.building.LockerCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingSpotCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.crm.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.crm.rpc.services.building.mech.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.rpc.services.customer.TenantPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.crm.rpc.services.customer.screening.PersonScreeningCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataCrudService;
import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;
import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.crm.rpc.services.policies.CrmPolicyRetrieveService;
import com.propertyvista.crm.rpc.services.policies.policy.EmailTemplatesPolicyCrudService;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.crm.rpc.services.security.CrmLoginAttemptsListerService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordChangeUserService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordResetService;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitItemCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.server.common.security.SharedUserEntityInstanceAccess;

public class VistaCrmAccessControlList extends ServletContainerAclBuilder {

    // TODO Change this if you want to make it work temporary. Build will fail!
    private static final boolean allowAllDuringDevelopment = true;

    public VistaCrmAccessControlList() {

        if (allowAllDuringDevelopment) {
            // Debug
            grant(VistaBasicBehavior.CRM, new IServiceExecutePermission("*"));
            grant(VistaBasicBehavior.CRM, new ServiceExecutePermission("*"));
            grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.ALL));
            grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.READ));
        }
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DeferredProcessService.class));
        grant(VistaBasicBehavior.CRM, new ServiceExecutePermission(EntityServices.Query.class));
        grant(VistaBasicBehavior.CRM, new ServiceExecutePermission(ReportServices.class, "*"));

        grant(new IServiceExecutePermission(CrmAuthenticationService.class));
        grant(VistaBasicBehavior.CRMPasswordChangeRequired, new IServiceExecutePermission(CrmPasswordResetService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FeedbackService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmPolicyRetrieveService.class));

// - Dashboard:
        InstanceAccess sharedUserEntityAccess = new SharedUserEntityInstanceAccess();
        grant(VistaBasicBehavior.CRM, new EntityPermission(DashboardMetadata.class, sharedUserEntityAccess, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new EntityPermission(GadgetMetadata.class, sharedUserEntityAccess, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DashboardMetadataService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DashboardMetadataCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ReportMetadataService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ReportMetadataCrudService.class));

        // Add All GadgetSettings
        //grant(VistaBehavior.PROPERTY_MANAGER, new EntityPermission(MyGadgetSettings.class, EntityPermission.ALL));

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
        grant(VistaBasicBehavior.CRM, new EntityPermission(ParkingSpot.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ParkingSpotCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(LockerArea.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LockerAreaCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(Locker.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LockerCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Floorplan.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FloorplanCrudService.class));

        grant(VistaCrmBehavior.PropertyManagement, new IServiceExecutePermission(UpdateUploadService.class));
        grant(VistaCrmBehavior.PropertyVistaSupport, new IServiceExecutePermission(UpdateUploadService.class));

// - Unit-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnit.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnitItem.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitItemCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnitOccupancySegment.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitOccupancyCrudService.class));

// - Tenant-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Lead.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeadCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Appointment.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(AppointmentCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Showing.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ShowingCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Customer.class, EntityPermission.ALL));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Tenant.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(TenantCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(PersonScreening.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PersonScreeningCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Lease.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new EntityPermission(Lease.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseViewerCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        grant(VistaCrmBehavior.Tenants, new IServiceExecutePermission(TenantPasswordChangeService.class));

// - Billing
        grant(VistaCrmBehavior.Billing, new EntityPermission(Bill.class, EntityPermission.READ));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillCrudService.class));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillingExecutionService.class));

// - Service-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Service.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ServiceCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Feature.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FeatureCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Concession.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ConcessionCrudService.class));

// - Organization:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Employee.class, EntityPermission.READ));
        grant(VistaCrmBehavior.Organization, new EntityPermission(Employee.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(EmployeeCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Portfolio.class, EntityPermission.READ));
        grant(VistaCrmBehavior.Organization, new EntityPermission(Portfolio.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PortfolioCrudService.class));

        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(ManagedCrmUserService.class));
        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(CrmLoginAttemptsListerService.class));

// -- Crm Users, Self management
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmPasswordChangeUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmAccountRecoveryOptionsUserService.class));
        grant(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired, new IServiceExecutePermission(CrmAccountRecoveryOptionsUserService.class));

// - Marketing-related:

// - Administration:
        grant(VistaBasicBehavior.CRM, new EntityPermission(EmailTemplatesPolicy.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(EmailTemplatesPolicyCrudService.class));

        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(MerchantAccountCrudService.class));

// - Other:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Company.class, EntityPermission.ALL));

// - Old servies:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Country.class.getPackage().getName() + ".*", EntityPermission.READ));
        grant(VistaBasicBehavior.CRM, new ServiceExecutePermission(EntityServices.class, "*"));

        // All other roles have everything the same
//        for (VistaTenantBehavior b : VistaTenantBehavior.getCrmBehaviors()) {
//            if (b != VistaBasicBehavior.CRM) {
//                grant(b, VistaBasicBehavior.CRM);
//            }
//        }

        // Data Access
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingDatasetAccessRule(), Building.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), AptUnit.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), LockerArea.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), Parking.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), Roof.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseDatasetAccessRule(), Lease.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new Lease2DatasetAccessRule(), Lease.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new TenantDatasetAccessRule(), Customer.class);

        freeze();
    }
}
