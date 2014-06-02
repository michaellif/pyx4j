/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.GeneratedValue;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;

import com.propertyvista.domain.company.Employee;

public interface BillableItemAdjustment extends IEntity {

    @GeneratedValue(type = GeneratedValue.GenerationType.randomUUID)
    IPrimitive<String> uid();

    @Caption(name = "Last Updated")
    @Timestamp
    IPrimitive<LogicalDate> updated();

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    BillableItem billableItem();

    @OrderColumn
    IPrimitive<Integer> orderInParent();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "adjustmentType")
    IPrimitive<ValueType> type();

    /*
     * for percentage - percentage
     * for monetary - amount
     */
    @NotNull
    @ToString(index = 1)
    @MemberColumn(name = "adjustmentValue")
    IMoneyPercentAmount value();

    IPrimitive<String> description();

    @Caption(description = "Empty value assumes Billable Item effective date")
    IPrimitive<LogicalDate> effectiveDate();

    @Caption(description = "Empty value assumes Billable Item expiration date")
    IPrimitive<LogicalDate> expirationDate();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createdWhen();

    Employee createdBy();
}
