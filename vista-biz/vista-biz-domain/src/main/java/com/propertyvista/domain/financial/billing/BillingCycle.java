/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author vladlouk
 * @version $Id$
 */

package com.propertyvista.domain.financial.billing;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

//TODO - see @Comments for new labels as per VISTA-2605 - alexs

@ToStringFormat("{0}, {1}")
@Table(prefix = "billing")
public interface BillingCycle extends IEntity {

    @ToString(index = 0)
    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @Detached
    @JoinColumn
    @Indexed(group = "BD,1", uniqueConstraint = true)
    Building building();

    @ReadOnly
    @ToString(index = 1)
    BillingType billingType();

    // @Comment(name = "Start Day",description = "First day of the Billing Period within Payment Frequency")
    @ReadOnly
    @Indexed(group = "BD,2")
    IPrimitive<LogicalDate> billingCycleStartDate();

    @ReadOnly
    IPrimitive<LogicalDate> billingCycleEndDate();

    // @Comment(name = "Bill Processing Day", description = "Number of days between Bill Processing Day and Billing Period Start Date") 
    IPrimitive<LogicalDate> targetBillExecutionDate();

    IPrimitive<LogicalDate> targetAutopayExecutionDate();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<LogicalDate> actualBillExecutionDate();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<LogicalDate> actualAutopayExecutionDate();

    // Used as different entity to avoid transaction isolation problems in HDQLDB
    @Owned
    @Detached(level = AttachLevel.Detached)
    BillingCycleStats stats();

}
