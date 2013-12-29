/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;

/**
 * Defines bill day and billing period, for now created by request
 * 
 */
@Table(prefix = "billing")
@ToStringFormat("{0} on {1}")
public interface BillingType extends IEntity {

    @ToString
    @NotNull
    @MemberColumn(notNull = true)
    @Indexed(uniqueConstraint = true, group = { "A,1" })
    IPrimitive<BillingPeriod> billingPeriod();

    @ToString
    @NotNull
    @MemberColumn(notNull = true)
    @Indexed(uniqueConstraint = true, group = { "A,2" })
    IPrimitive<Integer> billingCycleStartDay();

}
