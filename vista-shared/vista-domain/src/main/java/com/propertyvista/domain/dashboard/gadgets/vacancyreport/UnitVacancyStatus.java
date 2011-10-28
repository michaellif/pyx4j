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
package com.propertyvista.domain.dashboard.gadgets.vacancyreport;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.gadgets.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;
import com.propertyvista.domain.property.asset.unit.AptUnit;

// TODO don't forget to rename to DTO and enable the @Transient annotation when it's ready 
// @Transient
public interface UnitVacancyStatus extends IEntity {

    // TODO ask Vlad about @Translatable and @XMLType
    public enum VacancyStatus {
        Vacant, Notice;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum RentedStatus {
        Rented, Unrented, OffMarket;
        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum RentReady {
        RentReady, RenoInProgress, NeedRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
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

    @Transient
    @Caption(name = "Vacant/Notice")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<VacancyStatus> vacancyStatus();

    @Transient
    @Caption(name = "Rented/Unrented/OffMarket")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<RentedStatus> rentedStatus();

    @Transient
    @Caption(name = "Scoping")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Boolean> isScoped();

    @Transient
    @Caption(name = "Physical Condition")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<RentReady> rentReady();

    IPrimitive<Double> unitRent();

    IPrimitive<Double> marketRent();

    /** <code>{@link #unitRent()} - {@link #unitMarketRent()} </code> */
    @Transient
    @Caption(name = "Delta, in $")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> rentDeltaAbsolute();

    /** <code>({@link #unitRent()} - {@link #unitMarketRent()})/{@link #unitMarketRent()}</code> */
    @Transient
    @Caption(name = "Delta, in %")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> rentDeltaRelative();

    /** {@link AptUnit#availableForRent()} - 1 */
    @Transient
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> moveOutDay();

    /** Applicable only for rented */
    @Transient
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> moveInDay();

    /** Applicable only for rented; maybe different than move out date */
    @Transient
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> rentedFromDate();

    /** For Vacant units numberOfDays between today and availableForRent date */
    @Transient
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Integer> daysVacant();

    /** days vacant * marketRent / 30 */
    @Transient
    @Caption(name = "Revenue Lost, in $")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> revenueLost();

    /** this is hack to use the lister service interface */
    @Transient
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> fromDate();

    /** this is hack to use the lister service interface */
    @Transient
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> toDate();

}
