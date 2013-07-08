/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 8, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.integration;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.Lease;

public class InvoiceLineItemTester extends Tester {

    private final BillingAccount billingAccount;

    public InvoiceLineItemTester(Lease lease) {
        this(lease.billingAccount());
    }

    public InvoiceLineItemTester(BillingAccount billingAccount) {
        super();
        this.billingAccount = billingAccount;
    }

    public <E extends InvoiceLineItem> InvoiceLineItemTester count(Class<E> lineIiemsClass, int size) {
        EntityQueryCriteria<E> criteria = new EntityQueryCriteria<E>(lineIiemsClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.asc(criteria.proto().id());
        assertEquals("Records count", size, Persistence.service().count(criteria));
        return this;
    }

    public <E extends InvoiceLineItem> InvoiceLineItemTester lastRecordAmount(Class<E> lineIiemsClass, String amount) {
        EntityQueryCriteria<E> criteria = new EntityQueryCriteria<E>(lineIiemsClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.asc(criteria.proto().id());
        E record = Persistence.service().retrieve(criteria);
        assertEquals("Amount of last Record", new BigDecimal(amount), record.amount().getValue());
        return this;
    }
}
