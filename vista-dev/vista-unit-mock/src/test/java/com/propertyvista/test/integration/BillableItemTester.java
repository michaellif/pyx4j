/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.integration;

import java.math.BigDecimal;

import com.propertyvista.domain.tenant.lease.BillableItem;

public class BillableItemTester extends Tester {

    private final BillableItem billableItem;

    public BillableItemTester(BillableItem billableItem) {
        this.billableItem = billableItem;
    }

    public BillableItemTester uid(String value) {
        assertEquals("uid", value, billableItem.uuid().getValue());
        return this;
    }

    public BillableItemTester agreedPrice(String value) {
        assertEquals("Agreed Price", new BigDecimal(value), billableItem.agreedPrice().getValue());
        return this;
    }

    public BillableItemTester effectiveDate(String date) {
        assertEquals("Effective Date", getDate(date), billableItem.effectiveDate().getValue());
        return this;
    }

    public BillableItemTester expirationDate(String date) {
        assertEquals("Expiration Date", getDate(date), billableItem.expirationDate().getValue());
        return this;
    }

    public BillableItemTester description(String value) {
        assertEquals("Description", value, billableItem.description().getValue());
        return this;
    }
}
