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
import com.propertyvista.crm.rpc.CrmSiteMap.Administration;
import com.propertyvista.crm.rpc.CrmSiteMap.Dashboard;
import com.propertyvista.crm.rpc.CrmSiteMap.Finance;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.crm.rpc.CrmSiteMap.Organization;
import com.propertyvista.crm.rpc.CrmSiteMap.Properties;
import com.propertyvista.crm.rpc.CrmSiteMap.Report;
import com.propertyvista.crm.rpc.CrmSiteMap.Tenants;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
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
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.AgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.LeaseTerminationPolicy;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.OnlineApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.policy.policies.YardiInterfacePolicy;
import com.propertyvista.domain.property.Landlord;
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
        AppPlaceEntityMapper.register(Complex.class, Properties.Complex.class, defaultImage);
        AppPlaceEntityMapper.register(Building.class, Properties.Building.class, CrmImages.INSTANCE.propertiesNormal());
        AppPlaceEntityMapper.register(AptUnit.class, Properties.Unit.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(AptUnitItem.class, Properties.UnitItem.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(Landlord.class, Properties.Landlord.class, defaultImage);

        AppPlaceEntityMapper.register(Roof.class, Properties.Roof.class, defaultImage);
        AppPlaceEntityMapper.register(Boiler.class, Properties.Boiler.class, defaultImage);
        AppPlaceEntityMapper.register(Elevator.class, Properties.Elevator.class, defaultImage);
        AppPlaceEntityMapper.register(Floorplan.class, Properties.Floorplan.class, defaultImage);
        AppPlaceEntityMapper.register(LockerArea.class, Properties.LockerArea.class, defaultImage);
        AppPlaceEntityMapper.register(Locker.class, Properties.Locker.class, defaultImage);
        AppPlaceEntityMapper.register(Parking.class, Properties.Parking.class, defaultImage);
        AppPlaceEntityMapper.register(ParkingSpot.class, Properties.ParkingSpot.class, defaultImage);

        AppPlaceEntityMapper.register(Feature.class, Properties.Feature.class, defaultImage);
        AppPlaceEntityMapper.register(Service.class, Properties.Service.class, defaultImage);
        AppPlaceEntityMapper.register(Concession.class, Properties.Concession.class, defaultImage);

        AppPlaceEntityMapper.register(ARCode.class, Administration.Financial.ARCode.class, defaultImage);
        AppPlaceEntityMapper.register(ProductItem.class, Properties.Service.class, defaultImage);
        AppPlaceEntityMapper.register(MaintenanceRequest.class, Tenants.MaintenanceRequest.class, defaultImage);

        AppPlaceEntityMapper.register(Customer.class, Tenants.Tenant.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(Tenant.class, Tenants.Tenant.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(Guarantor.class, Tenants.Guarantor.class, CrmImages.INSTANCE.tenant());
        AppPlaceEntityMapper.register(CustomerScreening.class, Tenants.Screening.class, defaultImage);

        AppPlaceEntityMapper.register(Lead.class, Marketing.Lead.class, defaultImage);
        AppPlaceEntityMapper.register(Appointment.class, Marketing.Appointment.class, defaultImage);
        AppPlaceEntityMapper.register(Showing.class, Marketing.Showing.class, defaultImage);

        AppPlaceEntityMapper.register(Lease.class, Tenants.Lease.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseTerm.class, Tenants.LeaseTerm.class, defaultImage);
        AppPlaceEntityMapper.register(AutopayAgreement.class, Finance.AutoPay.class, defaultImage);

        AppPlaceEntityMapper.register(BillingCycle.class, Finance.BillingCycle.class, defaultImage);
        AppPlaceEntityMapper.register(Bill.class, Finance.Bill.class, defaultImage);
        AppPlaceEntityMapper.register(PaymentRecord.class, Finance.Payment.class, defaultImage);
        AppPlaceEntityMapper.register(AggregatedTransfer.class, Finance.AggregatedTransfer.class, defaultImage);
        AppPlaceEntityMapper.register(DepositLifecycle.class, Finance.LeaseDeposit.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseAdjustment.class, Finance.LeaseAdjustment.class, defaultImage);

        AppPlaceEntityMapper.register(MerchantAccount.class, Administration.Financial.MerchantAccount.class, defaultImage);
        AppPlaceEntityMapper.register(GlCode.class, Administration.Financial.GlCodeCategory.class, defaultImage);
        AppPlaceEntityMapper.register(GlCodeCategory.class, Administration.Financial.GlCodeCategory.class, defaultImage);
        AppPlaceEntityMapper.register(Tax.class, Administration.Financial.Tax.class, defaultImage);

        AppPlaceEntityMapper.register(CrmRole.class, Administration.Security.UserRole.class, defaultImage);
        AppPlaceEntityMapper.register(Employee.class, Organization.Employee.class, defaultImage);
        AppPlaceEntityMapper.register(Portfolio.class, Organization.Portfolio.class, defaultImage);
        AppPlaceEntityMapper.register(Vendor.class, Organization.Vendor.class, defaultImage);

        AppPlaceEntityMapper.register(HomePageGadget.class, Administration.ContentManagement.Website.HomePageGadgets.class, defaultImage);

        AppPlaceEntityMapper.register(DashboardMetadata.class, Dashboard.Manage.class, defaultImage);
        // TODO add report place mapping here

        // policies
        AppPlaceEntityMapper.register(ApplicationDocumentationPolicy.class, Administration.Policies.ApplicationDocumentation.class, defaultImage);
        AppPlaceEntityMapper.register(BackgroundCheckPolicy.class, Administration.Policies.BackgroundCheck.class, defaultImage);
        AppPlaceEntityMapper.register(DepositPolicy.class, Administration.Policies.Deposits.class, defaultImage);
        AppPlaceEntityMapper.register(EmailTemplatesPolicy.class, Administration.Policies.EmailTemplates.class, defaultImage);
        AppPlaceEntityMapper.register(IdAssignmentPolicy.class, Administration.Policies.IdAssignment.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseAdjustmentPolicy.class, Administration.Policies.LeaseAdjustment.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseTerminationPolicy.class, Administration.Policies.LeaseTermination.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseBillingPolicy.class, Administration.Policies.Billing.class, defaultImage);
        AppPlaceEntityMapper.register(OnlineApplicationLegalPolicy.class, Administration.Policies.OnlineApplicationTerms.class, defaultImage);
        AppPlaceEntityMapper.register(AgreementLegalPolicy.class, Administration.Policies.AgreementLegalTerms.class, defaultImage);
        AppPlaceEntityMapper.register(LegalTermsPolicy.class, Administration.Policies.LegalDocumentation.class, defaultImage);
        AppPlaceEntityMapper.register(MaintenanceRequestPolicy.class, Administration.Policies.MaintenanceRequest.class, defaultImage);
        AppPlaceEntityMapper.register(N4Policy.class, Administration.Policies.N4.class, defaultImage);
        AppPlaceEntityMapper.register(DatesPolicy.class, Administration.Policies.Dates.class, defaultImage);
        AppPlaceEntityMapper.register(RestrictionsPolicy.class, Administration.Policies.Restrictions.class, defaultImage);
        AppPlaceEntityMapper.register(PaymentTypeSelectionPolicy.class, Administration.Policies.PaymentTypeSelection.class, defaultImage);
        AppPlaceEntityMapper.register(PetPolicy.class, Administration.Policies.Pet.class, defaultImage);
        AppPlaceEntityMapper.register(ProductTaxPolicy.class, Administration.Policies.ProductTax.class, defaultImage);
        AppPlaceEntityMapper.register(ProspectPortalPolicy.class, Administration.Policies.ProspectPortal.class, defaultImage);
        AppPlaceEntityMapper.register(ARPolicy.class, Administration.Policies.AR.class, defaultImage);
        AppPlaceEntityMapper.register(TenantInsurancePolicy.class, Administration.Policies.TenantInsurance.class, defaultImage);
        AppPlaceEntityMapper.register(AutoPayPolicy.class, Administration.Policies.AutoPay.class, defaultImage);
        AppPlaceEntityMapper.register(YardiInterfacePolicy.class, Administration.Policies.YardiInterface.class, defaultImage);

        AppPlaceEntityMapper.register(InvoiceDebit.class, Tenants.Lease.InvoiceDebit.class, defaultImage);
        AppPlaceEntityMapper.register(InvoiceCredit.class, Tenants.Lease.InvoiceCredit.class, defaultImage);

        // DTO mappings:
        AppPlaceEntityMapper.register(LeaseDTO.class, Tenants.Lease.class, defaultImage);
        AppPlaceEntityMapper.register(LeaseApplicationDTO.class, Tenants.LeaseApplication.class, defaultImage);

        AppPlaceEntityMapper.register(CustomerCreditCheckDTO.class, Report.CustomerCreditCheck.class, defaultImage);
        AppPlaceEntityMapper.register(CustomerCreditCheckLongReportDTO.class, Tenants.CustomerCreditCheckLongReport.class, defaultImage);

        AppPlaceEntityMapper.register(MoneyInBatchDTO.class, Finance.MoneyIn.Batch.class, defaultImage);
    }
}
