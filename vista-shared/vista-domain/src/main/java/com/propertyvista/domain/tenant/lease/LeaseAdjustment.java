/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.company.Employee;

public interface LeaseAdjustment extends IEntity {

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "itemType")
    LeaseAdjustmentReason reason();

    @NotNull
    @ToString(index = 1)
    @Format("#0.00")
    IPrimitive<BigDecimal> amount();

    IPrimitive<String> description();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> effectiveDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> expirationDate();

    IPrimitive<Integer> billingPeriodNumber();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createdWhen();

    Employee createdBy();
}