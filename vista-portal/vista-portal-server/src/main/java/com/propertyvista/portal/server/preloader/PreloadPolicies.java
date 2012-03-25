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

import java.util.Arrays;
import java.util.List;

import com.propertyvista.portal.server.preloader.policy.subpreloaders.ApplicationDocumentationPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.DepositPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.EmailTemplatesPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseBillingPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LeaseTermsPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.MiscPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ProductTaxPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.util.SimplePoliciesPreloader;

public class PreloadPolicies extends SimplePoliciesPreloader {

    private static final List<Class<? extends AbstractPolicyPreloader<?>>> PRODUCTION = Arrays.asList(//@formatter:off
            (Class<? extends AbstractPolicyPreloader<?>>[])
            new Class<?>[] {
                    MiscPolicyPreloader.class,
                    ApplicationDocumentationPolicyPreloader.class,
                    LeaseTermsPolicyPreloader.class,
                    EmailTemplatesPolicyPreloader.class,
                    DepositPolicyPreloader.class,
                    LeaseBillingPolicyPreloader.class
            }
    ); //@formatter:on

    private static final List<Class<? extends AbstractPolicyPreloader<?>>> DEVELOPMENT = Arrays.asList(//@formatter:off
            (Class<? extends AbstractPolicyPreloader<?>>[])
            new Class<?>[] {
                    MiscPolicyPreloader.class,
                    ApplicationDocumentationPolicyPreloader.class,
                    LeaseTermsPolicyPreloader.class,
                    EmailTemplatesPolicyPreloader.class,
                    ProductTaxPolicyPreloader.class,
                    DepositPolicyPreloader.class,
                    LeaseBillingPolicyPreloader.class
            }
    ); //@formatter:on

    public PreloadPolicies(boolean isProduction) {
        super(isProduction ? PRODUCTION : DEVELOPMENT);
    }
}
