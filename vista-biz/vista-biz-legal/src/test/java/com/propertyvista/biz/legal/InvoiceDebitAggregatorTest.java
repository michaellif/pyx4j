/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-10-01
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceDebit;

public class InvoiceDebitAggregatorTest {

    private static long gensym = 1L;

    private LinkedList<InvoiceDebit> debits;

    @Before
    public void setUp() {
        debits = new LinkedList<InvoiceDebit>();

        BillingCycle billingCycleA = createBillingCycle(1, new LogicalDate(DateUtils.detectDateformat("2010-01-01")),
                new LogicalDate(DateUtils.detectDateformat("2010-01-31")));
        BillingCycle billingCycleB = createBillingCycle(2, new LogicalDate(DateUtils.detectDateformat("2010-02-01")),
                new LogicalDate(DateUtils.detectDateformat("2010-02-28")));
        BillingCycle billingCycleC = createBillingCycle(3, new LogicalDate(DateUtils.detectDateformat("2010-03-01")),
                new LogicalDate(DateUtils.detectDateformat("2010-03-31")));

        debits.add(createInvoiceDebit(billingCycleA, "1000", "0"));
        debits.add(createInvoiceDebit(billingCycleB, "2000", "0"));
        debits.add(createInvoiceDebit(billingCycleC, "3000", "0"));
        debits.add(createInvoiceDebit(billingCycleA, "1000", "0"));
        debits.add(createInvoiceDebit(billingCycleB, "2000", "0"));
        debits.add(createInvoiceDebit(billingCycleC, "3000", "0"));
        debits.add(createInvoiceDebit(billingCycleA, "1000", "0"));
        debits.add(createInvoiceDebit(billingCycleC, "3000", "0"));
        debits.add(createInvoiceDebit(billingCycleB, "2000", "0"));
        debits.add(createInvoiceDebit(billingCycleC, "3000", "0"));
        debits.add(createInvoiceDebit(billingCycleB, "2000", "0"));
        debits.add(createInvoiceDebit(billingCycleC, "3000", "0"));

    }

    @Test
    public void testAggregate() {
        Map<BillingCycle, List<InvoiceDebit>> aggregatedDebits = new InvoiceDebitAggregator().aggregate(debits);
        List<InvoiceDebit> a = aggregatedDebits.get(createBillingCycle(1, null, null));
        List<InvoiceDebit> b = aggregatedDebits.get(createBillingCycle(2, null, null));
        List<InvoiceDebit> c = aggregatedDebits.get(createBillingCycle(3, null, null));

        Assert.assertEquals(3, a.size());
        for (InvoiceDebit i : a) {
            Assert.assertEquals(new BigDecimal("1000"), i.amount().getValue());
        }

        Assert.assertEquals(4, b.size());
        for (InvoiceDebit i : b) {
            Assert.assertEquals(new BigDecimal("2000"), i.amount().getValue());
        }

        Assert.assertEquals(5, c.size());
        for (InvoiceDebit i : c) {
            Assert.assertEquals(new BigDecimal("3000"), i.amount().getValue());
        }
    }

    private BillingCycle createBillingCycle(long id, LogicalDate from, LogicalDate to) {
        BillingCycle billingCycle = EntityFactory.create(BillingCycle.class);
        billingCycle.setPrimaryKey(new Key(id));
        billingCycle.billingCycleStartDate().setValue(from);
        billingCycle.billingCycleEndDate().setValue(from);
        return billingCycle;
    }

    private InvoiceDebit createInvoiceDebit(BillingCycle billingCycle, String charged, String paid) {
        InvoiceDebit invoiceDebit = EntityFactory.create(InvoiceDebit.class);
        invoiceDebit.setPrimaryKey(new Key(gensym));
        invoiceDebit.billingCycle().set(billingCycle);
        invoiceDebit.amount().setValue(new BigDecimal(charged));
        invoiceDebit.outstandingDebit().setValue(invoiceDebit.amount().getValue().subtract(new BigDecimal(paid)));
        return invoiceDebit;
    }
}
