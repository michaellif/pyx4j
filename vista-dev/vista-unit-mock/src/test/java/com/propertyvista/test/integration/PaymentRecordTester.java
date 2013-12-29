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
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;

public class PaymentRecordTester extends Tester {

    private final BillingAccount billingAccount;

    private final List<PaymentRecord> paymentRecords;

    public PaymentRecordTester(BillingAccount billingAccount) {
        super();
        this.billingAccount = billingAccount;
        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.asc(criteria.proto().id());
        paymentRecords = Persistence.service().query(criteria);
    }

    private String infor() {
        return billingAccount.lease().leaseId().getStringView();
    }

    public PaymentRecordTester count(int size) {
        assertEquals(infor() + " Records count", size, paymentRecords.size());
        return this;
    }

    private PaymentRecord lastRecord() {
        return paymentRecords.get(paymentRecords.size() - 1);
    }

    public PaymentRecordTester lastRecordStatus(PaymentStatus paymentStatus) {
        PaymentRecord lastRecord = lastRecord();
        assertEquals(infor() + " Status of last Record " + lastRecord.getPrimaryKey(), paymentStatus, lastRecord.paymentStatus().getValue());
        return this;
    }

    public PaymentRecordTester lastRecordAmount(String amount) {
        PaymentRecord lastRecord = lastRecord();
        assertEquals(infor() + " Amount of last Record " + lastRecord.getPrimaryKey(), new BigDecimal(amount), lastRecord.amount().getValue());
        return this;
    }
}
