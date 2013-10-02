/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class N4PolicyPreloader extends AbstractPolicyPreloader<N4Policy> {

    public N4PolicyPreloader() {
        super(N4Policy.class);
    }

    @Override
    protected N4Policy createPolicy(StringBuilder log) {
        N4Policy policy = EntityFactory.create(N4Policy.class);
        policy.includeSignature().setValue(true);

        log.append(policy.getStringView());
        return policy;
    }

}
