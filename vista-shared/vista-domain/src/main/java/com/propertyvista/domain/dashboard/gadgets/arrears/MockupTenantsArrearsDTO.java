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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;

@Transient
public interface MockupTenantsArrearsDTO extends MockupTenant {
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> propertyCode();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> unit();

    @Caption(name = "0 - 30 Days")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> arrears1MonthAgo();

    @Caption(name = "30 - 60 Days")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> arrears2MonthsAgo();

    @Caption(name = "60 -90 Days")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> arrears3MonthsAgo();

    @Caption(name = "Over 90 Days")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> arrears4MonthsAgo();
}
