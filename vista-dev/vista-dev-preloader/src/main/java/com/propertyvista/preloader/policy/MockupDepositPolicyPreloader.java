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
package com.propertyvista.preloader.policy;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.DepositPolicy;

public class MockupDepositPolicyPreloader extends AbstractPolicyPreloader<DepositPolicy> {

    private final static I18n i18n = I18n.get(MockupDepositPolicyPreloader.class);

    public MockupDepositPolicyPreloader() {
        super(DepositPolicy.class);
    }

    @Override
    protected DepositPolicy createPolicy(StringBuilder log) {
        DepositPolicy policy = EntityFactory.create(DepositPolicy.class);

        policy.annualInterestRate().setValue(new BigDecimal("0.11"));
        policy.securityDepositRefundWindow().setValue(11);

        log.append(policy.getStringView());

        return policy;
    }
}
