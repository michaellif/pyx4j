/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.InvoiceDebitComparator;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class ARInvoiceDebitComparatorTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBucketAgeComparator() {
        setSysDate("01-Apr-2011");

        //Same day
        compareBucketAge("01-Apr-2011", "01-Apr-2011", 0);
        compareBucketAge("27-Mar-2011", "27-Mar-2011", 0);
        compareBucketAge("27-Mar-2010", "27-Mar-2010", 0);

        //Same bucket
        compareBucketAge("20-Mar-2011", "10-Mar-2011", 0);
        compareBucketAge("10-Mar-2011", "20-Mar-2011", 0);
        compareBucketAge("20-Feb-2011", "10-Feb-2011", 0);
        compareBucketAge("10-Feb-2011", "20-Feb-2011", 0);
        compareBucketAge("20-Jan-2011", "10-Jan-2011", 0);
        compareBucketAge("10-Jan-2011", "20-Jan-2011", 0);
        compareBucketAge("20-Dec-2010", "10-Dec-2010", 0);
        compareBucketAge("10-Dec-2010", "20-Dec-2010", 0);
        compareBucketAge("20-Oct-2010", "10-Oct-2010", 0);
        compareBucketAge("10-Oct-2010", "20-Oct-2010", 0);
        compareBucketAge("20-Jun-2010", "10-Oct-2010", 0);
        compareBucketAge("10-Oct-2010", "20-Jun-2010", 0);
        compareBucketAge("20-Jun-2011", "10-Oct-2011", 0);
        compareBucketAge("10-Oct-2011", "20-Jun-2011", 0);
        compareBucketAge("02-Mar-2011", "03-Mar-2011", 0);

        //Different buckets
        compareBucketAge("01-Mar-2011", "2-Mar-2011", -1);
        compareBucketAge("31-Mar-2011", "31-Jan-2011", 58);
        compareBucketAge("31-Jan-2011", "31-Mar-2011", -58);
        compareBucketAge("31-Jan-2011", "20-Dec-2010", 42);

    }

    private void compareBucketAge(String date1, String date2, int expected) {
        InvoiceDebit lineItem1 = EntityFactory.create(InvoiceAccountCharge.class);
        lineItem1.dueDate().setValue(getDate(date1));

        InvoiceDebit lineItem2 = EntityFactory.create(InvoiceAccountCharge.class);
        lineItem2.dueDate().setValue(getDate(date2));

        assertEquals("", expected, InvoiceDebitComparator.compareBucketAge(lineItem1, lineItem2));

    }
}
