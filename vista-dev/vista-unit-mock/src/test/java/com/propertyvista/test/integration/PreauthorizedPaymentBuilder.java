/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.integration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class PreauthorizedPaymentBuilder {

    private final List<PreauthorizedPaymentCoveredItem> items = new ArrayList<PreauthorizedPaymentCoveredItem>();

    public PreauthorizedPaymentBuilder add(BillableItem billableItem) {
        return add(billableItem, billableItem.agreedPrice().getValue().toString());
    }

    public PreauthorizedPaymentBuilder add(BillableItem billableItem, String amount) {
        PreauthorizedPaymentCoveredItem item = EntityFactory.create(PreauthorizedPaymentCoveredItem.class);
        item.billableItem().set(billableItem);
        item.amount().setValue(new BigDecimal(amount));
        items.add(item);
        return this;
    }

    public List<PreauthorizedPaymentCoveredItem> build() {
        return items;
    }
}
