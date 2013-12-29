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
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy.BjccEntry;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class BackgroundCheckPolicyPreloader extends AbstractPolicyPreloader<BackgroundCheckPolicy> {

    public BackgroundCheckPolicyPreloader() {
        super(BackgroundCheckPolicy.class);
    }

    @Override
    protected BackgroundCheckPolicy createPolicy(StringBuilder log) {
        BackgroundCheckPolicy policy = EntityFactory.create(BackgroundCheckPolicy.class);

        policy.version().bankruptcy().setValue(BjccEntry.m12);
        policy.version().judgment().setValue(BjccEntry.m12);
        policy.version().collection().setValue(BjccEntry.m12);
        policy.version().chargeOff().setValue(BjccEntry.m12);
        policy.strategyNumber().setValue(1);

        return policy;
    }
}
