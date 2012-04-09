/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 6, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client;

import com.pyx4j.site.client.AppPlaceEntityMapper;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;
import com.propertyvista.domain.policy.dto.EmailTemplatesPolicyDTO;
import com.propertyvista.domain.policy.dto.IdAssignmentPolicyDTO;
import com.propertyvista.domain.policy.dto.LeaseAdjustmentPolicyDTO;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;
import com.propertyvista.domain.policy.dto.MiscPolicyDTO;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;
import com.propertyvista.domain.policy.dto.ProductTaxPolicyDTO;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
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
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.OnlineApplicationDTO;
import com.propertyvista.dto.OnlineMasterApplicationDTO;
import com.propertyvista.dto.RoofDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.misc.EquifaxResult;

public class CrmEntityMapper {

    public static void init() {
        AppPlaceEntityMapper.register(AptUnit.class, CrmSiteMap.Properties.Unit.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(AptUnitDTO.class, CrmSiteMap.Properties.Unit.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(AptUnitItem.class, CrmSiteMap.Properties.UnitItem.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(Boiler.class, CrmSiteMap.Properties.Boiler.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Building.class, CrmSiteMap.Properties.Building.class, CrmImages.INSTANCE.propertiesNormal());
        AppPlaceEntityMapper.register(BuildingDTO.class, CrmSiteMap.Properties.Building.class, CrmImages.INSTANCE.propertiesNormal());
        AppPlaceEntityMapper.register(Complex.class, CrmSiteMap.Properties.Complex.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Elevator.class, CrmSiteMap.Properties.Elevator.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(ElevatorDTO.class, CrmSiteMap.Properties.Elevator.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Floorplan.class, CrmSiteMap.Properties.Floorplan.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(FloorplanDTO.class, CrmSiteMap.Properties.Floorplan.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Feature.class, CrmSiteMap.Properties.Feature.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(Concession.class, CrmSiteMap.Properties.Concession.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LockerArea.class, CrmSiteMap.Properties.LockerArea.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Parking.class, CrmSiteMap.Properties.Parking.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(ParkingSpot.class, CrmSiteMap.Properties.ParkingSpot.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(ProductItem.class, CrmSiteMap.Properties.Service.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Roof.class, CrmSiteMap.Properties.Roof.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(RoofDTO.class, CrmSiteMap.Properties.Roof.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Service.class, CrmSiteMap.Properties.Service.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(BillDTO.class, CrmSiteMap.Tenants.Bill.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(EquifaxResult.class, CrmSiteMap.Tenants.EquifaxResult.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Guarantor.class, CrmSiteMap.Tenants.Guarantor.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(GuarantorDTO.class, CrmSiteMap.Tenants.Guarantor.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Lease.class, CrmSiteMap.Tenants.Lease.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LeaseDTO.class, CrmSiteMap.Tenants.Lease.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Locker.class, CrmSiteMap.Properties.Locker.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(MaintenanceRequest.class, CrmSiteMap.Tenants.MaintenanceRequest.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(MaintenanceRequestDTO.class, CrmSiteMap.Tenants.MaintenanceRequest.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(OnlineApplicationDTO.class, CrmSiteMap.Tenants.Application.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(OnlineMasterApplicationDTO.class, CrmSiteMap.Tenants.OnlineMasterApplication.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(PaymentRecord.class, CrmSiteMap.Tenants.Payment.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(PersonScreening.class, CrmSiteMap.Tenants.Screening.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Tenant.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(TenantInLease.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(TenantInLeaseDTO.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(Appointment.class, Marketing.Appointment.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Lead.class, Marketing.Lead.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Showing.class, Marketing.Showing.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(BillingRun.class, CrmSiteMap.Finance.BillingRun.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(EmployeeDTO.class, CrmSiteMap.Organization.Employee.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Portfolio.class, CrmSiteMap.Organization.Portfolio.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Vendor.class, CrmSiteMap.Organization.Vendor.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(CrmRole.class, CrmSiteMap.Settings.UserRole.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(FeatureItemType.class, CrmSiteMap.Settings.FeatureItemType.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(GlCode.class, CrmSiteMap.Settings.GlCodeCategory.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(GlCodeCategory.class, CrmSiteMap.Settings.GlCodeCategory.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LeaseAdjustmentReason.class, CrmSiteMap.Settings.LeaseAdjustmentReason.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(ServiceItemType.class, CrmSiteMap.Settings.ServiceItemType.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Tax.class, CrmSiteMap.Settings.Tax.class, CrmImages.INSTANCE.arrowGreyLeft());

        AppPlaceEntityMapper.register(DashboardMetadata.class, CrmSiteMap.Dashboard.Edit.class, CrmImages.INSTANCE.arrowGreyLeft());
        // TODO add report place mapping here

        // policies
        AppPlaceEntityMapper.register(ApplicationDocumentationPolicyDTO.class, CrmSiteMap.Settings.Policies.ApplicationDocumentation.class,
                CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(BackgroundCheckPolicyDTO.class, CrmSiteMap.Settings.Policies.BackgroundCheck.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(DepositPolicyDTO.class, CrmSiteMap.Settings.Policies.Deposits.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(EmailTemplatesPolicyDTO.class, CrmSiteMap.Settings.Policies.EmailTemplates.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(IdAssignmentPolicyDTO.class, CrmSiteMap.Settings.Policies.IdAssignment.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(IdAssignmentPolicyDTO.class, CrmSiteMap.Settings.Policies.IdAssignment.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LeaseAdjustmentPolicyDTO.class, CrmSiteMap.Settings.Policies.LeaseAdjustment.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LeaseBillingPolicyDTO.class, CrmSiteMap.Settings.Policies.LeaseBilling.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LeaseBillingPolicyDTO.class, CrmSiteMap.Settings.Policies.LeaseBilling.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(LeaseTermsPolicyDTO.class, CrmSiteMap.Settings.Policies.LeaseTerms.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(MiscPolicyDTO.class, CrmSiteMap.Settings.Policies.Misc.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(PetPolicyDTO.class, CrmSiteMap.Settings.Policies.PetPolicy.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(ProductTaxPolicyDTO.class, CrmSiteMap.Settings.Policies.ProductTax.class, CrmImages.INSTANCE.arrowGreyLeft());
    }
}
