/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 5, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.availabilityreport;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;

@Transient
public interface UnitAvailabilityStatusDTO extends UnitAvailabilityStatus {
    @Caption(name = "Days Vacant")
    /** For Vacant units numberOfDays between today and availableForRent date */
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Integer> daysVacant();

    /** days vacant * marketRent / 30 */
    @Caption(name = "Revenue Lost ($)")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> revenueLost();
}
