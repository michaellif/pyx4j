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

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

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

class VistaCrmAdministrationPoliciesAccessControlList extends UIAclBuilder {

    VistaCrmAdministrationPoliciesAccessControlList() {
        grant(AdminGeneral, CrmAdministrationPolicesOtherAccess.class);
        grant(AdminFinancial, CrmAdministrationPolicesFinancialAccess.class);

        grant(AdminGeneral, new IServiceExecutePermission(ApplicationDocumentationPolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(ARPolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(AutoPayPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(BackgroundCheckPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(DatesPolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(DepositPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(EmailTemplatesPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(EmailTemplateManagerService.class));
        grant(AdminGeneral, new IServiceExecutePermission(IdAssignmentPolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(LeaseAdjustmentPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(LeaseAgreementLegalPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(LeaseApplicationPolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(LeaseBillingPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(LeaseTerminationPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(LegalDocumentationPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(MaintenanceRequestPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(N4PolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(PaymentTypeSelectionPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(PetPolicyCrudService.class));

        grant(AdminFinancial, new IServiceExecutePermission(ProductTaxPolicyCrudService.class));
        grant(AdminFinancial, new IServiceExecutePermission(SelectTaxListService.class));

        grant(AdminGeneral, new IServiceExecutePermission(ProspectPortalPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(RestrictionsPolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(TenantInsurancePolicyCrudService.class));
        grant(AdminGeneral, new IServiceExecutePermission(YardiInterfacePolicyCrudService.class));
    }
}
