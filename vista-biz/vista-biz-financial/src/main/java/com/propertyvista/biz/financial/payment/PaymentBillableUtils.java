/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class PaymentBillableUtils {

    //TODO proper implementation that will use adjustments
    public static BigDecimal getActualPrice(BillableItem billableItem) {
        return billableItem.agreedPrice().getValue();
    }

    public static boolean isBillableItemPapable(BillableItem billableItem, BillingCycle cycle) {
        return (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(cycle.billingCycleStartDate().getValue()));
    }

    public static Map<String, BillableItem> getAllBillableItems(LeaseTerm.LeaseTermV leaseTermV) {
        Map<String, BillableItem> billableItems = new LinkedHashMap<String, BillableItem>();
        billableItems.put(leaseTermV.leaseProducts().serviceItem().uid().getValue(), leaseTermV.leaseProducts().serviceItem());
        for (BillableItem bi : leaseTermV.leaseProducts().featureItems()) {
            billableItems.put(bi.uid().getValue(), bi);
        }
        return billableItems;
    }
}
