/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.AdminFinancial;
import static com.propertyvista.domain.security.VistaCrmBehavior.AdminGeneral;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationPolicesFinancialAccess;
import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationPolicesOtherAccess;
import com.propertyvista.crm.rpc.services.policies.emailtemplates.EmailTemplateManagerService;
import com.propertyvista.crm.rpc.services.policies.policy.ARPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.ApplicationDocumentationPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.AutoPayPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.BackgroundCheckPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.DatesPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.DepositPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.EmailTemplatesPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.IdAssignmentPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseAdjustmentPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseAgreementLegalPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseApplicationPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseBillingPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseTerminationPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LegalDocumentationPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.MaintenanceRequestPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.N4PolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.PaymentTypeSelectionPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.PetPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.ProductTaxPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.ProspectPortalPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.RestrictionsPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.TenantInsurancePolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.YardiInterfacePolicyCrudService;
import com.propertyvista.crm.rpc.services.selections.SelectTaxListService;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseAgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.LeaseApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.policy.policies.YardiInterfacePolicy;
import com.propertyvista.domain.site.SiteImageResource;

class VistaCrmAdministrationPoliciesAccessControlList extends UIAclBuilder {

    VistaCrmAdministrationPoliciesAccessControlList() {
        grant(AdminGeneral, CrmAdministrationPolicesOtherAccess.class);
        grant(AdminFinancial, CrmAdministrationPolicesFinancialAccess.class);

        grant(AdminGeneral, new IServiceExecutePermission(ApplicationDocumentationPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(ApplicationDocumentationPolicy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(ARPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(ARPolicy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(AutoPayPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(AutoPayPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(BackgroundCheckPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(BackgroundCheckPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(DatesPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(DatesPolicy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(DepositPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(DepositPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(EmailTemplatesPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(EmailTemplateManagerService.class));
        grant(AdminGeneral, new IServiceExecutePermission(SiteImageResourceCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(SiteImageResourceUploadService.class));
        grant(AdminGeneral, new EntityPermission(SiteImageResource.class, READ));

        grant(AdminGeneral, new IServiceExecutePermission(IdAssignmentPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(IdAssignmentPolicy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(LeaseAdjustmentPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(LeaseAdjustmentPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(LeaseAgreementLegalPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(LeaseAgreementLegalPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(LeaseApplicationPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(LeaseApplicationLegalPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(LeaseTerminationPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(LegalTermsPolicy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(LeaseBillingPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(LeaseBillingPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(LegalDocumentationPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(LegalTermsPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(MaintenanceRequestPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(MaintenanceRequestPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(N4PolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(N4Policy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(PaymentTypeSelectionPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(PaymentTypeSelectionPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(PetPolicyCrudService.class));

        grant(AdminFinancial, new IServiceExecutePermission(ProductTaxPolicyCrudService.class));
        grant(AdminFinancial, new EntityPermission(ProductTaxPolicy.class, ALL));

        grant(AdminFinancial, new IServiceExecutePermission(SelectTaxListService.class));

        grant(AdminGeneral, new IServiceExecutePermission(ProspectPortalPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(ProspectPortalPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(RestrictionsPolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(RestrictionsPolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(TenantInsurancePolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(TenantInsurancePolicy.class, ALL));

        grant(AdminGeneral, new IServiceExecutePermission(YardiInterfacePolicyCrudService.class));
        grant(AdminGeneral, new EntityPermission(YardiInterfacePolicy.class, ALL));
    }
}
