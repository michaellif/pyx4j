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

import com.propertyvista.domain.financial.BillingAccount.ProrationMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy.BillConfirmationMethod;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LeaseBillingPolicyPreloader extends AbstractPolicyPreloader<LeaseBillingPolicy> {

    private final static I18n i18n = I18n.get(LeaseBillingPolicyPreloader.class);

    public LeaseBillingPolicyPreloader() {
        super(LeaseBillingPolicy.class);
    }

    @Override
    protected LeaseBillingPolicy createPolicy(StringBuilder log) {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        policy.defaultBillingCycleSartDay().setValue(1);
        policy.useDefaultBillingCycleSartDay().setValue(true);

        policy.prorationMethod().setValue(ProrationMethod.Standard);

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

        policy.confirmationMethod().setValue(BillConfirmationMethod.manual);

        return policy;
    }
}
