/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author stanp
 */
package com.propertyvista.domain.legal.n4;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;

public interface N4BatchItem extends N4LeaseData {

    @Owner
    @JoinColumn
    @Indexed
    @ReadOnly
    @Detached
    N4Batch batch();

    @JoinColumn
    @Indexed
    @ReadOnly
    @Detached
    Lease lease();

    @Override
    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<N4RentOwingForPeriod> rentOwingBreakdown();

    @Override
    @Deprecated
    // TODO - can calculate this only when the delivery method is selected at the time of servicing N4, so not needed here at all
    IPrimitive<LogicalDate> terminationDate();

    @ReadOnly
    IPrimitive<Date> serviced();
}
