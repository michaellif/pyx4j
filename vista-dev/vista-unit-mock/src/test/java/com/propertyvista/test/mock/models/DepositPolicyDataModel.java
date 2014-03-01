/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.test.mock.MockDataModel;

public class DepositPolicyDataModel extends MockDataModel<DepositPolicy> {

    public DepositPolicyDataModel() {
    }

    @Override
    protected void generate() {
        DepositPolicy policy = EntityFactory.create(DepositPolicy.class);

        policy.annualInterestRate().setValue(new BigDecimal("0.11"));
        policy.securityDepositRefundWindow().setValue(11);

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }

}
