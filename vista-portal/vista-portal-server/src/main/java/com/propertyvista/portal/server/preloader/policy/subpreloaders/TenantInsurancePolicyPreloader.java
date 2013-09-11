/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class TenantInsurancePolicyPreloader extends AbstractPolicyPreloader<TenantInsurancePolicy> {

    public TenantInsurancePolicyPreloader() {
        super(TenantInsurancePolicy.class);
    }

    @Override
    protected TenantInsurancePolicy createPolicy(StringBuilder log) {
        TenantInsurancePolicy policy = EntityFactory.create(TenantInsurancePolicy.class);
        policy.requireMinimumLiability().setValue(false);

        policy.noInsuranceStatusMessage().setValue("According to our records you do not have valid tenant insurance!");

        policy.tenantInsuranceInvitation().setValue(

        "As per your Lease Agreement, you must obtain and provide the landlord with proof of tenant insurance. "

        + "We have teamed up with Highcourt Partners Limited, a licensed broker, to assist you in obtaining your tenant insurance.");

        return policy;

    }

}
