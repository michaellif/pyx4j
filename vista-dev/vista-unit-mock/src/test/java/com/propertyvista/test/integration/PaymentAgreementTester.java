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
package com.propertyvista.test.integration;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;

public class PaymentAgreementTester extends Tester {

    private final BillingAccount billingAccount;

    private final List<AutopayAgreement> papRecords;

    public PaymentAgreementTester(BillingAccount billingAccount) {
        super();
        this.billingAccount = billingAccount;
        EntityQueryCriteria<AutopayAgreement> criteria = new EntityQueryCriteria<AutopayAgreement>(AutopayAgreement.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant().lease().billingAccount(), billingAccount));
        criteria.asc(criteria.proto().id());
        papRecords = Persistence.service().query(criteria);
    }

    public PaymentAgreementTester count(int size) {
        assertEquals("Records count", size, papRecords.size());
        return this;
    }

    public PaymentAgreementTester activeCount(int size) {
        int count = 0;
        for (AutopayAgreement pap : papRecords) {
            if (!pap.isDeleted().getValue(false)) {
                count++;
            }
        }
        assertEquals("Active Records count", size, count);
        return this;
    }

    public AutopayAgreement lastRecord() {
        return papRecords.get(papRecords.size() - 1);
    }

    public PaymentAgreementTester lastRecordAmount(String amount) {
        BigDecimal paAmount = BigDecimal.ZERO;
        for (AutopayAgreementCoveredItem item : lastRecord().coveredItems()) {
            paAmount = paAmount.add(item.amount().getValue());
        }
        assertEquals("Amount of last Record", new BigDecimal(amount), paAmount);
        return this;
    }
}
