/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

/**
 * Represents mockup of pre-comuptated arrears of a tenant on a specific date.
 * 
 * @author artyom
 * 
 */
public interface MockupArrear extends IEntity {
    // the unit, building, and tenant references here are for search optimization purposes    
    @Detached
    @ReadOnly
    AptUnit unit();

    @Detached
    @ReadOnly
    Building building();

    @Owner
    @Detached
    @ReadOnly
    MockupTenant belongsTo();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> propertyCode();

    @Caption(name = "Unit")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> unitNumber();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> firstName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> lastName();

    /**
     * The time when the status has been taken.
     */
    IPrimitive<LogicalDate> statusTimestamp();

    @Format("#0.00")
    @Caption(name = "0 - 30 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> monthAgo();

    @Format("#0.00")
    @Caption(name = "30 - 60 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> twoMonthsAgo();

    @Format("#0.00")
    @Caption(name = "60- 90 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> threeMonthsAgo();

    @Format("#0.00")
    @Caption(name = "Over 90 Days")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> overFourMonthsAgo();

    @Format("#0.00")
    @Caption(name = "AR Balance")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> arBalance();

    @Format("#0.00")
    @Caption(name = "Prepayments")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> prepayments();

    /** {@link MockupArrear#arBalance()} - {@link MockupArrear#prepayments() } */
    @Format("#0.00")
    @Caption(name = "Total Balance")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> totalBalance();
}
