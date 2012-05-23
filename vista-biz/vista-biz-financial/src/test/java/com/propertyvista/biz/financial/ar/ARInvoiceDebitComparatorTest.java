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
package com.propertyvista.biz.financial.ar;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestsUtils;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class ARInvoiceDebitComparatorTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBucketAgeComparator() {
        SysDateManager.setSysDate("01-Mar-2011");

        InvoiceDebit lineItem = EntityFactory.create(InvoiceAccountCharge.class);
        lineItem.dueDate().setValue(FinancialTestsUtils.getDate("18-Mar-2011"));
        lineItem.debitType().setValue(DebitType.accountCharge);

    }
}
