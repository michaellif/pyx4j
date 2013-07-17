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
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Financial;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Security;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Website;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.AutoPayChangePolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.LeaseTerminationPolicy;
import com.propertyvista.domain.policy.policies.LegalDocumentation;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.policy.policies.YardiInterfacePolicy;
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
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;

public class CrmEntityMapper {

    public static void init() {
        ImageResource defaultImage = CrmImages.INSTANCE.bookmarkNormal();

        // DBO mappings:
        AppPlaceEntityMapper.register(Complex.class, CrmSiteMap.Properties.Complex.class, defaultImage);
        AppPlaceEntityMapper.register(Building.class, CrmSiteMap.Properties.Building.class, CrmImages.INSTANCE.propertiesNormal());
        AppPlaceEntityMapper.register(AptUnit.class, CrmSiteMap.Properties.Unit.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(AptUnitItem.class, CrmSiteMap.Properties.UnitItem.class, CrmImages.INSTANCE.unitsNormal());

        AppPlaceEntityMapper.register(Roof.class, CrmSiteMap.Properties.Roof.class, defaultImage);
        AppPlaceEntityMapper.register(Boiler.class, CrmSiteMap.Properties.Boiler.class, defaultImage);
        AppPlaceEntityMapper.register(Elevator.class, CrmSiteMap.Properties.Elevator.class, defaultImage);
        AppPlaceEntityMapper.register(Floorplan.class, CrmSiteMap.Properties.Floorplan.class, defaultImage);
        AppPlaceEntityMapper.register(LockerArea.class, CrmSiteMap.Properties.LockerArea.class, defaultImage);
        AppPlaceEntityMapper.register(Locker.class, CrmSiteMap.Properties.Locker.class, defaultImage);
        AppPlaceEntityMapper.register(Parking.class, CrmSiteMap.Properties.Parking.class, defaultImage);
        AppPlaceEntityMapper.register(ParkingSpot.class, CrmSiteMap.Properties.ParkingSpot.class, defaultImage);

        AppPlaceEntityMapper.register(Feature.class, CrmSiteMap.Properties.Feature.class, defaultImage);
        AppPlaceEntityMapper.register(Service.class, CrmSiteMap.Properties.Service.class, defaultImage);
        AppPlaceEntityMapper.register(Concession.class, CrmSiteMap.Properties.Concession.class, defaultImage);

        AppPlaceEntityMapper.register(ARCode.class, Financial.ARCode.class, defaultImage);
        AppPlaceEntityMapper.register(ProductItem.class, CrmSiteMap.Properties.Service.class, defaultImage);
        AppPlaceEntityMapper.register(MaintenanceRequest.class, CrmSiteMap.Tenants.MaintenanceRequest.class, defaultImage);

        AppPlaceEntityMapper.register(Customer.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(Tenant.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(Guarantor.class, CrmSiteMap.Tenants.Guarantor.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(CustomerScreening.class, CrmSiteMap.Tenants.Screening.class, defaultImage);

        AppPlaceEntityMapper.register(Lead.class, Marketing.Lead.class, defaultImage);
        AppPlaceEntityMapper.register(Appointment.class, Marketing.Appointment.class, defaultImage);
        AppPlaceEntityMapper.register(Showing.class, Marketing.Showing.class, defaultImage);

        AppPlaceEntityMapper.register(Lease.class, CrmSiteMap.Tenants.Lease.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseTerm.class, CrmSiteMap.Tenants.LeaseTerm.class, defaultImage);

        AppPlaceEntityMapper.register(BillingCycle.class, CrmSiteMap.Finance.BillingCycle.class, defaultImage);
        AppPlaceEntityMapper.register(Bill.class, CrmSiteMap.Finance.Bill.class, defaultImage);
        AppPlaceEntityMapper.register(PaymentRecord.class, CrmSiteMap.Finance.Payment.class, defaultImage);
        AppPlaceEntityMapper.register(AggregatedTransfer.class, CrmSiteMap.Finance.AggregatedTransfer.class, defaultImage);
        AppPlaceEntityMapper.register(DepositLifecycle.class, CrmSiteMap.Finance.LeaseDeposit.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseAdjustment.class, CrmSiteMap.Finance.LeaseAdjustment.class, defaultImage);

        AppPlaceEntityMapper.register(MerchantAccount.class, Financial.MerchantAccount.class, defaultImage);
        AppPlaceEntityMapper.register(GlCode.class, Financial.GlCodeCategory.class, defaultImage);
        AppPlaceEntityMapper.register(GlCodeCategory.class, Financial.GlCodeCategory.class, defaultImage);
        AppPlaceEntityMapper.register(Tax.class, Financial.Tax.class, defaultImage);

        AppPlaceEntityMapper.register(CrmRole.class, Security.UserRole.class, defaultImage);
        AppPlaceEntityMapper.register(Employee.class, CrmSiteMap.Organization.Employee.class, defaultImage);
        AppPlaceEntityMapper.register(Portfolio.class, CrmSiteMap.Organization.Portfolio.class, defaultImage);
        AppPlaceEntityMapper.register(Vendor.class, CrmSiteMap.Organization.Vendor.class, defaultImage);

        AppPlaceEntityMapper.register(HomePageGadget.class, Website.Content.HomePageGadgets.class, defaultImage);

        AppPlaceEntityMapper.register(DashboardMetadata.class, CrmSiteMap.Dashboard.Manage.class, defaultImage);
        // TODO add report place mapping here

        // policies
        AppPlaceEntityMapper.register(ApplicationDocumentationPolicy.class, CrmSiteMap.Administration.Policies.ApplicationDocumentation.class, defaultImage);
        AppPlaceEntityMapper.register(BackgroundCheckPolicy.class, CrmSiteMap.Administration.Policies.BackgroundCheck.class, defaultImage);
        AppPlaceEntityMapper.register(DepositPolicy.class, CrmSiteMap.Administration.Policies.Deposits.class, defaultImage);
        AppPlaceEntityMapper.register(EmailTemplatesPolicy.class, CrmSiteMap.Administration.Policies.EmailTemplates.class, defaultImage);
        AppPlaceEntityMapper.register(IdAssignmentPolicy.class, CrmSiteMap.Administration.Policies.IdAssignment.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseAdjustmentPolicy.class, CrmSiteMap.Administration.Policies.LeaseAdjustment.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseTerminationPolicy.class, CrmSiteMap.Administration.Policies.LeaseTermination.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseBillingPolicy.class, CrmSiteMap.Administration.Policies.Billing.class, defaultImage);
        AppPlaceEntityMapper.register(LegalDocumentation.class, CrmSiteMap.Administration.Policies.LegalDocumentation.class, defaultImage);
        AppPlaceEntityMapper.register(DatesPolicy.class, CrmSiteMap.Administration.Policies.Dates.class, defaultImage);
        AppPlaceEntityMapper.register(RestrictionsPolicy.class, CrmSiteMap.Administration.Policies.Restrictions.class, defaultImage);
        AppPlaceEntityMapper.register(PaymentTypeSelectionPolicy.class, CrmSiteMap.Administration.Policies.PaymentTypeSelection.class, defaultImage);
        AppPlaceEntityMapper.register(PetPolicy.class, CrmSiteMap.Administration.Policies.Pet.class, defaultImage);
        AppPlaceEntityMapper.register(ProductTaxPolicy.class, CrmSiteMap.Administration.Policies.ProductTax.class, defaultImage);
        AppPlaceEntityMapper.register(ARPolicy.class, CrmSiteMap.Administration.Policies.AR.class, defaultImage);
        AppPlaceEntityMapper.register(TenantInsurancePolicy.class, CrmSiteMap.Administration.Policies.TenantInsurance.class, defaultImage);
        AppPlaceEntityMapper.register(AutoPayChangePolicy.class, CrmSiteMap.Administration.Policies.AutoPayChange.class, defaultImage);
        AppPlaceEntityMapper.register(YardiInterfacePolicy.class, CrmSiteMap.Administration.Policies.YardiInterface.class, defaultImage);

        AppPlaceEntityMapper.register(InvoiceDebit.class, CrmSiteMap.Tenants.Lease.InvoiceDebit.class, defaultImage);
        AppPlaceEntityMapper.register(InvoiceCredit.class, CrmSiteMap.Tenants.Lease.InvoiceCredit.class, defaultImage);

        // DTO mappings:
        AppPlaceEntityMapper.register(LeaseDTO.class, CrmSiteMap.Tenants.Lease.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseApplicationDTO.class, CrmSiteMap.Tenants.LeaseApplication.class, defaultImage);

        AppPlaceEntityMapper.register(CustomerCreditCheckDTO.class, CrmSiteMap.Report.CustomerCreditCheck.class, defaultImage);
        AppPlaceEntityMapper.register(CustomerCreditCheckLongReportDTO.class, CrmSiteMap.Tenants.CustomerCreditCheckLongReport.class, defaultImage);

    }
}
