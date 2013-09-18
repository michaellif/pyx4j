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

        return policy;

    }

}
