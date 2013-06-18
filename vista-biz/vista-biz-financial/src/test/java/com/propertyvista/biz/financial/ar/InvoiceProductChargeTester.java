/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;

import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.test.integration.Tester;

public class InvoiceProductChargeTester extends Tester {

    private final InvoiceProductCharge invoiceProductCharge;

    public InvoiceProductChargeTester(InvoiceProductCharge invoiceProductCharge) {
        super();
        this.invoiceProductCharge = invoiceProductCharge;
    }

    public InvoiceProductChargeTester amount(String value) {
        assertEquals("Charge Amount", new BigDecimal(value), invoiceProductCharge.amount().getValue());
        return this;
    }

    public InvoiceProductChargeTester taxTotal(String value) {
        assertEquals("Tax Total", new BigDecimal(value), invoiceProductCharge.taxTotal().getValue());
        return this;
    }
}
