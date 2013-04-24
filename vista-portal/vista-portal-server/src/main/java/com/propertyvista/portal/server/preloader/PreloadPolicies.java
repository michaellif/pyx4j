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

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ARPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ApplicationDocumentationPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.BackgroundCheckPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.DatesPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.DepositPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.EmailTemplatesPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.IdAssignmentPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseBillingPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LegalDocumentationPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupDepositPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupLeaseAdjustmentPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupLeaseBillingPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupPADPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MockupProductTaxPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.PADPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.PaymentMethodSelectionPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ProductTaxPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.RestrictionsPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.TenantInsurancePolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPoliciesPreloader;
import com.propertyvista.portal.server.preloader.policy.util.OrganizationPoliciesNodePreloader;
import com.propertyvista.shared.config.VistaFeatures;

public class PreloadPolicies extends AbstractPoliciesPreloader {

    public PreloadPolicies(boolean isProduction) {
        add(new OrganizationPoliciesNodePreloader());
        add(new ARPolicyPreloader());
        add(new ApplicationDocumentationPolicyPreloader());
        add(new EmailTemplatesPolicyPreloader());
        add(new IdAssignmentPolicyPreloader());
        add(new RestrictionsPolicyPreloader());
        addLocalRestrictionsPolicies();
        add(new DatesPolicyPreloader());
        add(new TenantInsurancePolicyPreloader());
        add(new PaymentMethodSelectionPolicyPreloader());

        if (!VistaTODO.Equifax_Short_VISTA_478) {
            add(new BackgroundCheckPolicyPreloader());
        }

        if (isProduction) {
            add(new ProductTaxPolicyPreloader());
            add(new DepositPolicyPreloader());
            add(new LeaseAdjustmentPolicyPreloader());
            add(new LeaseBillingPolicyPreloader());
            add(new PADPolicyPreloader());
        } else {
            add(new LegalDocumentationPolicyPreloader());
            add(new MockupProductTaxPolicyPreloader());
            add(new MockupDepositPolicyPreloader());
            add(new MockupLeaseAdjustmentPolicyPreloader());
            add(new MockupLeaseBillingPolicyPreloader());
            add(new MockupPADPolicyPreloader());
        }
    }

    private void addLocalRestrictionsPolicies() {
        if (false) { // TODO this is for commit only: it seems that calling VistaFeatures.instance().countryOfOperation() screws the testDefaultPreload 
            if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                add(new RestrictionsPolicyPreloader().province("AB").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("BC").ageOfMajority(19));
                add(new RestrictionsPolicyPreloader().province("MB").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("NB").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("NL").ageOfMajority(19));
                add(new RestrictionsPolicyPreloader().province("NT").ageOfMajority(19));
                add(new RestrictionsPolicyPreloader().province("NS").ageOfMajority(19));
                add(new RestrictionsPolicyPreloader().province("NU").ageOfMajority(19));
                add(new RestrictionsPolicyPreloader().province("ON").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("PE").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("QC").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("SK").ageOfMajority(18));
                add(new RestrictionsPolicyPreloader().province("YT").ageOfMajority(19));
            }
        }
    }

}
