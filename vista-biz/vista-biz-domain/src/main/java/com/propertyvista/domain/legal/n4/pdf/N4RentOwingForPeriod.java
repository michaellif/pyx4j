/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-20
 * @author ArtyomB
 */
package com.propertyvista.domain.legal.n4.pdf;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface N4RentOwingForPeriod extends IEntity {

    @NotNull
    @ToString(index = 2)
    IPrimitive<LogicalDate> fromDate();

    @NotNull
    @ToString(index = 3)
    IPrimitive<LogicalDate> toDate();

    @NotNull
    @Format("$#,##0.00")
    IPrimitive<BigDecimal> rentCharged();

    @NotNull
    @Format("$#,##0.00")
    IPrimitive<BigDecimal> rentPaid();

    @NotNull
    @ToString(index = 0)
    @Format("$#,##0.00")
    IPrimitive<BigDecimal> rentOwing();
}
