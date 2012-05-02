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

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.site.client.AppPlaceEntityMapper;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.MiscPolicy;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
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
import com.propertyvista.domain.site.HomePageGadget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.misc.EquifaxResult;

public class CrmEntityMapper {

    public static void init() {
        ImageResource defaultImage = CrmImages.INSTANCE.bookmarkNormal();

        // DTO mappings:
        AppPlaceEntityMapper.register(LeaseDTO.class, CrmSiteMap.Tenants.Lease.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseApplicationDTO.class, CrmSiteMap.Tenants.LeaseApplication.class, defaultImage);

        // DBO mappings:
        AppPlaceEntityMapper.register(AptUnit.class, CrmSiteMap.Properties.Unit.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(AptUnitItem.class, CrmSiteMap.Properties.UnitItem.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(Boiler.class, CrmSiteMap.Properties.Boiler.class, defaultImage);
        AppPlaceEntityMapper.register(Building.class, CrmSiteMap.Properties.Building.class, CrmImages.INSTANCE.propertiesNormal());
        AppPlaceEntityMapper.register(Complex.class, CrmSiteMap.Properties.Complex.class, defaultImage);
        AppPlaceEntityMapper.register(Elevator.class, CrmSiteMap.Properties.Elevator.class, defaultImage);
        AppPlaceEntityMapper.register(Floorplan.class, CrmSiteMap.Properties.Floorplan.class, defaultImage);
        AppPlaceEntityMapper.register(Feature.class, CrmSiteMap.Properties.Feature.class, defaultImage);

        AppPlaceEntityMapper.register(Concession.class, CrmSiteMap.Properties.Concession.class, defaultImage);
        AppPlaceEntityMapper.register(LockerArea.class, CrmSiteMap.Properties.LockerArea.class, defaultImage);
        AppPlaceEntityMapper.register(Parking.class, CrmSiteMap.Properties.Parking.class, defaultImage);
        AppPlaceEntityMapper.register(ParkingSpot.class, CrmSiteMap.Properties.ParkingSpot.class, defaultImage);
        AppPlaceEntityMapper.register(ProductItem.class, CrmSiteMap.Properties.Service.class, defaultImage);
        AppPlaceEntityMapper.register(Roof.class, CrmSiteMap.Properties.Roof.class, defaultImage);
        AppPlaceEntityMapper.register(Service.class, CrmSiteMap.Properties.Service.class, defaultImage);

        AppPlaceEntityMapper.register(Bill.class, CrmSiteMap.Tenants.Bill.class, defaultImage);
        AppPlaceEntityMapper.register(EquifaxResult.class, CrmSiteMap.Tenants.EquifaxResult.class, defaultImage);
        AppPlaceEntityMapper.register(Guarantor.class, CrmSiteMap.Tenants.Guarantor.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(Lease.class, CrmSiteMap.Tenants.Lease.class, defaultImage);
        AppPlaceEntityMapper.register(Locker.class, CrmSiteMap.Properties.Locker.class, defaultImage);
        AppPlaceEntityMapper.register(MaintenanceRequest.class, CrmSiteMap.Tenants.MaintenanceRequest.class, defaultImage);
        AppPlaceEntityMapper.register(PaymentRecord.class, CrmSiteMap.Tenants.Payment.class, defaultImage);
        AppPlaceEntityMapper.register(PersonScreening.class, CrmSiteMap.Tenants.Screening.class, defaultImage);
        AppPlaceEntityMapper.register(Customer.class, CrmSiteMap.Tenants.Tenant.class, defaultImage);
        AppPlaceEntityMapper.register(Tenant.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.tenant());

        AppPlaceEntityMapper.register(Appointment.class, Marketing.Appointment.class, defaultImage);
        AppPlaceEntityMapper.register(Lead.class, Marketing.Lead.class, defaultImage);
        AppPlaceEntityMapper.register(Showing.class, Marketing.Showing.class, defaultImage);

        AppPlaceEntityMapper.register(BillingRun.class, CrmSiteMap.Finance.BillingRun.class, defaultImage);

        AppPlaceEntityMapper.register(Employee.class, CrmSiteMap.Organization.Employee.class, defaultImage);
        AppPlaceEntityMapper.register(Portfolio.class, CrmSiteMap.Organization.Portfolio.class, defaultImage);
        AppPlaceEntityMapper.register(Vendor.class, CrmSiteMap.Organization.Vendor.class, defaultImage);

        AppPlaceEntityMapper.register(CrmRole.class, CrmSiteMap.Settings.UserRole.class, defaultImage);
        AppPlaceEntityMapper.register(FeatureItemType.class, CrmSiteMap.Settings.FeatureItemType.class, defaultImage);
        AppPlaceEntityMapper.register(GlCode.class, CrmSiteMap.Settings.GlCodeCategory.class, defaultImage);
        AppPlaceEntityMapper.register(GlCodeCategory.class, CrmSiteMap.Settings.GlCodeCategory.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseAdjustmentReason.class, CrmSiteMap.Settings.LeaseAdjustmentReason.class, defaultImage);
        AppPlaceEntityMapper.register(ServiceItemType.class, CrmSiteMap.Settings.ServiceItemType.class, defaultImage);
        AppPlaceEntityMapper.register(Tax.class, CrmSiteMap.Settings.Tax.class, defaultImage);

        AppPlaceEntityMapper.register(HomePageGadget.class, CrmSiteMap.Settings.HomePageModules.class, defaultImage);

        AppPlaceEntityMapper.register(DashboardMetadata.class, CrmSiteMap.Dashboard.Edit.class, defaultImage);
        // TODO add report place mapping here

        // policies
        AppPlaceEntityMapper.register(ApplicationDocumentationPolicy.class, CrmSiteMap.Settings.Policies.ApplicationDocumentation.class, defaultImage);
        AppPlaceEntityMapper.register(BackgroundCheckPolicy.class, CrmSiteMap.Settings.Policies.BackgroundCheck.class, defaultImage);
        AppPlaceEntityMapper.register(DepositPolicy.class, CrmSiteMap.Settings.Policies.Deposits.class, defaultImage);
        AppPlaceEntityMapper.register(EmailTemplatesPolicy.class, CrmSiteMap.Settings.Policies.EmailTemplates.class, defaultImage);
        AppPlaceEntityMapper.register(IdAssignmentPolicy.class, CrmSiteMap.Settings.Policies.IdAssignment.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseAdjustmentPolicy.class, CrmSiteMap.Settings.Policies.LeaseAdjustment.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseBillingPolicy.class, CrmSiteMap.Settings.Policies.LeaseBilling.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseTermsPolicy.class, CrmSiteMap.Settings.Policies.LeaseTerms.class, defaultImage);
        AppPlaceEntityMapper.register(MiscPolicy.class, CrmSiteMap.Settings.Policies.Misc.class, defaultImage);
        AppPlaceEntityMapper.register(PetPolicy.class, CrmSiteMap.Settings.Policies.PetPolicy.class, defaultImage);
        AppPlaceEntityMapper.register(ProductTaxPolicy.class, CrmSiteMap.Settings.Policies.ProductTax.class, defaultImage);
    }
}
