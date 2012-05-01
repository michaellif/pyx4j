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

import com.propertyvista.portal.server.preloader.policy.subpreloaders.ApplicationDocumentationPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.DepositPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.EmailTemplatesPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.IdAssignmentPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseAdjustmentPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseBillingPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseTermsPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MiscPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ProductTaxPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.util.SimplePoliciesPreloader;

public class PreloadPolicies extends SimplePoliciesPreloader {

    public PreloadPolicies(boolean isProduction) {
        add(new MiscPolicyPreloader());
        add(new ApplicationDocumentationPolicyPreloader());
        add(new LeaseTermsPolicyPreloader());
        add(new EmailTemplatesPolicyPreloader());
        add(new DepositPolicyPreloader());
        add(new LeaseBillingPolicyPreloader());
        add(new IdAssignmentPolicyPreloader());

        if (!isProduction) {
            add(new ProductTaxPolicyPreloader());
            add(new LeaseAdjustmentPolicyPreloader());
        }
    }
}
