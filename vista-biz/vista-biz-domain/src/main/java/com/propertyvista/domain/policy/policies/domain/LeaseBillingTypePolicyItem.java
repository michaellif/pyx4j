/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 27, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

//TODO Find a better name

//See VISTA-2605 for comments - caption descriptions need to be changed

public interface LeaseBillingTypePolicyItem extends IEntity {

    @Owner
    @Detached
    @NotNull
    @MemberColumn(notNull = true)
    @Indexed
    @JoinColumn
    LeaseBillingPolicy leaseBillingPolicy();

    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();

    IPrimitive<BillingPeriod> billingPeriod();

    @Caption(description = "First day of Billing Cycle for selected Payment Frequency")
    IPrimitive<Integer> billingCycleStartDay();

    @NotNull
    @Caption(description = "When to create Bill for the next cycle, relative to Start Day")
    IPrimitive<Integer> billExecutionDayOffset();

    @NotNull
    @Caption(description = "Bill payment Due day, relative to Start Day")
    IPrimitive<Integer> paymentDueDayOffset();

    @NotNull
    @Caption(description = "Final Bill payment Due day, relative to Lease End Day")
    IPrimitive<Integer> finalDueDayOffset();

    @NotNull
    @Caption(description = "When to run PreAuthorized Payments, relative to Start Day")
    IPrimitive<Integer> autopayExecutionDayOffset();

}
