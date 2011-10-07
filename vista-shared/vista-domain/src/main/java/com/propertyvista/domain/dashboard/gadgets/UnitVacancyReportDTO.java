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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.Address;

public interface UnitVacancyReportDTO extends IEntity {

    // TODO ask Vlad about @Translatable and @XMLType
    public enum VacancyStatus {
        Vacant, Notice;
    }

    public enum RentedStatus {
        Rented, Unrented, OffMarket;
    }

    @Caption(name = "Property")
    IPrimitive<String> propertyCode();

    IPrimitive<String> buildingName();

    // TODO change to normal address (street number + city)
    Address address();

    // TODO ? region()

    IPrimitive<String> owner();

    IPrimitive<String> propertyManager();

    IPrimitive<String> complexName();

    // AptUnitInfo.number
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
    IPrimitive<Boolean> isRentReady();

    IPrimitive<Double> unitRent();

    IPrimitive<Double> unitMarketRent();

    /** <code>unitRent()</code> - <code>unitMarketRent()</code> */
    @Caption(name = "Delta, in $")
    IPrimitive<Double> rentDeltaAbsolute();

    /** (unitRent() - unitMarketRent())/unitMarketRent() */
    @Caption(name = "Delta, in %")
    IPrimitive<Double> rentDeltaRelative();

    /** @see AptUnit.availableForRent() - 1 */
    IPrimitive<LogicalDate> moveOutDay();

    /** Applicable only for rented */
    IPrimitive<LogicalDate> moveInDay();

    /** Applicable only for rented; maybe different than move out date */
    IPrimitive<LogicalDate> rentedFromDate();

    /** For Vacant units numberOfDays between today and availableForRent date */
    IPrimitive<Integer> daysVacant();

    /** days vacant * marketRent / 30 */
    @Caption(name = "Revenue Lost, in $")
    IPrimitive<Double> revenueLost();
}
