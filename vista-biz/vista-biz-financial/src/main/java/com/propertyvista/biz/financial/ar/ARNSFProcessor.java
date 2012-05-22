/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.AbstractProcessor;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceNSF;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;

public class ARNSFProcessor extends AbstractProcessor {

    private static final I18n i18n = I18n.get(ARNSFProcessor.class);

    void postNSFCharge(PaymentRecord paymentRecord) {
        Persistence.service().retrieve(paymentRecord.billingAccount());
        // get NSF policy data
        Persistence.service().retrieve(paymentRecord.billingAccount().lease());
        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                paymentRecord.billingAccount().lease().unit().belongsTo(), LeaseBillingPolicy.class);

        NsfFeeItem nsfItem = null;
        for (NsfFeeItem nsf : leaseBillingPolicy.nsfFees()) {
            if (paymentRecord.paymentMethod().type().getValue().equals(nsf.paymentType().getValue())) {
                nsfItem = nsf;
                break;
            }
        }
        if (nsfItem == null) {
            return;
        }

        InvoiceNSF charge = EntityFactory.create(InvoiceNSF.class);
        charge.billingAccount().set(paymentRecord.billingAccount());
        charge.consumed().setValue(false);
        charge.debitType().setValue(DebitType.nsf);
        charge.amount().setValue(nsfItem.fee().getValue());
        charge.dueDate().setValue(BillDateUtils.calculateNextBillDueDate(paymentRecord.billingAccount()));
        charge.description().setValue(i18n.tr("NSF fee"));
        charge.taxTotal().setValue(new BigDecimal("0.00"));
        charge.consumed().setValue(false);

        Persistence.service().persist(charge);

        ARTransactionManager.postInvoiceLineItem(charge);
    }

}
