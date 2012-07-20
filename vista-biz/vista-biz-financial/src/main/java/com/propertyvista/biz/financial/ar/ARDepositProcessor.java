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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.DepositLifecycle.DepositStatus;

public class ARDepositProcessor extends AbstractARProcessor {

    private static final I18n i18n = I18n.get(ARDepositProcessor.class);

    void postDepositRefund(Deposit deposit) {
        assert (!deposit.lifecycle().isNull());

        InvoiceDepositRefund refund = EntityFactory.create(InvoiceDepositRefund.class);
        refund.deposit().set(deposit.lifecycle());
        refund.amount().setValue(deposit.lifecycle().currentAmount().getValue().negate());
        refund.billingAccount().set(deposit.lifecycle().billingAccount());
        refund.description().setValue(i18n.tr("Deposit Refund"));
        refund.claimed().setValue(false);
        Persistence.service().persist(refund);

        ARTransactionManager.postInvoiceLineItem(refund);

        deposit.lifecycle().status().setValue(DepositStatus.Returned);
        Persistence.service().persist(deposit);
    }
}
