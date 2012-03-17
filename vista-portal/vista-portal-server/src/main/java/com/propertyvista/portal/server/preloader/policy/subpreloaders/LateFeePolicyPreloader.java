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

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.LateFeePolicy;
import com.propertyvista.domain.policy.policies.LateFeePolicy.BaseFeeType;
import com.propertyvista.domain.policy.policies.LateFeePolicy.MaxTotalFeeType;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LateFeePolicyPreloader extends AbstractPolicyPreloader<LateFeePolicy> {

    private final static I18n i18n = I18n.get(LateFeePolicyPreloader.class);

    public LateFeePolicyPreloader() {
        super(LateFeePolicy.class);
    }

    @Override
    protected LateFeePolicy createPolicy(StringBuilder log) {
        LateFeePolicy policy = EntityFactory.create(LateFeePolicy.class);

        policy.baseFee().setValue(new BigDecimal(500.00));
        policy.baseFeeType().setValue(BaseFeeType.FlatAmount);
        policy.maxTotalFee().setValue(new BigDecimal(1000.00));
        policy.maxTotalFeeType().setValue(MaxTotalFeeType.FlatAmount);
        policy.minimumAmounDue().setValue(new BigDecimal(30));

        return policy;
    }
}
