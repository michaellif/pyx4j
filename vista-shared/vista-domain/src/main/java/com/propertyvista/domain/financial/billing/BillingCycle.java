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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
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
    @Detached
    @JoinColumn
    Building building();

    @ReadOnly
    @ToString(index = 1)
    BillingType billingType();

    @ReadOnly
    IPrimitive<LogicalDate> billingPeriodStartDate();

    @ReadOnly
    IPrimitive<LogicalDate> billingPeriodEndDate();

    @ReadOnly
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> executionTargetDate();
}
