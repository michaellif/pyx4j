/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 19, 2015
 * @author stanp
 */
package com.propertyvista.domain.eviction;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.legal.n4.N4LeaseArrears;

@DiscriminatorValue("N4")
public interface EvictionStatusN4 extends EvictionStatus {

    @Detached
    N4LeaseArrears leaseArrears();

    @NotNull
    IPrimitive<LogicalDate> terminationDate();

    @NotNull
    IPrimitive<LogicalDate> expiryDate();

    @Editor(type = EditorType.money)
    @NotNull
    IPrimitive<BigDecimal> cancellationBalance();
}
