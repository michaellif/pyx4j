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
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.InvoiceNSF;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;

class ARInternalNSFManager {

    private static final I18n i18n = I18n.get(ARInternalNSFManager.class);

    private ARInternalNSFManager() {
    }

    private static class SingletonHolder {
        public static final ARInternalNSFManager INSTANCE = new ARInternalNSFManager();
    }

    static ARInternalNSFManager instance() {
        return SingletonHolder.INSTANCE;
    }

    void applyNSFCharge(PaymentRecord paymentRecord) {
        Persistence.service().retrieve(paymentRecord.billingAccount());
        // get NSF policy data
        Persistence.service().retrieve(paymentRecord.billingAccount().lease());
        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                paymentRecord.billingAccount().lease().unit().building(), LeaseBillingPolicy.class);

        NsfFeeItem nsfItem = null;
        for (NsfFeeItem nsf : leaseBillingPolicy.nsfFees()) {
            if (nsf.paymentType().getValue().equals(paymentRecord.paymentMethod().type().getValue())) {
                nsfItem = nsf;
                break;
            }
        }
        if (nsfItem == null) {
            return;
        }

        InvoiceNSF charge = EntityFactory.create(InvoiceNSF.class);
        charge.billingAccount().set(paymentRecord.billingAccount());
        charge.arCode().set(ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.NSF));
        charge.amount().setValue(nsfItem.fee().getValue());
        charge.dueDate().setValue(
                ARInternalTransactionManager.instance().getTransactionDueDate(paymentRecord.billingAccount(), new LogicalDate(SystemDateManager.getDate())));

        charge.description().setValue(i18n.tr("NSF fee"));
        charge.taxTotal().setValue(BigDecimal.ZERO);
        charge.paymentRecord().set(paymentRecord);

        Persistence.service().persist(charge);

        ARInternalTransactionManager.instance().postInvoiceLineItem(charge);
    }
}
