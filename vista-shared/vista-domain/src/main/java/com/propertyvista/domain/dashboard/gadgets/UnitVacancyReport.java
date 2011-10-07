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
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

// TODO don't forget to rename to DTO and enable the @Transient annotation when it's ready 
// @Transient
public interface UnitVacancyReport extends IEntity {

    // TODO ask Vlad about @Translatable and @XMLType
    public enum VacancyStatus {
        Vacant, Notice;
    }

    public enum RentedStatus {
        Rented, Unrented, OffMarket;
    }

    public enum RentReady {
        RentReady, RenoInProgress, NeedRepairs
    }

    @Caption(name = "Property")
    IPrimitive<String> propertyCode();

    IPrimitive<String> buildingName();

    // TODO change to normal Address Entity
    IPrimitive<String> address();

    IPrimitive<String> region();

    IPrimitive<String> owner();

    IPrimitive<String> propertyManager();

    IPrimitive<String> complexName();

    // AptUnitInfo.number
    // supposed to be String
    IPrimitive<String> unit();

    IPrimitive<String> floorplanName();

    IPrimitive<String> floorplanMarketingName();

    @Caption(name = "Vacant/Notice")
    IPrimitive<VacancyStatus> vacancyStatus();

    @Caption(name = "Rented/Unrented/OffMarket")
    IPrimitive<RentedStatus> rentedStatus();

    @Caption(name = "Scoping")
    IPrimitive<Boolean> isScoped();

    @Caption(name = "Physical Condition")
    IPrimitive<RentReady> rentReady();

    IPrimitive<Double> unitRent();

    IPrimitive<Double> marketRent();

    /** <code>unitRent()</code> - <code>unitMarketRent()</code> */
    @Transient
    @Caption(name = "Delta, in $")
    IPrimitive<Double> rentDeltaAbsolute();

    /** (unitRent() - unitMarketRent())/unitMarketRent() */
    @Transient
    @Caption(name = "Delta, in %")
    @Format("#0.00")
    IPrimitive<Double> rentDeltaRelative();

    /** @see AptUnit.availableForRent() - 1 */
    IPrimitive<LogicalDate> moveOutDay();

    /** Applicable only for rented */
    IPrimitive<LogicalDate> moveInDay();

    /** Applicable only for rented; maybe different than move out date */
    IPrimitive<LogicalDate> rentedFromDate();

    /** For Vacant units numberOfDays between today and availableForRent date */
    @Transient
    IPrimitive<Integer> daysVacant();

    /** days vacant * marketRent / 30 */
    @Transient
    @Caption(name = "Revenue Lost, in $")
    @Format("#0.00")
    IPrimitive<Double> revenueLost();
}
