/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.AbstractProcessor;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.billing.InvoiceLatePaymentFee;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

public class BillingLatePaymentFeeProcessor extends AbstractProcessor {

    private static final I18n i18n = I18n.get(BillingLatePaymentFeeProcessor.class);

    private final Billing billing;

    BillingLatePaymentFeeProcessor(Billing billing) {
        this.billing = billing;
    }

    public void createLatePaymentFeeItem() {
        // TODO Auto-generated method stub

        BigDecimal dueFromPreviousBill = billing.getNextPeriodBill().balanceForwardAmount().getValue();
        //Exit if negative

        BigDecimal serviceCharge = billing.getNextPeriodBill().serviceCharge().getValue();

        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                billing.getNextPeriodBill().billingRun().building(), LeaseBillingPolicy.class);

        BigDecimal latePaymentFee = LatePaymentUtils.calculateLatePaymentFee(dueFromPreviousBill, serviceCharge, leaseBillingPolicy);

        InvoiceLatePaymentFee charge = EntityFactory.create(InvoiceLatePaymentFee.class);
        charge.bill().set(billing.getNextPeriodBill());
        charge.dueDate().setValue(billing.getNextPeriodBill().billingPeriodStartDate().getValue());
        charge.amount().setValue(latePaymentFee);
        charge.description().setValue(i18n.tr("Late payment fee"));

        Persistence.service().persist(charge);

        billing.getNextPeriodBill().lineItems().add(charge);

        billing.getNextPeriodBill().pendingAccountAdjustments()
                .setValue(billing.getNextPeriodBill().pendingAccountAdjustments().getValue().add(charge.amount().getValue()));

    }

}
