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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.gadgets.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;

// TODO probably also must include Tenant Name/Contact Information
public interface UnitAvailabilityStatus extends IEntity {

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

    public enum RentReadinessStatus {
        RentReady, RenoInProgress, NeedsRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<LogicalDate> statusDate();

    @Owner
    @ReadOnly
    @Detached
    AptUnit belongsTo();

    /** This is optimization to avoid additional join while we answer queries */
    @ReadOnly
    @Detached
    Building buildingBelongsTo();

    IPrimitive<String> propertyCode();

    IPrimitive<String> buildingName();

    IPrimitive<String> address();

    IPrimitive<String> region();

    // TODO not clear where from to get the value of owner() property
    IPrimitive<String> owner();

    IPrimitive<String> propertyManagerName();

    IPrimitive<String> complexName();

    /** {@link AptUnit#info()} -> {@link AptUnitInfo#number()} */
    IPrimitive<String> unit();

    IPrimitive<String> floorplanName();

    IPrimitive<String> floorplanMarketingName();

    @Caption(name = "Vacant/Notice")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<VacancyStatus> vacancyStatus();

    @Caption(name = "Rented/Unrented/OffMarket")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<RentedStatus> rentedStatus();

    @Caption(name = "Scoping")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Boolean> isScoped();

    @Caption(name = "Physical Condition")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<RentReadinessStatus> rentReadinessStatus();

    IPrimitive<Double> unitRent();

    IPrimitive<Double> marketRent();

    /** <code>{@link #unitRent()} - {@link #unitMarketRent()} </code> */
    @Caption(name = "Delta, in $")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> rentDeltaAbsolute();

    /** <code>({@link #unitRent()} - {@link #unitMarketRent()})/{@link #unitMarketRent()}</code> */
    @Caption(name = "Delta, in %")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Double> rentDeltaRelative();

    /** {@link AptUnit#availableForRent()} - 1 */
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> moveOutDay();

    /** Applicable only for rented */
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> moveInDay();

    /** Applicable only for rented; maybe different than move out date */
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> rentedFromDate();

    /**
     * Used to speed up the {@link UnitAvailabilityStatusDTO#daysVacant()} computation. Should be equal to <code>{@link #moveOutDay()} + 1</code> or actually
     * {@link AptUnit#availableForRent()}
     */
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> availableFromDay();

}
