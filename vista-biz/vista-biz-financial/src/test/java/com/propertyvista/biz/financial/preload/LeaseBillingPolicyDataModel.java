/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.biz.financial.preload;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.BillingAccount.ProrationMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;

public class LeaseBillingPolicyDataModel {
    private final BuildingDataModel buildingDataModel;

    private LeaseBillingPolicy policy;

    private final PreloadConfig config;

    public LeaseBillingPolicyDataModel(PreloadConfig config, BuildingDataModel buildingDataModel) {
        this.config = config;
        this.buildingDataModel = buildingDataModel;
    }

    public void generate() {
        policy = EntityFactory.create(LeaseBillingPolicy.class);

        policy.defaultBillingCycleSartDay().setValue(config.defaultBillingCycleSartDay);
        policy.useDefaultBillingCycleSartDay().setValue(false);
        policy.prorationMethod().setValue(ProrationMethod.Actual);

        LateFeeItem lateFee = EntityFactory.create(LateFeeItem.class);
        lateFee.baseFee().setValue(new BigDecimal(50.00));
        lateFee.baseFeeType().setValue(BaseFeeType.FlatAmount);
        lateFee.maxTotalFee().setValue(new BigDecimal(1000.00));
        lateFee.maxTotalFeeType().setValue(LateFeeItem.MaxTotalFeeType.FlatAmount);
        policy.lateFee().set(lateFee);

        NsfFeeItem nsfItem = EntityFactory.create(NsfFeeItem.class);
        nsfItem.paymentType().setValue(PaymentType.Cash);
        nsfItem.fee().setValue(new BigDecimal(100.00));
        policy.nsfFees().add(nsfItem);

        nsfItem = EntityFactory.create(NsfFeeItem.class);
        nsfItem.paymentType().setValue(PaymentType.Check);
        nsfItem.fee().setValue(new BigDecimal(30.00));
        policy.nsfFees().add(nsfItem);

        policy.confirmationMethod().setValue(config.billConfirmationMethod);

        policy.node().set(buildingDataModel.getBuilding());

        Persistence.service().persist(policy);
    }

    LeaseBillingPolicy getPolicy() {
        return policy;
    }
}
