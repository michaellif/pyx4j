/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.financial.billing.InvoiceCharge;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class ARArrearsManagerHelperTestBase extends TestCase {

    protected InvoiceDebit makeCharge(DebitType debitType, String amount, String outstandingDebit, String dueDate) {
        InvoiceCharge charge = EntityFactory.create(InvoiceCharge.class);
        charge.debitType().setValue(debitType);
        charge.amount().setValue(new BigDecimal(amount));
        charge.dueDate().setValue(new LogicalDate(DateUtils.detectDateformat(dueDate)));
        return charge;
    }
}
