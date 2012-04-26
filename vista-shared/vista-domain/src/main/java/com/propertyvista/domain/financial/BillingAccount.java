/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public interface BillingAccount extends IEntity {

    enum ProrationMethod {
        Actual, Standard, Annual
    }

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    Lease lease();

    @ReadOnly(allowOverrideNull = true)
    BillingCycle billingCycle();

    IPrimitive<String> accountNumber();

    /**
     * Assign to BillingRun during billing extract.
     * Set to null when last period bill has been approved or rejected.
     */
    BillingRun currentBillingRun();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<Bill> bills();

    /**
     * Immediate InvoiceLineItems are kept here between billing runs. Approved billing cleans this set.
     * 
     * @return
     */
    @Detached
    ISet<InvoiceLineItem> interimLineItems();

    /**
     * Counter for all (including failed) bills for given lease
     * 
     * @return
     */
    IPrimitive<Integer> billCounter();

    IPrimitive<Double> total();

    @Owned
    @OrderBy(PaymentRecord.OrderId.class)
    @Detached(level = AttachLevel.Detached)
    IList<PaymentRecord> payments();

    @Owned
    @OrderBy(LeaseAdjustment.OrderId.class)
    @Caption(name = "Lease Adjustments")
    @Detached()
    IList<LeaseAdjustment> adjustments();

    IPrimitive<ProrationMethod> prorationMethod();

    IPrimitive<Integer> billingPeriodStartDate();

    //Should have deposit value field

    // atb report

    /**
     * for newly created/converted existing leases:
     */
    IPrimitive<BigDecimal> initialBalance();
}
