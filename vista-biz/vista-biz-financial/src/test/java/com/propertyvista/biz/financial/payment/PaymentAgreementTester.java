/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.test.integration.Tester;

public class PaymentAgreementTester extends Tester {

    private final BillingAccount billingAccount;

    private final List<PreauthorizedPayment> paymentRecords;

    public PaymentAgreementTester(BillingAccount billingAccount) {
        super();
        this.billingAccount = billingAccount;
        EntityQueryCriteria<PreauthorizedPayment> criteria = new EntityQueryCriteria<PreauthorizedPayment>(PreauthorizedPayment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant().lease().billingAccount(), billingAccount));
        criteria.asc(criteria.proto().id());
        paymentRecords = Persistence.service().query(criteria);
    }

    public PaymentAgreementTester count(int size) {
        assertEquals("Records count", size, paymentRecords.size());
        return this;
    }

    public PreauthorizedPayment lastRecord() {
        return paymentRecords.get(paymentRecords.size() - 1);
    }

    public PaymentAgreementTester lastRecordAmount(String amount) {
        BigDecimal paAmount = BigDecimal.ZERO;
        for (PreauthorizedPaymentCoveredItem item : lastRecord().coveredItems()) {
            paAmount = paAmount.add(item.amount().getValue());
        }
        assertEquals("Amount of last Record", new BigDecimal(amount), paAmount);
        return this;
    }
}
