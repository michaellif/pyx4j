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
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ARPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ApplicationDocumentationPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.AutoPayPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.BackgroundCheckPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.DatesPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.DepositPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.EmailTemplatesPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.IdAssignmentPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseBillingPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LegalTermsPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupDepositPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupLeaseAdjustmentPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupLeaseBillingPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupLeaseSigningPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupOnlineApplicationPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupProductTaxPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupProspectPortalPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.N4PolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.PaymentMethodSelectionPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.PreloadRestrictionsPolicies;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ProductTaxPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.TenantInsurancePolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.YardiInterfacePolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPoliciesPreloader;
import com.propertyvista.portal.server.preloader.policy.util.OrganizationPoliciesNodePreloader;

public class PreloadPolicies extends AbstractPoliciesPreloader {

    public PreloadPolicies(boolean isProduction) {
        add(new OrganizationPoliciesNodePreloader());
        add(new ARPolicyPreloader());
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

        if (!VistaTODO.Equifax_Off_VISTA_478) {
            add(new BackgroundCheckPolicyPreloader());
        }

        if (isProduction) {
            add(new ProductTaxPolicyPreloader());
            add(new DepositPolicyPreloader());
            add(new LeaseAdjustmentPolicyPreloader());
            add(new LeaseBillingPolicyPreloader());
        } else {
            add(new LegalTermsPolicyPreloader());
            add(new MockupProductTaxPolicyPreloader());
            add(new MockupDepositPolicyPreloader());
            add(new MockupLeaseAdjustmentPolicyPreloader());
            add(new MockupLeaseBillingPolicyPreloader());
            add(new MockupOnlineApplicationPolicyPreloader());
            add(new MockupLeaseSigningPolicyPreloader());
            add(new MockupProspectPortalPolicyPreloader());
        }
    }

}
