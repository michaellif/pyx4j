/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.integration;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;

public class TenantSureTransactionTester extends Tester {

    private final TenantSureInsurancePolicy tenantSurePolicy;

    private final List<TenantSureTransaction> paymentRecords;

    public TenantSureTransactionTester(TenantSureInsurancePolicy tenantSurePolicy) {
        super();
        this.tenantSurePolicy = tenantSurePolicy;
        EntityQueryCriteria<TenantSureTransaction> criteria = EntityQueryCriteria.create(TenantSureTransaction.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().insurance(), tenantSurePolicy));
        criteria.asc(criteria.proto().id());
        paymentRecords = Persistence.service().query(criteria);
    }

    private String infor() {
        return "Policy#" + tenantSurePolicy.getPrimaryKey();
    }

    public TenantSureTransactionTester count(int size) {
        assertEquals(infor() + " Records count", size, paymentRecords.size());
        return this;
    }

    private TenantSureTransaction lastRecord() {
        return paymentRecords.get(paymentRecords.size() - 1);
    }

    public TenantSureTransactionTester lastRecordStatus(TenantSureTransaction.TransactionStatus paymentStatus) {
        TenantSureTransaction lastRecord = lastRecord();
        assertEquals(infor() + " Status of last Record " + lastRecord.getPrimaryKey(), //
                paymentStatus, lastRecord.status().getValue());
        return this;
    }

    public TenantSureTransactionTester lastRecordAmount(String amount) {
        TenantSureTransaction lastRecord = lastRecord();
        assertEquals(infor() + " Amount of last Record " + lastRecord.getPrimaryKey(), //
                new BigDecimal(amount), lastRecord.amount().getValue());
        return this;
    }

    public TenantSureTransactionTester lastRecordDate(String dateStr) {
        TenantSureTransaction lastRecord = lastRecord();
        assertEquals(infor() + " Date of last Record " + lastRecord.getPrimaryKey(), // 
                dateStr, new LogicalDate(lastRecord.transactionDate().getValue()));
        return this;
    }

}
