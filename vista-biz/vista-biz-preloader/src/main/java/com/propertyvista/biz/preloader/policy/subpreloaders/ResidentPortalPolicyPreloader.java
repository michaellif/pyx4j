/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.ResidentPortalPolicy;

public class ResidentPortalPolicyPreloader extends AbstractPolicyPreloader<ResidentPortalPolicy> {

    public ResidentPortalPolicyPreloader() {
        super(ResidentPortalPolicy.class);
    }

    @Override
    protected ResidentPortalPolicy createPolicy(StringBuilder log) {
        ResidentPortalPolicy policy = EntityFactory.create(ResidentPortalPolicy.class);

        policy.communicationEnabled().setValue(true);

        log.append(policy.getStringView());
        return policy;
    }

}
