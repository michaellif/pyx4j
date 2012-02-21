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

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.gadgets.CommonGadgetColumns;
import com.propertyvista.domain.dashboard.gadgets.util.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.util.CustomComparator;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;

// TODO probably also must include Tenant Name/Contact Information
public interface UnitAvailabilityStatus extends IEntity {

    public enum Vacancy {
        Vacant,

        Notice;

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

    public enum RentReadiness {
        RentReady, RenoInProgress, NeedsRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum Scoping {
        Scoped, Unscoped;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @EmbeddedEntity
    CommonGadgetColumns common();

    @ReadOnly
    AptUnit unit();

    /** This is optimization to avoid additional join while we answer queries */
    @ReadOnly
    Building building();

    @ReadOnly
    Floorplan floorplan();

    @ReadOnly
    Complex complex();

    // REFERENCED DATA
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> propertyCode();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> buildingName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> address();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> region();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> complexName();

    /** {@link AptUnit#info()} -> {@link AptUnitInfo#number()} */
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> unitName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> floorplanName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> floorplanMarketingName();

    // STATUS DATA    
    IPrimitive<LogicalDate> statusDate();

    @Caption(name = "Vacant/Notice")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Vacancy> vacancyStatus();

    @Caption(name = "Rented/Unrented/OffMarket")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<RentedStatus> rentedStatus();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<Scoping> scoping();

    @Caption(name = "Physical Condition")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<RentReadiness> rentReadinessStatus();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> unitRent();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> marketRent();

    /** <code>{@link #unitRent()} - {@link #unitMarketRent()} </code> */
    @Caption(name = "Delta, in $")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rentDeltaAbsolute();

    /** <code>({@link #unitRent()} - {@link #unitMarketRent()})/{@link #unitMarketRent()}</code> */
    @Caption(name = "Delta, in %")
    @Format("#0.00")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<BigDecimal> rentDeltaRelative();

    /** {@link AptUnit#availableForRent()} - 1 */
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LogicalDate> moveOutDay();

    /**
     * Applicable only for rented
     * 
     * @deprecated use {@link #rentedFromDate()} instead
     */
    @Deprecated
    @Format("MM/dd/yyyy")
    @CustomComparator(clazz = ComparableComparator.class)
    @Caption(name = "Move In Date")
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
