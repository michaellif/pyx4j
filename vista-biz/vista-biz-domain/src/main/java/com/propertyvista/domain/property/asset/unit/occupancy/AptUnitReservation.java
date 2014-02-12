/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit.occupancy;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public interface AptUnitReservation extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    AptUnit unit();

    @OrderColumn
    @NotNull
    IPrimitive<Date> dateFrom();

    IPrimitive<Date> dateTo();

    @Detached
    Lease lease();

}
