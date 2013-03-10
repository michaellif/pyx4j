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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{0}, {1}")
@Table(prefix = "billing")
public interface BillingCycle extends IEntity {

    @ToString(index = 0)
    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @Detached
    @JoinColumn
    @Indexed(group = "DBT,2", uniqueConstraint = true)
    Building building();

    @ReadOnly
    @ToString(index = 1)
    @Indexed(group = "DBT,3")
    BillingType billingType();

    @ReadOnly
    @Indexed(group = "DBT,1")
    IPrimitive<LogicalDate> billingCycleStartDate();

    @ReadOnly
    IPrimitive<LogicalDate> billingCycleEndDate();

    @ReadOnly
    IPrimitive<LogicalDate> executionTargetDate();

    IPrimitive<LogicalDate> padCalculationDate();

    IPrimitive<LogicalDate> padExecutionDate();

    // Used as different entity to avoid transaction isolation problems in HDQLDB
    @Owned
    BillingCycleStats stats();

}
