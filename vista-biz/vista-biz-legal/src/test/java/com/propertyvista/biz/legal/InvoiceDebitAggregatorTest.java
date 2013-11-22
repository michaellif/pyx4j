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
import java.util.Arrays;
import java.util.HashMap;
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
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;

public class InvoiceDebitAggregatorTest {

    private static long gensym = 1L;

    private LinkedList<InvoiceDebit> debits;

    @Before
    public void setUp() {
        debits = new LinkedList<InvoiceDebit>();

        BillingCycle billingCycleA = createBillingCycle(1, "2010-01-01", "2010-01-31");
        BillingCycle billingCycleB = createBillingCycle(2, "2010-02-01", "2010-02-28");
        BillingCycle billingCycleC = createBillingCycle(3, "2010-03-01", "2010-03-31");

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

    @Test
    public void testDebitsForPeriod() {
        Map<BillingCycle, List<InvoiceDebit>> aggregatedDebits = new HashMap<BillingCycle, List<InvoiceDebit>>();
        BillingCycle billingCycleA = createBillingCycle(1, "2013-12-01", "2013-12-31");
        aggregatedDebits.put(billingCycleA, Arrays.asList(//@formatter:off
                createInvoiceDebit(billingCycleA, "1", "1"),
                createInvoiceDebit(billingCycleA, "2", "1"),
                createInvoiceDebit(billingCycleA, "3", "2")
        ));//@formatter:on

        BillingCycle billingCycleB = createBillingCycle(2, "2013-11-01", "2013-11-30");
        aggregatedDebits.put(billingCycleB, Arrays.asList(//@formatter:off
                        createInvoiceDebit(billingCycleB, "100", "100"),
                        createInvoiceDebit(billingCycleB, "200", "100"),
                        createInvoiceDebit(billingCycleB, "300", "200")
        ));//@formatter:on

        BillingCycle billingCycleC = createBillingCycle(3, "2013-10-01", "2013-10-31");
        aggregatedDebits.put(billingCycleC, Arrays.asList(//@formatter:off
                        createInvoiceDebit(billingCycleB, "1000", "1000"),
                        createInvoiceDebit(billingCycleB, "2000", "1000"),
                        createInvoiceDebit(billingCycleB, "3000", "2000")
        ));//@formaterr:on
        
        List<RentOwingForPeriod> debitsForPeriod = new InvoiceDebitAggregator().debitsForPeriod(aggregatedDebits);
        Assert.assertEquals(3, debitsForPeriod.size());
        
        Assert.assertEquals(new BigDecimal("6"), debitsForPeriod.get(2).rentCharged().getValue());
        Assert.assertEquals(new BigDecimal("4"), debitsForPeriod.get(2).rentPaid().getValue());
        Assert.assertEquals(new BigDecimal("2"), debitsForPeriod.get(2).rentOwing().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-12-01")), debitsForPeriod.get(2).from().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-12-31")), debitsForPeriod.get(2).to().getValue());
        
        Assert.assertEquals(new BigDecimal("600"), debitsForPeriod.get(1).rentCharged().getValue());
        Assert.assertEquals(new BigDecimal("400"), debitsForPeriod.get(1).rentPaid().getValue());
        Assert.assertEquals(new BigDecimal("200"), debitsForPeriod.get(1).rentOwing().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-11-01")), debitsForPeriod.get(1).from().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-11-30")), debitsForPeriod.get(1).to().getValue());

        Assert.assertEquals(new BigDecimal("6000"), debitsForPeriod.get(0).rentCharged().getValue());
        Assert.assertEquals(new BigDecimal("4000"), debitsForPeriod.get(0).rentPaid().getValue());
        Assert.assertEquals(new BigDecimal("2000"), debitsForPeriod.get(0).rentOwing().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-10-01")), debitsForPeriod.get(0).from().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-10-31")), debitsForPeriod.get(0).to().getValue());

    }
    
    @Test
    public void testDebitsForPeriodCombinePeriods() {        
        Map<BillingCycle, List<InvoiceDebit>> aggregatedDebits = new HashMap<BillingCycle, List<InvoiceDebit>>();
        BillingCycle billingCycleA = createBillingCycle(1, "2013-12-01", "2013-12-31");
        aggregatedDebits.put(billingCycleA, Arrays.asList(//@formatter:off
                createInvoiceDebit(billingCycleA, "1", "1"),
                createInvoiceDebit(billingCycleA, "2", "1"),
                createInvoiceDebit(billingCycleA, "3", "2")
        ));//@formatter:on

        BillingCycle billingCycleB = createBillingCycle(2, "2013-11-01", "2013-11-30");
        aggregatedDebits.put(billingCycleB, Arrays.asList(//@formatter:off
                        createInvoiceDebit(billingCycleB, "100", "100"),
                        createInvoiceDebit(billingCycleB, "200", "100"),
                        createInvoiceDebit(billingCycleB, "300", "200")
        ));//@formatter:on

        BillingCycle billingCycleC = createBillingCycle(3, "2013-10-01", "2013-10-31");
        aggregatedDebits.put(billingCycleC, Arrays.asList(//@formatter:off
                        createInvoiceDebit(billingCycleB, "1000", "100")                        
        ));//@formaterr:on
        
        BillingCycle billingCycleD = createBillingCycle(4, "2013-09-01", "2013-09-30");
        aggregatedDebits.put(billingCycleD, Arrays.asList(//@formatter:off
                createInvoiceDebit(billingCycleD, "1000", "100")                
        ));//@formaterr:on
        
        BillingCycle billingCycleE = createBillingCycle(5, "2013-08-01", "2013-08-31");
        aggregatedDebits.put(billingCycleE, Arrays.asList(//@formatter:off
                createInvoiceDebit(billingCycleE, "1000", "100")                
        ));//@formaterr:on
        
        List<RentOwingForPeriod> debitsForPeriod = new InvoiceDebitAggregator().debitsForPeriod(aggregatedDebits);
        Assert.assertEquals(3, debitsForPeriod.size());

        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-08-01")), debitsForPeriod.get(0).from().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-10-31")), debitsForPeriod.get(0).to().getValue());
        Assert.assertEquals(new BigDecimal("3000"), debitsForPeriod.get(0).rentCharged().getValue());
        Assert.assertEquals(new BigDecimal("300"), debitsForPeriod.get(0).rentPaid().getValue());
        Assert.assertEquals(new BigDecimal("2700"), debitsForPeriod.get(0).rentOwing().getValue());
        
        Assert.assertEquals(new BigDecimal("600"), debitsForPeriod.get(1).rentCharged().getValue());
        Assert.assertEquals(new BigDecimal("400"), debitsForPeriod.get(1).rentPaid().getValue());
        Assert.assertEquals(new BigDecimal("200"), debitsForPeriod.get(1).rentOwing().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-11-01")), debitsForPeriod.get(1).from().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-11-30")), debitsForPeriod.get(1).to().getValue());
        
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-12-01")), debitsForPeriod.get(2).from().getValue());
        Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2013-12-31")), debitsForPeriod.get(2).to().getValue());
        Assert.assertEquals(new BigDecimal("6"), debitsForPeriod.get(2).rentCharged().getValue());
        Assert.assertEquals(new BigDecimal("4"), debitsForPeriod.get(2).rentPaid().getValue());
        Assert.assertEquals(new BigDecimal("2"), debitsForPeriod.get(2).rentOwing().getValue());
        
    }
     

    private BillingCycle createBillingCycle(long id, String from, String to) {
        BillingCycle billingCycle = EntityFactory.create(BillingCycle.class);
        billingCycle.setPrimaryKey(new Key(id));
        billingCycle.billingCycleStartDate().setValue(from != null ? new LogicalDate(DateUtils.detectDateformat(from)) : null);
        billingCycle.billingCycleEndDate().setValue(to != null ? new LogicalDate(DateUtils.detectDateformat(to)) : null);
        return billingCycle;
    }

    private InvoiceDebit createInvoiceDebit(BillingCycle billingCycle, String charged, String paid) {
        InvoiceDebit invoiceDebit = EntityFactory.create(InvoiceDebit.class);
        invoiceDebit.setPrimaryKey(new Key(gensym));
        invoiceDebit.billingCycle().set(billingCycle);
        invoiceDebit.amount().setValue(new BigDecimal(charged));
        invoiceDebit.taxTotal().setValue(BigDecimal.ZERO);
        invoiceDebit.outstandingDebit().setValue(invoiceDebit.amount().getValue().subtract(new BigDecimal(paid)));
        return invoiceDebit;
    }
}
