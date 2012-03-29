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

    @ReadOnly
    BillingCycle billingCycle();

    IPrimitive<Integer> accountNumber();

    /**
     * Assign to BillingRun during billing extract.
     * Set to null when last period bill has been approved or rejected.
     */
    BillingRun currentBillingRun();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Bill> bills();

    @Detached
    ISet<InvoiceLineItem> interimLineItems();

    //TODO when VladS will provide deletion of polymorphic entities 
    //@Owned
    //@Detached(level = AttachLevel.Detached)
    //ISet<_InvoiceLineItem> lineItems();

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

}
