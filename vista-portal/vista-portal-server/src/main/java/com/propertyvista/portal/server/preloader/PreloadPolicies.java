/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.server.preloader;

import com.propertyvista.biz.preloader.policy.AbstractPoliciesPreloader;
import com.propertyvista.biz.preloader.policy.OrganizationPoliciesNodePreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.ARPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.ApplicationDocumentationPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.AutoPayPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.BackgroundCheckPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.DatesPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.DepositPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.EmailTemplatesPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.IdAssignmentPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LeaseAdjustmentPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LeaseAgreementPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LeaseApplicationPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LeaseBillingPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LegalQuestionsPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LegalTermsPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.MaintenanceRequestPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.N4PolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.PaymentMethodSelectionPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.PreloadRestrictionsPolicies;
import com.propertyvista.biz.preloader.policy.subpreloaders.ProductTaxPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.ProspectPortalPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.ResidentPortalPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.TenantInsurancePolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.YardiInterfacePolicyPreloader;
import com.propertyvista.preloader.policy.MockupDepositPolicyPreloader;
import com.propertyvista.preloader.policy.MockupLeaseAdjustmentPolicyPreloader;
import com.propertyvista.preloader.policy.MockupLeaseBillingPolicyPreloader;
import com.propertyvista.preloader.policy.MockupProductTaxPolicyPreloader;
import com.propertyvista.preloader.policy.MockupProspectPortalPolicyPreloader;

public class PreloadPolicies extends AbstractPoliciesPreloader {

    public PreloadPolicies(boolean isProduction) {
        add(new OrganizationPoliciesNodePreloader());
        add(new ARPolicyPreloader());
        add(new MaintenanceRequestPolicyPreloader());
        add(new ApplicationDocumentationPolicyPreloader());
        add(new EmailTemplatesPolicyPreloader());
        add(new IdAssignmentPolicyPreloader());
        add(new PreloadRestrictionsPolicies());
        add(new DatesPolicyPreloader());
        add(new TenantInsurancePolicyPreloader());
        add(new PaymentMethodSelectionPolicyPreloader());
        add(new AutoPayPolicyPreloader());
        add(new YardiInterfacePolicyPreloader());
        add(new N4PolicyPreloader());
        add(new LeaseApplicationPolicyPreloader());
        add(new LeaseAgreementPolicyPreloader());
        add(new LegalTermsPolicyPreloader());
        add(new LegalQuestionsPolicyPreloader());
        add(new BackgroundCheckPolicyPreloader());
        add(new ResidentPortalPolicyPreloader());

        if (isProduction) {
            add(new ProductTaxPolicyPreloader());
            add(new DepositPolicyPreloader());
            add(new LeaseAdjustmentPolicyPreloader());
            add(new LeaseBillingPolicyPreloader());
            add(new ProspectPortalPolicyPreloader());
        } else {
            add(new MockupProductTaxPolicyPreloader());
            add(new MockupDepositPolicyPreloader());
            add(new MockupLeaseAdjustmentPolicyPreloader());
            add(new MockupLeaseBillingPolicyPreloader());
            add(new MockupProspectPortalPolicyPreloader());
        }
    }

}
