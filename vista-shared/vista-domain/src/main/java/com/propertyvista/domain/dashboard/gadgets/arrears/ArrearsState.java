/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.util.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.util.CustomComparator;
import com.propertyvista.domain.tenant.Tenant;

public interface ArrearsState extends IEntity {

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    Tenant tenant();

    IPrimitive<LogicalDate> asOf();

    @Format("#0.00")
    @Caption(name = "This month")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> thisMonth();

    @Format("#0.00")
    @Caption(name = "0 - 30 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> a0to30();

    @Format("#0.00")
    @Caption(name = "30 - 60 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> a30to60();

    @Format("#0.00")
    @Caption(name = "60- 90 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> a60to90();

    @Format("#0.00")
    @Caption(name = "Over 90 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> over90();

    @Format("#0.00")
    @Caption(name = "AR Balance")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> arBalance();

    @Format("#0.00")
    @Caption(name = "Prepayments")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> prepayments();

    /** {@link Arrears#arBalance()} - {@link Arrears#prepayments() } */
    @Format("#0.00")
    @Caption(name = "Total Balance")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalBalance();

}
